package ru.andreyszdlv.taskmanager.dto.auth;

import lombok.Builder;

@Builder
public record LoginResponseDto(
        String accessToken,
        String refreshToken
) {
}
