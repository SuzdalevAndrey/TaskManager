package ru.andreyszdlv.taskmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.andreyszdlv.taskmanager.dto.comment.CommentDto;
import ru.andreyszdlv.taskmanager.dto.comment.CreateCommentRequestDto;
import ru.andreyszdlv.taskmanager.service.CommentService;
import ru.andreyszdlv.taskmanager.validation.RequestValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    private final RequestValidator requestValidator;

    @PostMapping("/{taskId}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable long taskId,
            @Valid @RequestBody CreateCommentRequestDto createCommentRequestDto,
            BindingResult bindingResult
    ) throws BindException {
        log.info("Received request create comment for taskId: {}", taskId);

        requestValidator.validateRequest(bindingResult);

        CommentDto createdComment = commentService.createComment(taskId, createCommentRequestDto);

        log.info("Comment created successfully for taskId: {}, commentId: {}", taskId, createdComment.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long commentId) {
        log.info("Received request delete comment with commentId: {}", commentId);

        commentService.deleteComment(commentId);

        log.info("Comment deleted successfully for commentId: {}", commentId);
        return ResponseEntity.noContent().build();
    }
}