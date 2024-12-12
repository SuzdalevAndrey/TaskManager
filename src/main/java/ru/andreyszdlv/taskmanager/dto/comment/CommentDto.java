package ru.andreyszdlv.taskmanager.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO для комментария, включая его ID, содержание, дату создания и ID автора")
public record CommentDto(

        @Schema(description = "Идентификатор комментария", example = "1")
        long id,

        @Schema(description = "Содержание комментария", example = "Это комментарий к задаче")
        String content,

        @Schema(description = "Дата и время создания комментария", example = "2024-12-12T12:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Идентификатор автора комментария", example = "2")
        long authorId
) {
}