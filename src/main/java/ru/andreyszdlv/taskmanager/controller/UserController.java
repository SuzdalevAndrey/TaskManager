package ru.andreyszdlv.taskmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.andreyszdlv.taskmanager.dto.user.UserDto;
import ru.andreyszdlv.taskmanager.service.UserService;

import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final MessageSource messageSource;

    @Operation(
            summary = "Назначение пользователя администратором",
            description = "Этот эндпоинт позволяет назначить пользователя администратором. Требуется роль ADMIN",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Роль пользователя успешно изменена на администратор",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Пользователь с таким ID не найден",
                            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав, пользователь с ролью USER.", content = @Content)
            }
    )
    @PostMapping("/{id}/make-admin")
    public ResponseEntity<String> makeAdmin(@PathVariable long id, Locale locale) {
        log.info("Received request make admin for user id: {}", id);

        userService.changeRoleToAdmin(id);

        log.info("User with id: {} now admin", id);
        return ResponseEntity.ok(
                messageSource.getMessage(
                        "user.role.change_to_admin",
                        new Object[]{id},
                        "user.role.change_to_admin",
                        locale
                )
        );
    }

    @Operation(
            summary = "Получение списка всех пользователей",
            description = "Этот эндпоинт позволяет получить список всех пользователей. Требуется роль ADMIN",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав, пользователь с ролью USER.", content = @Content)
            }
    )
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(){
        log.info("Received request get all users");

        List<UserDto> users = userService.getAllUsers();

        log.info("Returning {} users", users.size());
        return ResponseEntity.ok(users);
    }
}
