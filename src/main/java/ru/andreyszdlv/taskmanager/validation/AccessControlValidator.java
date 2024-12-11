package ru.andreyszdlv.taskmanager.validation;

import ru.andreyszdlv.taskmanager.model.Comment;
import ru.andreyszdlv.taskmanager.model.Task;

public interface AccessControlValidator {

    boolean validateAccessTask(Task task);

    boolean validateAccessComment(Comment comment);
}
