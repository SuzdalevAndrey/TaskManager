package ru.andreyszdlv.taskmanager.exception;

import lombok.Getter;

@Getter
public class UserAlreadyExsitsException extends RuntimeException {

    public UserAlreadyExsitsException(String message) {
        super(message);
    }
}
