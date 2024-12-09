package ru.andreyszdlv.taskmanager.dto.task;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;
import ru.andreyszdlv.taskmanager.validation.NotBlankIfPresent;

public record CreateTaskRequestDto(

        @Size(max = 255, message = "{validation.error.task.title.size.invalid}")
        @NotBlank(message = "{validation.error.task.title.is_empty}")
        String title,

        @Size(max = 1000, message = "{validation.error.task.title.size.invaid}")
        @NotBlankIfPresent(message = "{validation.error.task.description.not_blank}")
        String description,

        @NotNull(message = "{validation.error.task.priority.is_empty}")
        TaskPriority priority,

        @NotNull(message = "{validation.error.task.status.is_empty}")
        TaskStatus status,

        @Min(value = 1, message = "{validation.error.task.assigneeId.invalid}")
        Long assigneeId
) { }
