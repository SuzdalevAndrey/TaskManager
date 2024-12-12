package ru.andreyszdlv.taskmanager.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Ответ на запрос входа пользователя с токенами доступа и обновления")
public record LoginResponseDto(

        @Schema(description = "Токен доступа", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHBpcmVkVG9rZW4iOiJKV1QiLCJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MzEyMzQxMjh9.Qkp5z2T")
        String accessToken,

        @Schema(description = "Токен обновления", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHBpcmVkVG9rZW4iOiJKV1QiLCJ1c2VyIjoiYWRtaW4iLCJpYXQiOjE2MzEyMzQxMjh9.Qkp5z2T")
        String refreshToken
) {
}
