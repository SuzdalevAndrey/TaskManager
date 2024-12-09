package ru.andreyszdlv.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.taskmanager.dto.comment.CommentDto;
import ru.andreyszdlv.taskmanager.dto.comment.CreateCommentRequestDto;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.exception.CommentNotFoundException;
import ru.andreyszdlv.taskmanager.mapper.CommentMapper;
import ru.andreyszdlv.taskmanager.model.Comment;
import ru.andreyszdlv.taskmanager.model.Task;
import ru.andreyszdlv.taskmanager.repository.CommentRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final TaskService taskService;

    private final UserService userService;

    private final SecurityContextService securityContextService;

    @Transactional
    public CommentDto createComment(long taskId, CreateCommentRequestDto requestDto) {

        Task task = taskService.getTaskByIdOrElseThrow(taskId);

        if(securityContextService.getCurrentUserRole() == Role.USER
                && !task.getAssignee().getEmail().equals(securityContextService.getCurrentUserName())) {
            throw new RuntimeException();
        }

        Comment comment = commentMapper.toComment(requestDto);
        comment.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        comment.setAuthor(userService.getUserOrElseThrow(securityContextService.getCurrentUserName()));
        comment.setTask(task);

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(long commentId) {
        Comment comment = this.getCommentByIdOrElseThrow(commentId);

        if(securityContextService.getCurrentUserRole() == Role.USER
                && !comment.getAuthor().getEmail().equals(securityContextService.getCurrentUserName())) {
            throw new RuntimeException();
        }

        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAllCommentsTaskByTaskId(long taskId) {
        Task task = taskService.getTaskByIdOrElseThrow(taskId);

        if(securityContextService.getCurrentUserRole() == Role.USER
                && !task.getAssignee().getEmail().equals(securityContextService.getCurrentUserName())) {
            throw new RuntimeException();
        }

        return task.getComments().stream().map(commentMapper::toCommentDto).toList();
    }

    @Transactional(readOnly = true)
    public Comment getCommentByIdOrElseThrow(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                ()->new CommentNotFoundException("error.404.comment.not_found")
        );
    }
}
