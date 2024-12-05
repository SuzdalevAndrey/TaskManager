package ru.andreyszdlv.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(
        @NotBlank(message = "{validation.error.refresh_token.is_empty}")
        String refreshToken
) {
}
