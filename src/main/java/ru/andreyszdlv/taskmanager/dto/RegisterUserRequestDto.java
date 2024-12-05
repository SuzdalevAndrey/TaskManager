package ru.andreyszdlv.taskmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequestDto(

        @NotBlank(message = "{validation.error.user.name.is_empty}")
        String name,

        @NotBlank(message = "{validation.error.user.email.is_empty}")
        @Email(message = "validation.error.user.email.invalid")
        String email,

        @NotBlank(message = "{validation.error.user.password.is_empty}")
        String password
) {
}
