package ru.andreyszdlv.taskmanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.taskmanager.dto.task.*;
import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;
import ru.andreyszdlv.taskmanager.exception.TaskNotFoundException;
import ru.andreyszdlv.taskmanager.mapper.TaskMapper;
import ru.andreyszdlv.taskmanager.model.Task;
import ru.andreyszdlv.taskmanager.model.User;
import ru.andreyszdlv.taskmanager.repository.TaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final UserService userService;

    @Transactional
    public TaskDto createTask(CreateTaskRequestDto requestDto){
        log.info("Creating new task with title: {}", requestDto.title());

        Task task = taskMapper.toTask(requestDto);
        log.info("Mapped CreateTaskRequestDto to Task entity");
        task.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        task.setAuthor(userService.getCurrentUser());

        if(requestDto.assigneeId() != null) {
            User assignee = userService.getUserByIdOrElseThrow(requestDto.assigneeId());
            task.setAssignee(assignee);
            log.info("Task assigned to user: {}", assignee.getEmail());
        }

        Task savedTask = taskRepository.save(task);
        log.info("Task with id: {} successfully created", savedTask.getId());

        return taskMapper.toTaskDto(savedTask);
    }

    @Transactional
    public TaskDto updateTaskPartial(
            long taskId,
            UpdateTaskPartialRequestDto requestDto
    ) {
        log.info("Updating task with id: {}", taskId);

        Task task = this.getTaskByIdOrElseThrow(taskId);

        Optional.ofNullable(requestDto.title()).ifPresent(task::setTitle);
        Optional.ofNullable(requestDto.description()).ifPresent(task::setDescription);
        Optional.ofNullable(requestDto.priority())
                .ifPresent((priority)->task.setPriority(TaskPriority.valueOf(priority)));
        Optional.ofNullable(requestDto.status())
                .ifPresent((status)->task.setStatus(TaskStatus.valueOf(status)));

        if(requestDto.assigneeId() != null) {
            User assignee = userService.getUserByIdOrElseThrow(requestDto.assigneeId());
            task.setAssignee(assignee);
            log.info("Task reassigned to user: {}", assignee.getEmail());
        }

        log.info("Task with id: {} successfully updated", taskId);
        return taskMapper.toTaskDto(task);
    }

    @Transactional
    public TaskDto updateStatus(long id, UpdateStatusRequestDto requestDto) {
        log.info("Updating status task with id: {}", id);

        Task task = getTaskByIdOrElseThrow(id);

        task.setStatus(TaskStatus.valueOf(requestDto.status()));

        log.info("Task status updated");
        return taskMapper.toTaskDto(task);
    }

    @Transactional
    public TaskDto updatePriority(long id, UpdatePriorityRequestDto requestDto) {
        log.info("Updating priority task with id: {}", id);

        Task task = getTaskByIdOrElseThrow(id);

        task.setPriority(TaskPriority.valueOf(requestDto.priority()));

        log.info("Task priority updated");
        return taskMapper.toTaskDto(task);
    }

    @Transactional
    public TaskDto updateAssignee(long id, UpdateAssigneeRequestDto requestDto) {
        log.info("Updating assignee for task with id: {}", id);

        Task task = getTaskByIdOrElseThrow(id);

        task.setAssignee(userService.getUserByIdOrElseThrow(requestDto.assigneeId()));

        log.info("Task assignee updated");
        return taskMapper.toTaskDto(task);
    }

    @Transactional
    public void deleteTask(long taskId) {
        log.info("Deleting task with id: {}", taskId);

        this.checkTaskExists(taskId);

        taskRepository.deleteById(taskId);

        log.info("Task with id: {} successfully deleted", taskId);
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
        log.info("Getting task by id: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task not found with id: {}", taskId);
                    return new TaskNotFoundException("error.404.task.not_found");
                });

        log.info("Task get successfully. Task id: {}",
                task.getId());
        return task;
    }

    private void checkTaskExists(long taskId) {
        log.info("Checking if task with id: {} exists", taskId);

        if(!taskRepository.existsById(taskId)) {
            log.error("Task with id: {} not exist", taskId);
            throw new TaskNotFoundException("error.404.task.not_found");
        }

        log.info("Task with id: {} exists", taskId);
    }
}
