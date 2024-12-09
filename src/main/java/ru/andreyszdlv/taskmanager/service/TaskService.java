package ru.andreyszdlv.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.taskmanager.dto.task.*;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;
import ru.andreyszdlv.taskmanager.mapper.TaskMapper;
import ru.andreyszdlv.taskmanager.model.Task;
import ru.andreyszdlv.taskmanager.model.User;
import ru.andreyszdlv.taskmanager.repository.TaskRepository;
import ru.andreyszdlv.taskmanager.validator.TaskValidator;
import ru.andreyszdlv.taskmanager.validator.UserValidator;

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

    private final TaskValidator taskValidator;

    private final UserValidator userValidator;

    @Transactional
    public CreateTaskResponseDto createTask(CreateTaskRequestDto requestDto){

        Task task = taskMapper.toTask(requestDto);
        task.setStatus(TaskStatus.WAITING);
        task.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        task.setAuthor(userValidator.getUserOrElseThrow(securityContextService.getCurrentUserName()));

        return taskMapper.toCreateTaskResponseDto(taskRepository.save(task));
    }

    @Transactional
    public UpdateTaskResponseDto updateTask(long taskId, UpdateTaskRequestDto requestDto) {
        Task task = taskValidator.getTaskByIdOrElseThrow(taskId);

        task.setStatus(requestDto.status());
        task.setDescription(requestDto.description());
        task.setPriority(requestDto.priority());

        if(requestDto.assigneeId() != null) {
            User assignee = userValidator.getUserOrElseThrow(requestDto.assigneeId());
            task.setAssignee(assignee);
        }

        return taskMapper.toUpdateTaskResponseDto(task);
    }

    @Transactional
    public UpdateTaskPartialResponseDto updateTaskPartial(
            long taskId,
            UpdateTaskPartialRequestDto requestDto
    ) {

        Task task = taskValidator.getTaskByIdOrElseThrow(taskId);

        Optional.ofNullable(requestDto.title()).ifPresent(task::setTitle);
        Optional.ofNullable(requestDto.description()).ifPresent(task::setDescription);
        Optional.ofNullable(requestDto.priority()).ifPresent(task::setPriority);
        Optional.ofNullable(requestDto.status()).ifPresent(task::setStatus);

        if(requestDto.assigneeId() != null) {
            User assignee = userValidator.getUserOrElseThrow(requestDto.assigneeId());
            task.setAssignee(assignee);
        }

        return taskMapper.toUpdateTaskPartialResponseDto(task);
    }

    @Transactional
    public void deleteTask(long taskId) {

        taskRepository.deleteById(taskId);
    }

    @Transactional(readOnly = true)
    public TaskDto getTaskById(long taskId) {
        return taskMapper.toTaskDto(taskValidator.getTaskByIdOrElseThrow(taskId));
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream().map(taskMapper::toTaskDto).toList();
    }
}
