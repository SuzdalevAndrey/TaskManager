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
import ru.andreyszdlv.taskmanager.validation.RequestValidator;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    private final RequestValidator requestValidator;

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDto> registerUser(
            @Valid @RequestBody RegisterUserRequestDto requestDto,
            BindingResult bindingResult) throws BindException {
        log.info("Received request register user with email: {}", requestDto.email());

        requestValidator.validateRequest(bindingResult);

        RegisterUserResponseDto responseDto = authService.registerUser(requestDto);

        log.info("User with email: {} and id: {} registered successfully", responseDto.email(), responseDto.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@Valid @RequestBody LoginRequestDto requestDto,
                                                      BindingResult bindingResult) throws BindException {
        log.info("Received request login user with email: {}", requestDto.email());

        requestValidator.validateRequest(bindingResult);

        LoginResponseDto responseDto = authService.loginUser(requestDto);

        log.info("User with email: {} logged in successfully", requestDto.email());
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDto requestDto,
            BindingResult bindingResult) throws BindException {
        log.info("Received request refresh token");

        requestValidator.validateRequest(bindingResult);

        RefreshTokenResponseDto responseDto = authService.refreshToken(requestDto);

        log.info("Token refreshed successfully for user");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(){
        log.info("Received request logout user");

        authService.logout();

        log.info("User logged out successfully");
        return ResponseEntity.noContent().build();
    }
}
