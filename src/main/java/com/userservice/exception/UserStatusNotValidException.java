package com.userservice.exception;

public class UserStatusNotValidException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = """
            Invalid user status!
            """;

    public UserStatusNotValidException() {
        super(DEFAULT_MESSAGE);
    }

    public UserStatusNotValidException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}
