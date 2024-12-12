package ru.andreyszdlv.taskmanager.dto.task;

import jakarta.validation.constraints.Min;
import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;
import ru.andreyszdlv.taskmanager.validation.ValueOfEnum;

public record TaskFilterDto(

        @ValueOfEnum(enumClass = TaskStatus.class, message = "{validation.error.task.status.invalid}")
        String status,

        @ValueOfEnum(enumClass = TaskPriority.class, message = "{validation.error.task.priority.invalid}")
        String priority,

        Long authorId,

        Long assigneeId,

        @Min(value = 0, message = "{validation.error.page.count.invalid}")
        Integer page,

        @Min(value = 1, message = "{validation.error.page.size.invalid}")
        Integer size
) {
    public TaskFilterDto {
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;
    }
}