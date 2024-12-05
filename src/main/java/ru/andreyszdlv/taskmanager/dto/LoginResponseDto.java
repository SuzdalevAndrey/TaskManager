package ru.andreyszdlv.taskmanager.dto;

import lombok.Builder;

@Builder
public record LoginResponseDto(
        String accessToken,
        String refreshToken
) {
}
