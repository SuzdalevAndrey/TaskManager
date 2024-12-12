package ru.andreyszdlv.taskmanager.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.andreyszdlv.taskmanager.enums.Role;

@Schema(description = "DTO для пользователя")
public record UserDto(

        @Schema(description = "Уникальный идентификатор пользователя", example = "1")
        long id,

        @Schema(description = "Имя пользователя", example = "Иван Иванов")
        String name,

        @Schema(description = "Электронная почта пользователя", example = "user@example.com")
        String email,

        @Schema(description = "Роль пользователя", allowableValues = {"USER", "ADMIN"}, example = "USER")
        Role role
) {
}
