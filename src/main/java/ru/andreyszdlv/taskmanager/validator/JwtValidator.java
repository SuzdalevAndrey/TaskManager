package ru.andreyszdlv.taskmanager.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.taskmanager.exception.InvalidRefreshTokenException;
import ru.andreyszdlv.taskmanager.exception.InvalidTokenException;
import ru.andreyszdlv.taskmanager.service.AccessAndRefreshJwtService;
import ru.andreyszdlv.taskmanager.service.JwtSecurityService;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtValidator {

    private final AccessAndRefreshJwtService accessAndRefreshJwtService;

    private final JwtSecurityService jwtSecurityService;

    public void validateAccess(String token) {
        log.info("Validating access token");

        String userEmail = jwtSecurityService.extractUserEmail(token);
        log.info("Extract userEmail from access token: {}", userEmail);

        String expectedAccessToken = accessAndRefreshJwtService.getAccessTokenByUserEmail(userEmail);

        if(Objects.isNull(expectedAccessToken) || !expectedAccessToken.equals(token)) {
            log.error("Access token validation failed for: {}", userEmail);
            throw new InvalidTokenException();
        }

        log.info("Access token validation successful for: {}", userEmail);
    }

    public void validateRefresh(String token) {
        log.info("Validating refresh token");

        String userEmail = jwtSecurityService.extractUserEmail(token);
        log.info("Extract userEmail from refresh token: {}", userEmail);

        String expectedRefreshToken = accessAndRefreshJwtService.getRefreshTokenByUserEmail(userEmail);

        if(Objects.isNull(expectedRefreshToken) || !expectedRefreshToken.equals(token)) {
            log.error("Refresh token validation failed for: {}", userEmail);
            throw new InvalidRefreshTokenException("error.409.refresh_token.invalid");
        }

        log.info("Refresh token validation successful for: {}", userEmail);
    }
}
