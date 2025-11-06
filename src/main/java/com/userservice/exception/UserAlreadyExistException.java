package com.userservice.exception;

public class UserAlreadyExistException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = """
            User already exist!
            """;

    public UserAlreadyExistException() {
        super(DEFAULT_MESSAGE);
    }

    public UserAlreadyExistException(String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}
