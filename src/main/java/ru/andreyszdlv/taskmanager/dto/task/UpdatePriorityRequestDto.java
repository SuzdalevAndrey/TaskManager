package ru.andreyszdlv.taskmanager.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.validation.ValueOfEnum;

@Schema(description = "DTO для обновления приоритета задачи")
public record UpdatePriorityRequestDto(
        @NotNull(message = "{validation.error.task.priority.is_empty}")
        @ValueOfEnum(enumClass = TaskPriority.class, message = "{validation.error.task.priority.invalid}")
        @Schema(description = "Приоритет задачи", allowableValues = {"LOW", "MEDIUM", "HIGH"}, example = "HIGH")
        String priority
) {
}
