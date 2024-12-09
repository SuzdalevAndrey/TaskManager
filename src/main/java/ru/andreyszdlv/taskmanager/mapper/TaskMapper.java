package ru.andreyszdlv.taskmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andreyszdlv.taskmanager.dto.task.*;
import ru.andreyszdlv.taskmanager.model.Task;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    CreateTaskResponseDto toCreateTaskResponseDto(Task task);

    Task toTask(CreateTaskRequestDto createTaskRequestDto);

    @Mapping(target = "assigneeId", source = "assignee.id")
    TaskDto toTaskDto(Task task);

    UpdateTaskResponseDto toUpdateTaskResponseDto(Task task);

    @Mapping(target = "assigneeId", source = "assignee.id")
    UpdateTaskPartialResponseDto toUpdateTaskPartialResponseDto(Task task);
}
