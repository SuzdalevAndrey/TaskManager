package ru.andreyszdlv.taskmanager.dto.auth;

public record RegisterUserResponseDto(
        long id,
        String name,
        String email
) {
}
