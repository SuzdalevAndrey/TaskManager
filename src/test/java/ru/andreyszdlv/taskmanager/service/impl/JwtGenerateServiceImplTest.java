package ru.andreyszdlv.taskmanager.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.andreyszdlv.taskmanager.enums.Role;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtGenerateServiceImplTest {

    JwtGenerateServiceImpl jwtGenerateService;

    @BeforeEach
    void setUp() {
        jwtGenerateService = new JwtGenerateServiceImpl();
        ReflectionTestUtils.setField(
                jwtGenerateService,
                "SECRET_KEY",
                "kkfkdkfkkirjh23njfdjfsdnkno13ifoewn312jh4fjenw21"
        );
    }

    @Test
    void generateAccessToken_Success() {
        String userEmail = "test@test.com";
        Role role = Role.USER;

        String accessToken = jwtGenerateService.generateAccessToken(userEmail, role);

        assertNotNull(accessToken);
    }

    @Test
    void generateRefreshToken_Success() {
        String userEmail = "test@test.com";
        Role role = Role.USER;

        String refreshToken = jwtGenerateService.generateRefreshToken(userEmail, role);

        assertNotNull(refreshToken);
    }
}