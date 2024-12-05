package ru.andreyszdlv.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.taskmanager.dto.RegisterUserRequestDto;
import ru.andreyszdlv.taskmanager.dto.RegisterUserResponseDto;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.mapper.UserMapper;
import ru.andreyszdlv.taskmanager.model.User;
import ru.andreyszdlv.taskmanager.repository.UserRepository;
import ru.andreyszdlv.taskmanager.validator.UserValidator;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final UserValidator userValidator;

    private final UserMapper userMapper;

    @Transactional
    public RegisterUserResponseDto registerUser(RegisterUserRequestDto registerUserRequestDto) {
        userValidator.checkUserExists(registerUserRequestDto.email());

        User user = userMapper.toUser(registerUserRequestDto);
        user.setRole(Role.USER);

        return userMapper.toRegisterUserResponseDto(userRepository.save(user));
    }
}
