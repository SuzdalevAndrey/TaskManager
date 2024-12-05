package ru.andreyszdlv.taskmanager.controller.advice;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.andreyszdlv.taskmanager.exception.InvalidTokenException;
import ru.andreyszdlv.taskmanager.exception.UserAlreadyExsitsException;
import ru.andreyszdlv.taskmanager.exception.UserNotFoundException;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler({
            UserAlreadyExsitsException.class,
            InvalidTokenException.class
    })
    public ProblemDetail handleConflictException(RuntimeException ex, Locale locale) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                messageSource.getMessage(ex.getMessage(), null, locale)
        );
    }

    @ExceptionHandler({BindException.class})
    public ProblemDetail handleConflictException(BindException ex, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                messageSource.getMessage("validation.error.title", null, locale)
        );
        problemDetail.setProperty(
                "errors",
                ex.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList()
        );
        return problemDetail;
    }

    @ExceptionHandler({
            UserNotFoundException.class
    })
    public ProblemDetail handleNotFoundException(RuntimeException ex, Locale locale) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                messageSource.getMessage(ex.getMessage(), null, locale)
        );
    }

}
