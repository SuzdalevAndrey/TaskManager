package ru.andreyszdlv.taskmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.andreyszdlv.taskmanager.dto.auth.*;
import ru.andreyszdlv.taskmanager.service.AuthService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDto> registerUser(
            @Valid @RequestBody RegisterUserRequestDto requestDto,
            BindingResult bindingResult) throws BindException {
        log.info("Registering user with email: {}", requestDto.email());

        if (bindingResult.hasErrors()) {
            log.error("Validation error: {}", bindingResult.getAllErrors());

            throw new BindException(bindingResult);
        }

        log.info("Validation successfully, registering user with email: {}", requestDto.email());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.registerUser(requestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@Valid @RequestBody LoginRequestDto requestDto,
                                                      BindingResult bindingResult) throws BindException {
        log.info("Login user with email: {}", requestDto.email());

        if (bindingResult.hasErrors()) {
            log.error("Validation error: {}", bindingResult.getAllErrors());

            throw new BindException(bindingResult);
        }

        log.info("Validation successfully, login user with email: {}", requestDto.email());
        return ResponseEntity.ok(authService.loginUser(requestDto));
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

        log.info("Validation successfully, refreshing token");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.refreshToken(requestDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(){
        log.info("Logout user");

        authService.logout();

        return ResponseEntity.noContent().build();
    }
}
