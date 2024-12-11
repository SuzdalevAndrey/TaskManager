package ru.andreyszdlv.taskmanager.dto.comment;

import java.time.LocalDateTime;

public record CommentDto(
        long id,

        String content,

        LocalDateTime createdAt,

        long authorId
) {
}
