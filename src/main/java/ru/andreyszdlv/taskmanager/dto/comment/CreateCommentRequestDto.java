package ru.andreyszdlv.taskmanager.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequestDto(
        @NotBlank(message = "{validation.error.task.content.is_empty}")
        @Size(max = 1000, message = "{validation.error.task.content.invalid}")
        String content
) {
}
