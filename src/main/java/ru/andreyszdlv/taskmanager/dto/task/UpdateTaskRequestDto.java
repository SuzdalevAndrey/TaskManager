package ru.andreyszdlv.taskmanager.dto.task;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;

public record UpdateTaskRequestDto(
        @Size(min = 1, max = 255, message = "{validation.error.task.title.size.invaid}")
        String title,

        @Size(max = 1000, message = "{validation.error.task.title.size.invaid}")
        String description,

        @NotNull(message = "{validation.error.task.priority.is_empty}")
        TaskPriority priority,

        @NotNull(message = "{validation.error.task.status.is_empty}")
        TaskStatus status,

        @Min(value = 1, message = "{validation.error.task.assigneeId.invalid}")
        Long assigneeId
) {
}
