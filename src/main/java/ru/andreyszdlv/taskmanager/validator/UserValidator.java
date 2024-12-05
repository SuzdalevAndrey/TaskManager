package ru.andreyszdlv.taskmanager.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.taskmanager.exception.UserAlreadyExsitsException;
import ru.andreyszdlv.taskmanager.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {
    //todo логи

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public void checkUserExists(String email){
        if(userRepository.existsByEmail(email))
            throw new UserAlreadyExsitsException("error.409.user.already_exists");
    }
}
