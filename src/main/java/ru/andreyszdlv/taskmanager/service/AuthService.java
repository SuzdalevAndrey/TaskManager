package ru.andreyszdlv.taskmanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.taskmanager.dto.*;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.mapper.UserMapper;
import ru.andreyszdlv.taskmanager.model.User;
import ru.andreyszdlv.taskmanager.repository.UserRepository;
import ru.andreyszdlv.taskmanager.util.SecurityUtils;
import ru.andreyszdlv.taskmanager.validator.JwtValidator;
import ru.andreyszdlv.taskmanager.validator.UserValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    private final UserValidator userValidator;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtSecurityService jwtSecurityService;

    private final AccessAndRefreshJwtService accessAndRefreshJwtService;

    private final JwtValidator jwtValidator;

    @Transactional
    public RegisterUserResponseDto registerUser(RegisterUserRequestDto registerUserRequestDto) {
        log.info("Registering new user with email: {}", registerUserRequestDto.email());

        userValidator.checkUserExists(registerUserRequestDto.email());

        User user = userMapper.toUser(registerUserRequestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        log.info("User registered successfully with email: {}", registerUserRequestDto.email());
        return userMapper.toRegisterUserResponseDto(userRepository.save(user));
    }

    @Transactional
    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {
        log.info("User login for email: {}", loginRequestDto.email());

        Authentication authentication = authenticateUser(loginRequestDto);
        User user = (User) authentication.getPrincipal();

        String accessToken = accessAndRefreshJwtService.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = accessAndRefreshJwtService.generateRefreshToken(user.getEmail(), user.getRole().name());

        log.info("User login successfully with email: {}", loginRequestDto.email());
        return new LoginResponseDto(accessToken, refreshToken);
    }

    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto requestDto) {
        log.info("Refreshing token");

        String refreshToken = requestDto.refreshToken();

        jwtValidator.validateRefresh(refreshToken);

        String userEmail = jwtSecurityService.extractUserEmail(refreshToken);
        log.info("Extracted user email: {}", userEmail);

        String role = jwtSecurityService.extractRole(refreshToken);
        log.info("Extracted user role: {}", role);

        String accessToken = accessAndRefreshJwtService.generateAccessToken(userEmail, role);

        log.info("Token refresh successfully for user: {}", userEmail);
        return RefreshTokenResponseDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void logout() {
        String userEmail = SecurityUtils.getCurrentUserName();

        log.info("User with email {} logout successfully", userEmail);
        accessAndRefreshJwtService.deleteByUserEmail(userEmail);
    }

    private Authentication authenticateUser(LoginRequestDto loginRequestDto) {
        log.info("AuthenticateUser for email: {}", loginRequestDto.email());
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.email(),
                        loginRequestDto.password()
                )
        );
    }
}
