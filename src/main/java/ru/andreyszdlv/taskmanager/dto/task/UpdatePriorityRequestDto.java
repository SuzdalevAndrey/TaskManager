package ru.andreyszdlv.taskmanager.dto.task;

import jakarta.validation.constraints.NotNull;
import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.validation.ValueOfEnum;

public record UpdatePriorityRequestDto(
        @NotNull(message = "{validation.error.task.priority.is_empty}")
        @ValueOfEnum(enumClass = TaskPriority.class, message = "{validation.error.task.priority.invalid}")
        String priority
) {
}
