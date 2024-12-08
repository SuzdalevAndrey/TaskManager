package ru.andreyszdlv.taskmanager.dto.task;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.andreyszdlv.taskmanager.enums.TaskPriority;

public record CreateTaskRequestDto(

        @Size(min = 1, max = 255, message = "{validation.error.task.title.size.invaid}")
        String title,

        @Size(max = 1000, message = "{validation.error.task.title.size.invaid}")
        String description,

        @NotNull(message = "{validation.error.task.priority.is_empty}")
        TaskPriority priority
) { }
