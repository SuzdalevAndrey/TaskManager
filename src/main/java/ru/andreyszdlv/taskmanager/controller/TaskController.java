package ru.andreyszdlv.taskmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.andreyszdlv.taskmanager.dto.task.*;
import ru.andreyszdlv.taskmanager.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateTaskResponseDto createTask(
            @Valid @RequestBody CreateTaskRequestDto createTaskRequestDto,
            BindingResult bindingResult
    ) throws BindException {

        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return taskService.createTask(createTaskRequestDto);
    }

    @PutMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public UpdateTaskResponseDto updateTask(
            @PathVariable long taskId,
            @Valid @RequestBody UpdateTaskRequestDto updateTaskRequestDto,
            BindingResult bindingResult
    ) throws BindException {

        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return taskService.updateTask(taskId, updateTaskRequestDto);
    }

    @PatchMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public UpdateTaskPartialResponseDto updateTaskPartial(
            @PathVariable long taskId,
            @Valid @RequestBody UpdateTaskPartialRequestDto updateTaskPartialRequestDto,
            BindingResult bindingResult
    ) throws BindException {

        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return taskService.updateTaskPartial(taskId, updateTaskPartialRequestDto);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable long taskId) {
        taskService.deleteTask(taskId);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDto> getAllTasks(){
        return taskService.getAllTasks();
    }

}
