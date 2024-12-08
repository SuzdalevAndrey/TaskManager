package ru.andreyszdlv.taskmanager.service;

import ru.andreyszdlv.taskmanager.enums.Role;

public interface JwtExtractorService {

    String extractUserEmail(String token);

    Role extractRole(String token);
}