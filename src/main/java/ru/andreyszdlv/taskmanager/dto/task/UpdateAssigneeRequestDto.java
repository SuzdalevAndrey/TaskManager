package ru.andreyszdlv.taskmanager.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO для обновления исполнителя задачи")
public record UpdateAssigneeRequestDto(
        @NotNull(message = "{validation.error.task.assigneeId.is_null}")
        @Min(value = 1, message = "{validation.error.task.assigneeId.invalid}")
        @Schema(description = "Идентификатор нового исполнителя задачи", example = "3")
        Long assigneeId
) {
}
