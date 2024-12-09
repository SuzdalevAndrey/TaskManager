package ru.andreyszdlv.taskmanager.dto.task;

import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;

import java.time.LocalDateTime;

public record UpdateTaskResponseDto(
        long id,

        String title,

        String description,

        TaskStatus status,

        TaskPriority priority,

        LocalDateTime createdAt
) {
}
