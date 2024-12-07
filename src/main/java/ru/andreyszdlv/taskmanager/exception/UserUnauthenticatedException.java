package ru.andreyszdlv.taskmanager.exception;

public class UserUnauthenticatedException extends RuntimeException {
    public UserUnauthenticatedException(String message) {
        super(message);
    }
}
