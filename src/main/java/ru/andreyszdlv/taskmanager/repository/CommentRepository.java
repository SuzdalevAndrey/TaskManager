package ru.andreyszdlv.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreyszdlv.taskmanager.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
