package ru.andreyszdlv.taskmanager.dto.task;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;
import ru.andreyszdlv.taskmanager.validation.ValueOfEnum;

@Schema(description = "DTO для обновления статуса задачи")
public record UpdateStatusRequestDto(
        @NotNull(message = "{validation.error.task.status.is_empty}")
        @ValueOfEnum(enumClass = TaskStatus.class, message = "{validation.error.task.status.invalid}")
        @Schema(description = "Статус задачи", allowableValues = {"WAITING", "IN_PROGRESS", "COMPLETED"}, example = "WAITING")
        String status
) {
}
