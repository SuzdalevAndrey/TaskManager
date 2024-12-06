package ru.andreyszdlv.taskmanager.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.taskmanager.exception.InvalidRefreshTokenException;
import ru.andreyszdlv.taskmanager.exception.InvalidTokenException;
import ru.andreyszdlv.taskmanager.exception.UserUnauthorizedException;
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
        String userEmail = jwtSecurityService.extractUserEmail(token);

        String expectedAccessToken = accessAndRefreshJwtService.getAccessTokenByUserEmail(userEmail);

        if(Objects.isNull(expectedAccessToken) || !expectedAccessToken.equals(token)) {
            throw new InvalidTokenException();
        }
    }

    public void validateRefresh(String token) {

        String userEmail = jwtSecurityService.extractUserEmail(token);

        String expectedRefreshToken = accessAndRefreshJwtService.getRefreshTokenByUserEmail(userEmail);

        if(Objects.isNull(expectedRefreshToken) || !expectedRefreshToken.equals(token)) {
            throw new InvalidRefreshTokenException("error.409.refresh_token.invalid");
        }
    }
}
