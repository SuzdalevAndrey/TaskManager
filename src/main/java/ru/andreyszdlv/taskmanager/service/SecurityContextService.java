package ru.andreyszdlv.taskmanager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.exception.UserUnauthenticatedException;

@Slf4j
@Service
public class SecurityContextService {

    public String getCurrentUserName(){
        log.info("Get current user name");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Check authentication");
        if(authentication.isAuthenticated()){
            log.info("User is authenticated");
            return authentication.getName();
        }

        log.error("User is unauthenticated");
        return null;
    }

    public Role getCurrentUserRole(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated()) {

            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .map(Role::valueOf)
                    .orElse(null);
        }

        return null;
    }
}
