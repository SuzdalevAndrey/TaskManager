package ru.andreyszdlv.taskmanager.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.taskmanager.exception.InvalidRefreshTokenException;
import ru.andreyszdlv.taskmanager.exception.InvalidTokenException;
import ru.andreyszdlv.taskmanager.service.JwtStorageService;
import ru.andreyszdlv.taskmanager.service.JwtExtractorService;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtValidator {

    private final JwtStorageService jwtStorageService;

    private final JwtExtractorService jwtExtractorService;

    public void validateAccessToken(String token) {
        log.info("Validating access token");

        String userEmail = jwtExtractorService.extractUserEmail(token);
        log.info("Extract userEmail from access token: {}", userEmail);

        String expectedAccessToken = jwtStorageService.getAccessTokenByUserEmail(userEmail);

        if(Objects.isNull(expectedAccessToken) || !expectedAccessToken.equals(token)) {
            log.error("Access token validation failed for: {}", userEmail);
            throw new InvalidTokenException();
        }

        log.info("Access token validation successful for: {}", userEmail);
    }

    public void validateRefreshToken(String token) {
        log.info("Validating refresh token");

        String userEmail = jwtExtractorService.extractUserEmail(token);
        log.info("Extract userEmail from refresh token: {}", userEmail);

        String expectedRefreshToken = jwtStorageService.getRefreshTokenByUserEmail(userEmail);

        if(Objects.isNull(expectedRefreshToken) || !expectedRefreshToken.equals(token)) {
            log.error("Refresh token validation failed for: {}", userEmail);
            throw new InvalidRefreshTokenException("error.409.refresh_token.invalid");
        }

        log.info("Refresh token validation successful for: {}", userEmail);
    }
}
