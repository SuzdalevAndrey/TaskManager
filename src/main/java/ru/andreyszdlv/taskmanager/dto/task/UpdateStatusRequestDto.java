package ru.andreyszdlv.taskmanager.dto.task;


import jakarta.validation.constraints.NotNull;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;
import ru.andreyszdlv.taskmanager.validation.ValueOfEnum;

public record UpdateStatusRequestDto(
        @NotNull(message = "{validation.error.task.status.is_empty}")
        @ValueOfEnum(enumClass = TaskStatus.class, message = "{validation.error.task.status.invalid}")
        String status
) {
}
