package ru.andreyszdlv.taskmanager.service;

import ru.andreyszdlv.taskmanager.enums.Role;

public interface JwtGenerateService {

    String generateAccessToken(String userEmail, Role role);

    String generateRefreshToken(String userEmail, Role role);
}
