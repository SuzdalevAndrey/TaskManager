package ru.andreyszdlv.taskmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andreyszdlv.taskmanager.dto.task.*;
import ru.andreyszdlv.taskmanager.model.Task;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = CommentMapper.class)
public interface TaskMapper {

    Task toTask(CreateTaskRequestDto createTaskRequestDto);

    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "comments", source = "comments")
    TaskDto toTaskDto(Task task);
}