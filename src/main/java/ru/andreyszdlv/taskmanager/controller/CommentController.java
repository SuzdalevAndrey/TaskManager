package ru.andreyszdlv.taskmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.andreyszdlv.taskmanager.dto.comment.CommentDto;
import ru.andreyszdlv.taskmanager.dto.comment.CreateCommentRequestDto;
import ru.andreyszdlv.taskmanager.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{taskId}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable long taskId,
            @Valid @RequestBody CreateCommentRequestDto createCommentRequestDto,
            BindingResult bindingResult
    ) throws BindException {

        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.createComment(taskId, createCommentRequestDto));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
