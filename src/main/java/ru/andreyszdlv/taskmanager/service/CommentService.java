package ru.andreyszdlv.taskmanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.taskmanager.dto.comment.CommentDto;
import ru.andreyszdlv.taskmanager.dto.comment.CreateCommentRequestDto;
import ru.andreyszdlv.taskmanager.exception.CommentNotFoundException;
import ru.andreyszdlv.taskmanager.mapper.CommentMapper;
import ru.andreyszdlv.taskmanager.model.Comment;
import ru.andreyszdlv.taskmanager.model.Task;
import ru.andreyszdlv.taskmanager.repository.CommentRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final TaskService taskService;

    private final UserService userService;

    @Transactional
    public CommentDto createComment(long taskId, CreateCommentRequestDto requestDto) {
        log.info("Creating comment for task: {}", taskId);

        Task task = taskService.getTaskByIdOrElseThrow(taskId);

        Comment comment = commentMapper.toComment(requestDto);
        log.info("Mapped CreateCommentRequestDto to Comment entity");

        comment.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        comment.setAuthor(userService.getCurrentUser());
        comment.setTask(task);

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment saved successfully with id: {}", savedComment.getId());

        return commentMapper.toCommentDto(savedComment);
    }

    @Transactional
    public void deleteComment(long commentId) {
        log.info("Deleting comment with id: {}", commentId);
        Comment comment = this.getCommentByIdOrElseThrow(commentId);

        commentRepository.deleteById(commentId);
        log.info("Comment with id: {} deleted successfully", commentId);
    }

    private Comment getCommentByIdOrElseThrow(long commentId) {
        log.info("Getting comment with id: {}", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("Comment not found with id: {}", commentId);
                    return new CommentNotFoundException("error.404.comment.not_found");
                });

        log.info("Comment get successfully. Comment id: {}, task id: {}",
                comment.getId(), comment.getTask().getId());
        return comment;
    }
}
