package ru.andreyszdlv.taskmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andreyszdlv.taskmanager.dto.comment.CommentDto;
import ru.andreyszdlv.taskmanager.dto.comment.CreateCommentRequestDto;
import ru.andreyszdlv.taskmanager.model.Comment;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "authorId", source = "author.id")
    CommentDto toCommentDto(Comment comment);

    Comment toComment(CreateCommentRequestDto createCommentRequestDto);

    List<CommentDto> toCommentDtos(List<Comment> comments);
}
