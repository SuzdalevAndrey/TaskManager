package ru.andreyszdlv.taskmanager.service;

public interface AccessAndRefreshJwtService {

    String generateAccessToken(long userId, String role);

    String generateRefreshToken(long userId, String role);

    String getAccessTokenByUserId(long userId);

    String getRefreshTokenByUserId(long userId);

    void deleteByUserId(long userId);
}
