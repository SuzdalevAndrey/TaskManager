package ru.andreyszdlv.taskmanager.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserRequestDto(

        @NotBlank(message = "{validation.error.user.name.is_empty}")
        @Size(min = 2, max = 50, message = "{validation.error.user.name.size.invalid}")
        String name,

        @NotBlank(message = "{validation.error.user.email.is_empty}")
        @Email(message = "{validation.error.user.email.invalid}")
        @Size(max = 255, message = "{validation.error.user.email.size.invalid}")
        String email,

        @NotBlank(message = "{validation.error.user.password.is_empty}")
        @Size(min = 6, max = 100, message = "{validation.error.user.password.size.invalid}")
        String password
) {
}
