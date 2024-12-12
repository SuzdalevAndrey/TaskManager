package ru.andreyszdlv.taskmanager.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для запроса на авторизацию пользователя")
public record LoginRequestDto(

        @NotBlank(message = "{validation.error.user.email.is_empty}")
        @Email(message = "{validation.error.user.email.invalid}")
        @Schema(description = "Электронная почта пользователя", example = "user@example.com")
        String email,

        @NotBlank(message = "{validation.error.user.password.is_empty}")
        @Size(min = 6, message = "{validation.error.user.password.invalid}")
        @Schema(description = "Пароль пользователя", example = "password123")
        String password
) {
}
