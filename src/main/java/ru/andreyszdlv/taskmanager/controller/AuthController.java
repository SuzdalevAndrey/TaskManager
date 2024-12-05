package ru.andreyszdlv.taskmanager.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.andreyszdlv.taskmanager.dto.*;
import ru.andreyszdlv.taskmanager.service.AuthService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDto> registerUser(
            @Valid @RequestBody RegisterUserRequestDto registerUserRequestDto,
            BindingResult bindingResult) throws BindException {
        log.info("Registering user with email: {}", registerUserRequestDto.email());

        if (bindingResult.hasErrors()) {
            log.error("Validation error: {}", bindingResult.getAllErrors());

            throw new BindException(bindingResult);
        }

        log.info("Validation successfully registering user with email: {}", registerUserRequestDto.email());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.registerUser(registerUserRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto,
                                                      BindingResult bindingResult) throws BindException {
        log.info("Login user with email: {}", loginRequestDto.email());

        if (bindingResult.hasErrors()) {
            log.error("Validation error: {}", bindingResult.getAllErrors());

            throw new BindException(bindingResult);
        }

        log.info("Validation successfully login user with email: {}", loginRequestDto.email());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.loginUser(loginRequestDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDto requestDto,
            BindingResult bindingResult) throws BindException {
        log.info("Refreshing token");

        if (bindingResult.hasErrors()) {
            log.error("Validation error: {}", bindingResult.getAllErrors());

            throw new BindException(bindingResult);
        }

        log.info("Validation successfully refresh token");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.refreshToken(requestDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @NotBlank(message = "{validation.error.refresh_token.is_empty}") @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            BindingResult bindingResult) throws BindException {

        if (bindingResult.hasErrors()) {
            log.error("Validation error: {}", bindingResult.getAllErrors());

            throw new BindException(bindingResult);
        }

        authService.logout(accessToken);

        return ResponseEntity.noContent().build();
    }
}
