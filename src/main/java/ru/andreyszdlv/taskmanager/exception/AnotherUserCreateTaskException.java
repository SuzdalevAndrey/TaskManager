package ru.andreyszdlv.taskmanager.exception;

public class AnotherUserCreateTaskException extends RuntimeException {
    public AnotherUserCreateTaskException(String message) {
        super(message);
    }
}
