package ru.andreyszdlv.taskmanager.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.andreyszdlv.taskmanager.exception.UserUnauthorizedException;

public class SecurityUtils {

    public static String getCurrentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated()){
            return authentication.getName();
        }

        throw new UserUnauthorizedException("error.401.user.unauthorized");
    }
}
