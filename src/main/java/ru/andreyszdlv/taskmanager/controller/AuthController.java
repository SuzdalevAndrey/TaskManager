package ru.andreyszdlv.taskmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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

    @Operation(
            summary = "Регистрация пользователя",
            description = "Эндпоинт для регистрации нового пользователя. Требуется передать имя, email и пароль.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterUserResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже зарегистрирован",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @SecurityRequirements
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

    @Operation(
            summary = "Авторизация пользователя",
            description = "Эндпоинт для авторизации пользователя с использованием email и пароля. Возвращает JWT токены.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно авторизован",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Неверный email или пароль",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@Valid @RequestBody LoginRequestDto requestDto,
                                                      BindingResult bindingResult) throws BindException {
        log.info("Received request login user with email: {}", requestDto.email());

        requestValidator.validateRequest(bindingResult);

        LoginResponseDto responseDto = authService.loginUser(requestDto);

        log.info("User with email: {} logged in successfully", requestDto.email());
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "Обновление токенов (refresh token)",
            description = "Эндпоинт для обновления JWT токенов. Требуется передать refresh token.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Токены успешно обновлены",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RefreshTokenResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Неверный или просроченный refresh token",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @SecurityRequirements
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

    @Operation(
            summary = "Выход пользователя",
            description = "Эндпоинт для выхода пользователя (удаление текущего токена).",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Пользователь успешно вышел"),
                    @ApiResponse(responseCode = "403", description = "Неавторизованный доступ",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(){
        log.info("Received request logout user");

        authService.logout();

        log.info("User logged out successfully");
        return ResponseEntity.noContent().build();
    }
}
