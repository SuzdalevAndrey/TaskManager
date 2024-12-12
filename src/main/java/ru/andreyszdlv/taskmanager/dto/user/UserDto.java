package ru.andreyszdlv.taskmanager.dto.user;

import ru.andreyszdlv.taskmanager.enums.Role;

public record UserDto(

        long id,

        String name,

        String email,

        Role role
) {
}
