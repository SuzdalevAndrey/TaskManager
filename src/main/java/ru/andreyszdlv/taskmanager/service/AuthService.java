package ru.andreyszdlv.taskmanager.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.taskmanager.dto.*;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.exception.InvalidTokenException;
import ru.andreyszdlv.taskmanager.mapper.UserMapper;
import ru.andreyszdlv.taskmanager.model.User;
import ru.andreyszdlv.taskmanager.repository.UserRepository;
import ru.andreyszdlv.taskmanager.util.SecurityUtils;
import ru.andreyszdlv.taskmanager.validator.JwtValidator;
import ru.andreyszdlv.taskmanager.validator.UserValidator;

@Service
@RequiredArgsConstructor
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
        userValidator.checkUserExists(registerUserRequestDto.email());

        User user = userMapper.toUser(registerUserRequestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        return userMapper.toRegisterUserResponseDto(userRepository.save(user));
    }

    @Transactional
    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.email(),
                        loginRequestDto.password()
                )
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = accessAndRefreshJwtService.generateAccessToken(user.getEmail(), user.getRole().name());

        String refreshToken = accessAndRefreshJwtService.generateRefreshToken(user.getEmail(), user.getRole().name());

        return new LoginResponseDto(accessToken, refreshToken);
    }

    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto requestDto) {
        String refreshToken = requestDto.refreshToken();

        jwtValidator.validateRefresh(refreshToken);

        String userEmail = jwtSecurityService.extractUserEmail(refreshToken);

        String role = jwtSecurityService.extractRole(refreshToken);

        String accessToken = accessAndRefreshJwtService.generateAccessToken(userEmail, role);

        return RefreshTokenResponseDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void logout() {

        String userEmail = SecurityUtils.getCurrentUserName();

        accessAndRefreshJwtService.deleteByUserId(userEmail);
    }
}
