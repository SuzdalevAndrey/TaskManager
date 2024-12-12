package ru.andreyszdlv.taskmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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

    @Operation(
            summary = "Создание комментария для задачи",
            description = "Этот эндпоинт позволяет создать новый комментарий для задачи по заданному taskId.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Комментарий успешно создан",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Не достаточно прав для взаимодействия",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
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

    @Operation(
            summary = "Удаление комментария",
            description = "Этот эндпоинт позволяет удалить комментарий по его commentId.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Комментарий успешно удален"),
                    @ApiResponse(responseCode = "404", description = "Комментарий не найден",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Не достаточно прав для взаимодействия",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long commentId) {
        log.info("Received request delete comment with commentId: {}", commentId);

        commentService.deleteComment(commentId);

        log.info("Comment deleted successfully for commentId: {}", commentId);
        return ResponseEntity.noContent().build();
    }
}