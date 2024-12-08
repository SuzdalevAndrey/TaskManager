package ru.andreyszdlv.taskmanager.service;

import ru.andreyszdlv.taskmanager.enums.Role;

public interface JwtStorageService {

    String generateAccessToken(String userEmail, Role role);

    String generateRefreshToken(String userEmail, Role role);

    String getAccessTokenByUserEmail(String userEmail);

    String getRefreshTokenByUserEmail(String userEmail);

    void deleteByUserEmail(String userEmail);
}
