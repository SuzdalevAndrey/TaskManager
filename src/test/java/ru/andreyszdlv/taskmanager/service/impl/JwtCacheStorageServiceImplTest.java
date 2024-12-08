package ru.andreyszdlv.taskmanager.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.service.JwtGenerateService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtCacheStorageServiceImplTest {

    @Mock
    JwtGenerateService jwtGenerateService;

    @InjectMocks
    JwtCacheStorageServiceImpl jwtStorageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateAccessToken_Success() {
        String email = "email@email.com";
        Role role = Role.USER;
        String expectedAccessToken = "accessToken";
        when(jwtGenerateService.generateAccessToken(email, role)).thenReturn(expectedAccessToken);

        String accessToken = jwtStorageService.generateAccessToken(email, role);

        assertNotNull(accessToken);
        assertEquals(expectedAccessToken, accessToken);
        verify(jwtGenerateService, times(1)).generateAccessToken(email, role);
    }

    @Test
    void generateRefreshToken_Success() {
        String email = "email@email.com";
        Role role = Role.USER;
        String expectedRefreshToken = "refreshToken";
        when(jwtGenerateService.generateRefreshToken(email, role)).thenReturn(expectedRefreshToken);

        String refreshToken = jwtStorageService.generateRefreshToken(email, role);

        assertNotNull(refreshToken);
        assertEquals(expectedRefreshToken, refreshToken);
        verify(jwtGenerateService, times(1)).generateRefreshToken(email, role);
    }

    @Test
    void getAccessTokenByUserEmail_Success() {
        String email = "email@email.com";

        String accessToken = jwtStorageService.getAccessTokenByUserEmail(email);

        assertNull(accessToken);
    }

    @Test
    void getRefreshTokenByUserEmail_Success() {
        String email = "email@email.com";

        String refreshToken = jwtStorageService.getRefreshTokenByUserEmail(email);

        assertNull(refreshToken);
    }
}