package ru.andreyszdlv.taskmanager.dto.task;

import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;
import ru.andreyszdlv.taskmanager.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public record TaskDto(
        long id,

        String title,

        String description,

        TaskStatus status,

        TaskPriority priority,

        LocalDateTime createdAt,

        long assigneeId,

        List<Comment> comments
) {
}
