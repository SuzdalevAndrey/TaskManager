package ru.andreyszdlv.taskmanager.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.taskmanager.exception.AnotherUserAssigneeTaskException;
import ru.andreyszdlv.taskmanager.exception.TaskNotFoundException;
import ru.andreyszdlv.taskmanager.model.Task;
import ru.andreyszdlv.taskmanager.repository.TaskRepository;
import ru.andreyszdlv.taskmanager.service.SecurityContextService;

@Component
@RequiredArgsConstructor
public class TaskValidator {

    private final TaskRepository taskRepository;

    private final SecurityContextService securityContextService;

    @Transactional(readOnly = true)
    public Task getTaskByIdOrElseThrow(long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(
                        () -> new TaskNotFoundException("error.404.task.not_found")
                );
    }

    public void validateCurrentUserAssigneeTask(Task task) {
        if(!task.getAssignee().getEmail().equals(securityContextService.getCurrentUserName())){
            throw new AnotherUserAssigneeTaskException("error.409.task.assignee.another_user");
        }
    }
}
