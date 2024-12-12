package ru.andreyszdlv.taskmanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
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

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(){
        log.info("Received request get all users");

        List<UserDto> users = userService.getAllUsers();

        log.info("Returning {} users", users.size());
        return ResponseEntity.ok(users);
    }
}
