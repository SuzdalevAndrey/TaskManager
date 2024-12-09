package ru.andreyszdlv.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.taskmanager.dto.task.*;
import ru.andreyszdlv.taskmanager.exception.TaskNotFoundException;
import ru.andreyszdlv.taskmanager.mapper.TaskMapper;
import ru.andreyszdlv.taskmanager.model.Task;
import ru.andreyszdlv.taskmanager.model.User;
import ru.andreyszdlv.taskmanager.repository.TaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final SecurityContextService securityContextService;

    private final TaskMapper taskMapper;

    private final UserService userService;

    @Transactional
    public TaskDto createTask(CreateTaskRequestDto requestDto){

        Task task = taskMapper.toTask(requestDto);
        task.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        task.setAuthor(userService.getUserOrElseThrow(securityContextService.getCurrentUserName()));

        if(requestDto.assigneeId() != null) {
            User assignee = userService.getUserOrElseThrow(requestDto.assigneeId());
            task.setAssignee(assignee);
        }

        return taskMapper.toTaskDto(taskRepository.save(task));
    }

    @Transactional
    public TaskDto updateTaskPartial(
            long taskId,
            UpdateTaskPartialRequestDto requestDto
    ) {

        Task task = this.getTaskByIdOrElseThrow(taskId);

        Optional.ofNullable(requestDto.title()).ifPresent(task::setTitle);
        Optional.ofNullable(requestDto.description()).ifPresent(task::setDescription);
        Optional.ofNullable(requestDto.priority()).ifPresent(task::setPriority);
        Optional.ofNullable(requestDto.status()).ifPresent(task::setStatus);

        if(requestDto.assigneeId() != null) {
            User assignee = userService.getUserOrElseThrow(requestDto.assigneeId());
            task.setAssignee(assignee);
        }

        return taskMapper.toTaskDto(task);
    }

    @Transactional
    public void deleteTask(long taskId) {
        this.checkTaskExists(taskId);

        taskRepository.deleteById(taskId);
    }

    @Transactional(readOnly = true)
    public TaskDto getTaskById(long taskId) {
        return taskMapper.toTaskDto(this.getTaskByIdOrElseThrow(taskId));
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream().map(taskMapper::toTaskDto).toList();
    }

    @Transactional(readOnly = true)
    public Task getTaskByIdOrElseThrow(long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(
                        () -> new TaskNotFoundException("error.404.task.not_found")
                );
    }

    @Transactional(readOnly = true)
    public void checkTaskExists(long taskId) {
        if(!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException("error.404.task.not_found");
        }
    }
}
