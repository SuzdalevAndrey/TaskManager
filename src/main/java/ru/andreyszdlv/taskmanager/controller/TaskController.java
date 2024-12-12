package ru.andreyszdlv.taskmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.andreyszdlv.taskmanager.dto.task.*;
import ru.andreyszdlv.taskmanager.service.TaskService;
import ru.andreyszdlv.taskmanager.validation.RequestValidator;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    private final RequestValidator requestValidator;

    @Operation(
            summary = "Получение всех задач",
            description = "Этот эндпоинт позволяет получить все задачи с возможностью фильтрации по статусу, приоритету, автору и исполнителю. Требуется роль ADMIN.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Задачи успешно получены",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Ошибка валидации фильтров",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав, пользователь с ролью USER.",
                            content = @Content
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Page<TaskDto>> getAllTasks(
            @Valid TaskFilterDto taskFilterDto,
            BindingResult bindingResult
    ) throws BindException {
        log.info("Received request get all tasks");

        requestValidator.validateRequest(bindingResult);

        Page<TaskDto> tasks = taskService.getAllTasks(
                taskFilterDto.status(),
                taskFilterDto.priority(),
                taskFilterDto.authorId(),
                taskFilterDto.assigneeId(),
                taskFilterDto.page(),
                taskFilterDto.size()
        );

        log.info("Returning {} tasks", tasks.getSize());
        return ResponseEntity.ok(tasks);
    }


    @Operation(
            summary = "Получение задач, назначенных на меня",
            description = "Этот эндпоинт позволяет получить все задачи, где текущий пользователь является исполнителем.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Задачи успешно получены",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Ошибка валидации фильтров",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
                    )
            }
    )
    @GetMapping("/assigned-to-me")
    public ResponseEntity<Page<TaskDto>> getTasksAssigneeToMe(
            @Valid TaskFilterForAssigneeDto taskFilterForAssigneeDto,
            BindingResult bindingResult
    ) throws BindException {
        log.info("Received request get tasks, where user assignee");

        requestValidator.validateRequest(bindingResult);

        Page<TaskDto> tasks = taskService.getAllTasksWhereUserAssignee(
                taskFilterForAssigneeDto.status(),
                taskFilterForAssigneeDto.priority(),
                taskFilterForAssigneeDto.page(),
                taskFilterForAssigneeDto.size()
        );

        log.info("Returning {} tasks", tasks.getSize());
        return ResponseEntity.ok(tasks);
    }

    @Operation(
            summary = "Получение задачи по ID",
            description = "Этот эндпоинт позволяет получить задачу по ее уникальному ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Задача успешно получена",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Задача с таким ID не найдена",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав, пользователь с ролью USER пытается получить задачу, у которой он не назначен исполнителем.",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable long id){
        log.info("Received request get task by id: {}", id);

        TaskDto task = taskService.getTaskById(id);

        log.info("Returning task with id: {}", id);
        return ResponseEntity.ok(task);
    }

    @Operation(
            summary = "Создание новой задачи",
            description = "Этот эндпоинт позволяет создать новую задачу с указанием всех необходимых данных. Требуется роль ADMIN.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Задача успешно создана",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Ошибка валидации данных",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав, пользователь с ролью USER.",
                            content = @Content
                    )
            }
    )
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

    @Operation(
            summary = "Частичное обновление задачи",
            description = "Этот эндпоинт позволяет частично обновить данные задачи (например, только название или описание). Требуется роль ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача успешно обновлена",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDto.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "404", description = "Задача с таким ID не найдена",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав, пользователь с ролью USER.", content = @Content)
            }
    )
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

    @Operation(
            summary = "Обновление статуса задачи",
            description = "Этот эндпоинт позволяет обновить статус задачи.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Статус задачи обновлен успешно",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDto.class))),
                    @ApiResponse(responseCode = "404", description = "Задача с таким ID не найдена",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав, пользователь с ролью USER пытается обновить статус задачи, у которой он не назначен исполнителем",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
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

    @Operation(
            summary = "Обновление приоритета задачи",
            description = "Этот эндпоинт позволяет обновить приоритет задачи. Требуется роль ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Приоритет задачи обновлен успешно",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDto.class))),
                    @ApiResponse(responseCode = "404", description = "Задача с таким ID не найдена",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав, пользователь с ролью USER", content = @Content)
            }
    )
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

    @Operation(
            summary = "Обновление исполнителя задачи",
            description = "Этот эндпоинт позволяет обновить исполнителя задачи. Требуется роль ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Исполнитель задачи обновлен успешно",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDto.class))),
                    @ApiResponse(responseCode = "404", description = "Задача с таким ID не найдена",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав, пользователь с ролью USER", content = @Content)
            }
    )
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

    @Operation(
            summary = "Удаление задачи",
            description = "Этот эндпоинт позволяет удалить задачу по ID. Требуется роль ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
                    @ApiResponse(responseCode = "404", description = "Задача с таким ID не найдена",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав, пользователь с ролью USER", content = @Content)
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable long id) {
        log.info("Received request delete task with id: {}", id);

        taskService.deleteTask(id);

        log.info("Task with id: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}