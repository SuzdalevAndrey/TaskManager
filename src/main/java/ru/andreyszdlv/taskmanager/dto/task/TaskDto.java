package ru.andreyszdlv.taskmanager.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.andreyszdlv.taskmanager.dto.comment.CommentDto;
import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "DTO для задачи, включая её данные, статус и комментарии")
public record TaskDto(
        @Schema(description = "Идентификатор задачи", example = "1")
        long id,

        @Schema(description = "Заголовок задачи", example = "Сделать отчет")
        String title,

        @Schema(description = "Описание задачи", example = "Необходимо подготовить отчет по проекту")
        String description,

        @Schema(description = "Статус задачи", allowableValues = {"WAITING", "IN_PROGRESS", "COMPLETED"}, example = "WAITING")
        TaskStatus status,

        @Schema(description = "Приоритет задачи", allowableValues = {"HIGH", "MEDIUM", "LOW"}, example = "HIGH")
        TaskPriority priority,

        @Schema(description = "Дата и время создания задачи", example = "2024-12-12T12:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Идентификатор автора задачи", example = "2")
        long authorId,

        @Schema(description = "Идентификатор исполнителя задачи", example = "3")
        Long assigneeId,

        @Schema(description = "Комментарии к задаче")
        List<CommentDto> comments
) {
}
