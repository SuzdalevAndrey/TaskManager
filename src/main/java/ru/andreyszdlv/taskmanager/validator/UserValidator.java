package ru.andreyszdlv.taskmanager.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.taskmanager.exception.UserAlreadyExsitsException;
import ru.andreyszdlv.taskmanager.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidator {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public void checkUserExists(String email){
        log.info("Checking user exists with email {}", email);
        if(userRepository.existsByEmail(email)){
            log.error("User with email {} already exists", email);
            throw new UserAlreadyExsitsException("error.409.user.already_exists");
        }
        log.info("User with email {} no exists", email);
    }
}
