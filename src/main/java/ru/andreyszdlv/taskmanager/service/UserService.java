package ru.andreyszdlv.taskmanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.taskmanager.exception.UserAlreadyExsitsException;
import ru.andreyszdlv.taskmanager.exception.UserNotFoundException;
import ru.andreyszdlv.taskmanager.model.User;
import ru.andreyszdlv.taskmanager.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

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

    @Transactional(readOnly = true)
    public User getUserOrElseThrow(long userId){
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new UserNotFoundException("error.404.user.not_found")
                );
    }

    @Transactional(readOnly = true)
    public User getUserOrElseThrow(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException("error.404.user.not_found")
                );
    }
}
