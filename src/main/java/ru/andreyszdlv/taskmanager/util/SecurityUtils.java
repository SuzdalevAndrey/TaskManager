package ru.andreyszdlv.taskmanager.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.andreyszdlv.taskmanager.exception.UserUnauthenticatedException;

@Slf4j
public class SecurityUtils {

    public static String getCurrentUserName(){
        log.info("Get current user name");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Check authentication");
        if(authentication.isAuthenticated()){
            log.info("User is authenticated");
            return authentication.getName();
        }

        log.error("User is unauthenticated");
        throw new UserUnauthenticatedException("error.401.user.unauthenticated");
    }
}
