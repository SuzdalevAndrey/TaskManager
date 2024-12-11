package ru.andreyszdlv.taskmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.andreyszdlv.taskmanager.dto.task.*;
import ru.andreyszdlv.taskmanager.service.TaskService;
import ru.andreyszdlv.taskmanager.validation.RequestValidator;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    private final RequestValidator requestValidator;

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks(){
        log.info("Received request get all tasks");

        List<TaskDto> tasks = taskService.getAllTasks();

        log.info("Returning {} tasks", tasks.size());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable long id){
        log.info("Received request get task by id: {}", id);

        TaskDto task = taskService.getTaskById(id);

        log.info("Returning task with id: {}", id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @Valid @RequestBody CreateTaskRequestDto createTaskRequestDto,
            BindingResult bindingResult
    ) throws BindException {
        log.info("Received request create task: {}", createTaskRequestDto);

        requestValidator.validateRequest(bindingResult);

        TaskDto createdTask = taskService.createTask(createTaskRequestDto);

        log.info("Task created successfully with id: {}", createdTask.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskDto> updateTaskPartial(
            @PathVariable long id,
            @Valid @RequestBody UpdateTaskPartialRequestDto updateTaskPartialRequestDto,
            BindingResult bindingResult
    ) throws BindException {
        log.info("Received request partially update task with id: {}", id);

        requestValidator.validateRequest(bindingResult);

        TaskDto updatedTask = taskService.updateTaskPartial(id, updateTaskPartialRequestDto);

        log.info("Task with id: {} partially updated successfully", id);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDto> updateStatusTask(
            @PathVariable long id,
            @Valid @RequestBody UpdateStatusRequestDto updateStatusRequestDto,
            BindingResult bindingResult
    ) throws BindException {
        log.info("Received request update status for task with id: {}", id);

        requestValidator.validateRequest(bindingResult);

        TaskDto updatedTask = taskService.updateStatus(id, updateStatusRequestDto);

        log.info("Status for task with id: {} updated successfully", id);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/priority")
    public ResponseEntity<TaskDto> updatePriorityTask(
            @PathVariable long id,
            @Valid @RequestBody UpdatePriorityRequestDto updatePriorityRequestDto,
            BindingResult bindingResult
    ) throws BindException {
        log.info("Received request update priority for task with id: {}", id);

        requestValidator.validateRequest(bindingResult);

        TaskDto updatedTask = taskService.updatePriority(id, updatePriorityRequestDto);

        log.info("Priority for task with id: {} updated successfully", id);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/assignee")
    public ResponseEntity<TaskDto> updateAssigneeTask(
            @PathVariable long id,
            @Valid @RequestBody UpdateAssigneeRequestDto updateAssigneeRequestDto,
            BindingResult bindingResult
    ) throws BindException{
        log.info("Received request update assignee for task with id: {}", id);

        requestValidator.validateRequest(bindingResult);

        TaskDto updatedTask = taskService.updateAssignee(id, updateAssigneeRequestDto);

        log.info("Assignee for task with id: {} updated successfully", id);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable long id) {
        log.info("Received request delete task with id: {}", id);

        taskService.deleteTask(id);

        log.info("Task with id: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}