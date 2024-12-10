package ru.andreyszdlv.taskmanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.exception.UserAlreadyExsitsException;
import ru.andreyszdlv.taskmanager.exception.UserNotFoundException;
import ru.andreyszdlv.taskmanager.exception.UserUnauthenticatedException;
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
    public User getUserByIdOrElseThrow(long userId){
        log.info("Getting user with id {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new UserNotFoundException("error.404.user.not_found");
                });

        log.info("User get successfully. User id: {}", userId);
        return user;
    }

    @Transactional(readOnly = true)
    public User getCurrentUser(){
        log.info("Getting current user");
        User user = userRepository.findByEmail(this.getCurrentUserEmail())
                .orElseThrow(() -> {
                    log.error("User not found");
                    return new UserUnauthenticatedException("error.401.user.unauthenticated");
                });

        log.info("Current user get successfully. User id: {}", user.getId());
        return user;
    }

    public String getCurrentUserEmail(){
        log.info("Get current user email");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Check authentication");
        if(authentication.isAuthenticated()){
            log.info("User is authenticated");
            return authentication.getName();
        }

        log.error("User is unauthenticated");
        throw new UserUnauthenticatedException("error.401.user.unauthenticated");
    }

    public Role getCurrentUserRole(){
        log.info("Get current user role");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Check authentication");
        if (authentication.isAuthenticated()) {
            log.info("User is authenticated");

            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .map(Role::valueOf)
                    .orElse(null);
        }

        log.error("User is unauthenticated");
        throw new UserUnauthenticatedException("error.401.user.unauthenticated");
    }
}
