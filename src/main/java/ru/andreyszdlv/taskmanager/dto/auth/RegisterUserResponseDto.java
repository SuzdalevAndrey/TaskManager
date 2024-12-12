package ru.andreyszdlv.taskmanager.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ на запрос регистрации пользователя с его данными")
public record RegisterUserResponseDto(

        @Schema(description = "Идентификатор пользователя", example = "1")
        long id,

        @Schema(description = "Имя пользователя", example = "Иван иванов")
        String name,

        @Schema(description = "Электронная почта пользователя", example = "user@example.com")
        String email
) {
}
