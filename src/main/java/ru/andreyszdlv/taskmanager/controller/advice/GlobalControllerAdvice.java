package ru.andreyszdlv.taskmanager.controller.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.andreyszdlv.taskmanager.exception.*;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler({
            UserAlreadyExsitsException.class
    })
    public ProblemDetail handleConflictException(RuntimeException ex, Locale locale) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale)
        );

        log.error("handleConflictException: {}", response);

        return response;
    }

    @ExceptionHandler({BindException.class})
    public ProblemDetail handleBadRequestException(BindException ex, Locale locale) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                messageSource.getMessage("validation.error.title", null, "validation.error.title", locale)
        );
        response.setProperty(
                "errors",
                ex.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList()
        );

        log.error("handleBadRequestException: {}", response);

        return response;
    }

    @ExceptionHandler({
            UserUnauthenticatedException.class,
            InvalidRefreshTokenException.class
    })
    public ProblemDetail handleUnauthorizedException(RuntimeException ex, Locale locale) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale)
        );

        log.error("handleUnauthorizedException: {}", response);

        return response;
    }

    @ExceptionHandler({
            TaskNotFoundException.class,
            UserNotFoundException.class,
            CommentNotFoundException.class
    })
    public ProblemDetail handleNotFoundException(RuntimeException ex, Locale locale) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale)
        );

        log.error("handleNotFoundException: {}", response);

        return response;
    }

    @ExceptionHandler({
            AccessDeniedException.class
    })
    public ProblemDetail handleForbiddenException(RuntimeException ex, Locale locale) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale)
        );

        log.error("handleForbiddenException: {}", response);

        return response;
    }
}
