package ru.andreyszdlv.taskmanager.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(

        @NotBlank(message = "{validation.error.user.email.is_empty}")
        @Email(message = "{validation.error.user.email.invalid}")
        String email,

        @NotBlank(message = "{validation.error.user.password.is_empty}")
        @Size(min = 6, message = "{validation.error.user.password.invalid}")
        String password
) {
}
