package ru.andreyszdlv.taskmanager.dto.task;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;
import ru.andreyszdlv.taskmanager.validation.NotBlankIfPresent;

public record UpdateTaskPartialRequestDto(
        @Size(max = 255, message = "{validation.error.task.title.size.invalid}")
        @NotBlankIfPresent(message = "{validation.error.task.title.not_blank}")
        String title,

        @Size(max = 1000, message = "{validation.error.task.title.size.invalid}")
        @NotBlankIfPresent(message = "{validation.error.task.description.not_blank}")
        String description,

        TaskPriority priority,

        TaskStatus status,

        @Min(value = 1, message = "{validation.error.task.assigneeId.invalid}")
        Long assigneeId
) {
}
