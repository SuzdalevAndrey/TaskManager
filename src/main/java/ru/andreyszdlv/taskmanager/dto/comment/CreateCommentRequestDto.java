package ru.andreyszdlv.taskmanager.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для создания нового комментария")
public record CreateCommentRequestDto(

        @NotBlank(message = "{validation.error.task.content.is_empty}")
        @Size(max = 1000, message = "{validation.error.task.content.invalid}")
        @Schema(description = "Содержание комментария", example = "Это новый комментарий", maxLength = 1000)
        String content
) {
}
