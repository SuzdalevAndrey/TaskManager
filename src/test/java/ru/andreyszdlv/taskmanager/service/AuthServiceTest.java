package ru.andreyszdlv.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.andreyszdlv.taskmanager.dto.*;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.exception.InvalidRefreshTokenException;
import ru.andreyszdlv.taskmanager.exception.UserAlreadyExsitsException;
import ru.andreyszdlv.taskmanager.exception.UserUnauthenticatedException;
import ru.andreyszdlv.taskmanager.mapper.UserMapper;
import ru.andreyszdlv.taskmanager.model.User;
import ru.andreyszdlv.taskmanager.repository.UserRepository;
import ru.andreyszdlv.taskmanager.validator.JwtValidator;
import ru.andreyszdlv.taskmanager.validator.UserValidator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserValidator userValidator;

    @Mock
    UserMapper userMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtExtractorService jwtExtractorService;

    @Mock
    JwtStorageService jwtStorageService;

    @Mock
    JwtValidator jwtValidator;

    @Mock
    SecurityContextService securityContextService;

    @InjectMocks
    AuthService authService;

    @BeforeEach
    void setUp() {MockitoAnnotations.openMocks(this);}

    @Test
    void registerUser_Success_WhenUserNotExists() {
        String name = "name";
        String email = "test@test.ru";
        User newUser = mock(User.class);
        User savedUser = mock(User.class);
        RegisterUserRequestDto requestDto = new RegisterUserRequestDto(name, email, "000000");
        RegisterUserResponseDto expectedResponseDto = new RegisterUserResponseDto(1L, name, email);
        when(userMapper.toUser(requestDto)).thenReturn(newUser);
        when(userRepository.save(newUser)).thenReturn(savedUser);
        when(userMapper.toRegisterUserResponseDto(savedUser)).thenReturn(expectedResponseDto);

        RegisterUserResponseDto responseDto = authService.registerUser(requestDto);

        assertNotNull(responseDto);
        assertEquals(expectedResponseDto, responseDto);
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void registerUser_ThrowsException_WhenUserAlreadyExists() {
        RegisterUserRequestDto requestDto = new RegisterUserRequestDto("name", "email", "000000");
        doThrow(UserAlreadyExsitsException.class).when(userValidator).checkUserExists(requestDto.email());

        assertThrows(
                UserAlreadyExsitsException.class,
                ()->authService.registerUser(requestDto)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_Success_WhenUserAuthenticated() {
        String email = "test@test.ru";
        String password = "000000";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(Role.USER);
        LoginRequestDto requestDto = new LoginRequestDto(email, password);
        LoginResponseDto expectedResponseDto=new LoginResponseDto(accessToken, refreshToken);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtStorageService.generateAccessToken(user.getEmail(), user.getRole())).thenReturn(accessToken);
        when(jwtStorageService.generateRefreshToken(user.getEmail(), user.getRole())).thenReturn(refreshToken);

        LoginResponseDto responseDto = authService.loginUser(requestDto);

        assertNotNull(responseDto);
        assertEquals(expectedResponseDto, responseDto);
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        verify(jwtStorageService, times(1))
                .generateAccessToken(user.getEmail(), user.getRole());
        verify(jwtStorageService, times(1))
                .generateRefreshToken(user.getEmail(), user.getRole());
    }

    @Test
    void loginUser_ThrowsException_WhenUserUnauthenticated() {
        String email = "test@test.ru";
        String password = "000000";
        Role role = Role.USER;
        LoginRequestDto requestDto = new LoginRequestDto(email, password);
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException(""));

        assertThrows(
                UserUnauthenticatedException.class,
                ()->authService.loginUser(requestDto)
        );

        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        verify(jwtStorageService, never()).generateAccessToken(email, role);
        verify(jwtStorageService, never()).generateRefreshToken(email, role);
    }

    @Test
    void refreshToken_Success_WhenTokenValid(){
        String refreshToken = "refreshToken";
        String accessToken = "accessToken";
        String email = "test@test.ru";
        Role role = Role.USER;
        RefreshTokenRequestDto requestDto = new RefreshTokenRequestDto(refreshToken);
        when(jwtExtractorService.extractUserEmail(refreshToken)).thenReturn(email);
        when(jwtExtractorService.extractRole(refreshToken)).thenReturn(role);
        when(jwtStorageService.generateAccessToken(email, role)).thenReturn(accessToken);
        RefreshTokenResponseDto expectedResponseDto = new RefreshTokenResponseDto(accessToken, refreshToken);

        RefreshTokenResponseDto responseDto = authService.refreshToken(requestDto);

        assertNotNull(responseDto);
        assertEquals(expectedResponseDto, responseDto);
        verify(jwtExtractorService, times(1)).extractUserEmail(refreshToken);
        verify(jwtExtractorService, times(1)).extractRole(refreshToken);
        verify(jwtStorageService, times(1)).generateAccessToken(email, role);
    }

    @Test
    void refreshToken_ThrowsException_WhenTokenInvalid(){
        String refreshToken = "refreshToken";
        RefreshTokenRequestDto requestDto = new RefreshTokenRequestDto(refreshToken);
        doThrow(InvalidRefreshTokenException.class).when(jwtValidator).validateRefreshToken(refreshToken);

        assertThrows(
                InvalidRefreshTokenException.class,
                ()->authService.refreshToken(requestDto)
        );

        verify(jwtStorageService, never()).generateAccessToken(any(), any());
    }

    @Test
    void logout_Success_WhenUserAuthenticated() {
        String email = "test@test.ru";
        when(securityContextService.getCurrentUserName()).thenReturn(email);

        authService.logout();

        verify(jwtStorageService, times(1)).deleteByUserEmail(email);
    }

    @Test
    void logout_ThrowsException_WhenUserUnauthenticated() {
        when(securityContextService.getCurrentUserName()).thenThrow(UserUnauthenticatedException.class);

        assertThrows(
                UserUnauthenticatedException.class,
                ()->authService.logout()
        );

        verify(jwtStorageService, never()).deleteByUserEmail(anyString());
    }
}