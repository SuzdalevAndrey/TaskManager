package ru.andreyszdlv.taskmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.taskmanager.dto.LoginRequestDto;
import ru.andreyszdlv.taskmanager.dto.LoginResponseDto;
import ru.andreyszdlv.taskmanager.dto.RegisterUserRequestDto;
import ru.andreyszdlv.taskmanager.dto.RegisterUserResponseDto;
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

            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }

        log.info("Validation successfully registering user with email: {}", registerUserRequestDto.email());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.registerUser(registerUserRequestDto));
    }

}
