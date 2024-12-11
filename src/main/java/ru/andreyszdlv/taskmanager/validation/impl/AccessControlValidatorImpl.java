package ru.andreyszdlv.taskmanager.validation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.model.Comment;
import ru.andreyszdlv.taskmanager.model.Task;
import ru.andreyszdlv.taskmanager.service.UserService;
import ru.andreyszdlv.taskmanager.validation.AccessControlValidator;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessControlValidatorImpl implements AccessControlValidator {

    private final UserService userService;

    public boolean validateAccessTask(Task task) {
        return userService.getCurrentUserRole() == Role.ADMIN
                || (userService.getCurrentUserRole() == Role.USER
                && task.getAssignee().getEmail().equals(userService.getCurrentUserEmail()));
    }

    public boolean validateAccessComment(Comment comment) {
        return userService.getCurrentUserRole() == Role.ADMIN
                || (userService.getCurrentUserRole() == Role.USER
                && comment.getAuthor().getEmail().equals(userService.getCurrentUserEmail()));
    }
}
