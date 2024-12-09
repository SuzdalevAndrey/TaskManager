package ru.andreyszdlv.taskmanager.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.taskmanager.exception.CommentNotFoundException;
import ru.andreyszdlv.taskmanager.model.Comment;
import ru.andreyszdlv.taskmanager.repository.CommentRepository;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Comment getCommentByIdOrElseThrow(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                ()->new CommentNotFoundException("error.404.comment.not_found")
        );
    }
}
