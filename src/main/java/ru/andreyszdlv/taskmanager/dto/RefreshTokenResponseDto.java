package ru.andreyszdlv.taskmanager.dto;

import lombok.Builder;

@Builder
public record RefreshTokenResponseDto(String accessToken, String refreshToken) {
}
