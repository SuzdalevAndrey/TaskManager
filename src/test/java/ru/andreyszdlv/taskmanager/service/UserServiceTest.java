package ru.andreyszdlv.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.andreyszdlv.taskmanager.exception.UserAlreadyExsitsException;
import ru.andreyszdlv.taskmanager.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkUserExists_NoThrowsException_WhenUserNotExists() {
        String email = "email@email.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        userService.checkUserExists(email);

        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void checkUserExists_ThrowsException_WhenUserAlreadyExists() {
        String email = "email@email.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(
                UserAlreadyExsitsException.class,
                ()->userService.checkUserExists(email)
        );

        verify(userRepository, times(1)).existsByEmail(email);
    }
}