package ru.andreyszdlv.taskmanager.dto.task;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateAssigneeRequestDto(
        @NotNull(message = "{validation.error.task.assigneeId.is_null}")
        @Min(value = 1, message = "{validation.error.task.assigneeId.invalid}")
        Long assigneeId
) {
}
