package ru.andreyszdlv.taskmanager.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;
import ru.andreyszdlv.taskmanager.validation.NotBlankIfPresent;
import ru.andreyszdlv.taskmanager.validation.ValueOfEnum;

@Schema(description = "DTO для частичного обновления задачи")
public record UpdateTaskPartialRequestDto(
        @Size(max = 255, message = "{validation.error.task.title.size.invalid}")
        @NotBlankIfPresent(message = "{validation.error.task.title.not_blank}")
        @Schema(description = "Заголовок задачи", example = "Сделать отчет", maxLength = 255)
        String title,

        @Size(max = 1000, message = "{validation.error.task.title.size.invalid}")
        @NotBlankIfPresent(message = "{validation.error.task.description.not_blank}")
        @Schema(description = "Описание задачи", example = "Необходимо подготовить отчет по проекту", maxLength = 1000)
        String description,

        @ValueOfEnum(enumClass = TaskPriority.class, message = "{validation.error.task.priority.invalid}")
        @Schema(description = "Приоритет задачи", allowableValues = {"HIGH", "MEDIUM", "LOW"}, example = "HIGH")
        String priority,

        @ValueOfEnum(enumClass = TaskStatus.class, message = "{validation.error.task.status.invalid}")
        @Schema(description = "Статус задачи", allowableValues = {"WAITING", "IN_PROGRESS", "COMPLETED"}, example = "WAITING")
        String status,

        @Min(value = 1, message = "{validation.error.task.assigneeId.invalid}")
        @Schema(description = "Идентификатор исполнителя задачи", example = "5")
        Long assigneeId
) {
}
