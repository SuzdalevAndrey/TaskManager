package ru.andreyszdlv.taskmanager.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
        long userId = jwtSecurityService.extractUserId(token);

        String expectedAccessToken = accessAndRefreshJwtService.getAccessTokenByUserId(userId);

        if(Objects.isNull(expectedAccessToken) || !expectedAccessToken.equals(token)) {
            throw new InvalidTokenException("error.409.token.invalid");
        }
    }

    public void validateRefresh(String token) {
        long userId = jwtSecurityService.extractUserId(token);

        String expectedRefreshToken = accessAndRefreshJwtService.getAccessTokenByUserId(userId);

        if(Objects.isNull(expectedRefreshToken) || !expectedRefreshToken.equals(token)) {
            throw new InvalidTokenException("error.409.token.invalid");
        }
    }
}
