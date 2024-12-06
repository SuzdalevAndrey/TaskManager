package ru.andreyszdlv.taskmanager.service;

public interface AccessAndRefreshJwtService {

    String generateAccessToken(String userEmail, String role);

    String generateRefreshToken(String userEmail, String role);

    String getAccessTokenByUserEmail(String userEmail);

    String getRefreshTokenByUserEmail(String userEmail);

    void deleteByUserId(String userEmail);
}
