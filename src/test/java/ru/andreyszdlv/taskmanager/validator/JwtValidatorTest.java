package ru.andreyszdlv.taskmanager.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.andreyszdlv.taskmanager.exception.InvalidTokenException;
import ru.andreyszdlv.taskmanager.service.JwtExtractorService;
import ru.andreyszdlv.taskmanager.service.JwtStorageService;
import ru.andreyszdlv.taskmanager.validation.JwtValidator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtValidatorTest {

    @Mock
    JwtStorageService jwtStorageService;

    @Mock
    JwtExtractorService jwtExtractorService;

    @InjectMocks
    JwtValidator jwtValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateAccessToken_Success_WhenTokenValid() {
        String userEmail = "user@email.com";
        String accessToken = "accessToken";
        when(jwtExtractorService.extractUserEmail(accessToken)).thenReturn(userEmail);
        when(jwtStorageService.getAccessTokenByUserEmail(userEmail)).thenReturn(accessToken);

        jwtValidator.validateAccessToken(accessToken);

        verify(jwtExtractorService, times(1)).extractUserEmail(accessToken);
        verify(jwtStorageService, times(1)).getAccessTokenByUserEmail(userEmail);
    }

    @Test
    void validateAccessToken_ThrowsException_WhenTokenInValid() {
        String userEmail = "user@email.com";
        String accessToken = "accessToken";
        when(jwtExtractorService.extractUserEmail(accessToken)).thenReturn(userEmail);
        when(jwtStorageService.getAccessTokenByUserEmail(userEmail)).thenThrow(InvalidTokenException.class);

        assertThrows(
                InvalidTokenException.class,
                () -> jwtValidator.validateAccessToken(accessToken)
        );

        verify(jwtExtractorService, times(1)).extractUserEmail(accessToken);
        verify(jwtStorageService, times(1)).getAccessTokenByUserEmail(userEmail);
    }

    @Test
    void validateRefreshToken_Success_WhenTokenValid() {
        String userEmail = "user@email.com";
        String refreshToken = "refreshToken";
        when(jwtExtractorService.extractUserEmail(refreshToken)).thenReturn(userEmail);
        when(jwtStorageService.getRefreshTokenByUserEmail(userEmail)).thenReturn(refreshToken);

        jwtValidator.validateRefreshToken(refreshToken);

        verify(jwtExtractorService, times(1)).extractUserEmail(refreshToken);
        verify(jwtStorageService, times(1)).getRefreshTokenByUserEmail(userEmail);
    }

    @Test
    void validateRefreshToken_ThrowsException_WhenTokenInValid() {
        String userEmail = "user@email.com";
        String refreshToken = "refreshToken";
        when(jwtExtractorService.extractUserEmail(refreshToken)).thenReturn(userEmail);
        when(jwtStorageService.getRefreshTokenByUserEmail(userEmail)).thenThrow(InvalidTokenException.class);

        assertThrows(
                InvalidTokenException.class,
                () -> jwtValidator.validateRefreshToken(refreshToken)
        );

        verify(jwtExtractorService, times(1)).extractUserEmail(refreshToken);
        verify(jwtStorageService, times(1)).getRefreshTokenByUserEmail(userEmail);
    }
}