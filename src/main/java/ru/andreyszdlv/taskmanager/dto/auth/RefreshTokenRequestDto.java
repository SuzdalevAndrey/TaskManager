package ru.andreyszdlv.taskmanager.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO для запроса на обновление токена пользователя")
public record RefreshTokenRequestDto(
        @NotBlank(message = "{validation.error.refresh_token.is_empty}")
        @Schema(description = "Refresh токен для обновления сессии")
        String refreshToken
) {
}
