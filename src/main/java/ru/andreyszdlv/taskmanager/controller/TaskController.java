package ru.andreyszdlv.taskmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.andreyszdlv.taskmanager.dto.task.*;
import ru.andreyszdlv.taskmanager.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks(){
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable long id){
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @Valid @RequestBody CreateTaskRequestDto createTaskRequestDto,
            BindingResult bindingResult
    ) throws BindException {

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.createTask(createTaskRequestDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskDto> updateTaskPartial(
            @PathVariable long id,
            @Valid @RequestBody UpdateTaskPartialRequestDto updateTaskPartialRequestDto,
            BindingResult bindingResult
    ) throws BindException {

        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return ResponseEntity.ok(taskService.updateTaskPartial(id, updateTaskPartialRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
