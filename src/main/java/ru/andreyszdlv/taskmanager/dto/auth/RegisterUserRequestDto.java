package ru.andreyszdlv.taskmanager.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для регистрации нового пользователя")
public record RegisterUserRequestDto(

        @NotBlank(message = "{validation.error.user.name.is_empty}")
        @Size(min = 2, max = 50, message = "{validation.error.user.name.size.invalid}")
        @Schema(description = "Имя пользователя", example = "Иван Иванов", minLength = 2, maxLength = 50)
        String name,

        @NotBlank(message = "{validation.error.user.email.is_empty}")
        @Email(message = "{validation.error.user.email.invalid}")
        @Size(max = 255, message = "{validation.error.user.email.size.invalid}")
        @Schema(description = "Электронная почта пользователя", example = "user@example.com", maxLength = 255)
        String email,

        @NotBlank(message = "{validation.error.user.password.is_empty}")
        @Size(min = 6, max = 100, message = "{validation.error.user.password.size.invalid}")
        @Schema(description = "Пароль пользователя", example = "password123", minLength = 6, maxLength = 100)
        String password
) {
}
