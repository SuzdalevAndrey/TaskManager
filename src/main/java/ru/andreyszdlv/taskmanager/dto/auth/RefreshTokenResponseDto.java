package ru.andreyszdlv.taskmanager.dto.auth;

import lombok.Builder;

@Builder
public record RefreshTokenResponseDto(String accessToken, String refreshToken) {
}
