package ru.andreyszdlv.taskmanager.dto;

public record RegisterUserResponseDto(
        long id,
        String name,
        String email
) {
}
