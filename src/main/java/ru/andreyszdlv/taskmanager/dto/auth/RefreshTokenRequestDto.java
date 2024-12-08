package ru.andreyszdlv.taskmanager.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(
        @NotBlank(message = "{validation.error.refresh_token.is_empty}")
        String refreshToken
) {
}
