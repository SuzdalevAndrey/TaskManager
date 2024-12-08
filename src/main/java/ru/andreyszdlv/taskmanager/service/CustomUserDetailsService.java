package ru.andreyszdlv.taskmanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.taskmanager.exception.UserNotFoundException;
import ru.andreyszdlv.taskmanager.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        log.info("Loading user with email: {}", username);

        return userRepository.findByEmail(username)
                .map(user -> {
                    log.info("User found: {}", user.getEmail());
                    return user;
                })
                .orElseThrow(() -> {
                    log.error("User not found for email: {}", username);
                    return new UsernameNotFoundException("User not found for email: " + username);
                });
    }
}
