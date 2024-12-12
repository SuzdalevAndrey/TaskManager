package ru.andreyszdlv.taskmanager.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;
import ru.andreyszdlv.taskmanager.validation.ValueOfEnum;

@Schema(description = "DTO для фильтрации задач")
public record TaskFilterDto(

        @ValueOfEnum(enumClass = TaskStatus.class, message = "{validation.error.task.status.invalid}")
        @Schema(description = "Статус задачи для фильтрации", allowableValues = {"WAITING", "IN_PROGRESS", "COMPLETED"}, example = "WAITING")
        String status,

        @ValueOfEnum(enumClass = TaskPriority.class, message = "{validation.error.task.priority.invalid}")
        @Schema(description = "Приоритет задачи для фильтрации", allowableValues = {"HIGH", "MEDIUM", "LOW"}, example = "HIGH")
        String priority,

        @Schema(description = "Идентификатор автора для фильтрации", example = "2")
        Long authorId,

        @Schema(description = "Идентификатор исполнителя для фильтрации", example = "3")
        Long assigneeId,

        @Min(value = 0, message = "{validation.error.page.count.invalid}")
        @Schema(description = "Номер страницы для пагинации", example = "0")
        Integer page,

        @Min(value = 1, message = "{validation.error.page.size.invalid}")
        @Schema(description = "Размер страницы для пагинации", example = "10")
        Integer size
) {
    public TaskFilterDto {
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;
    }
}