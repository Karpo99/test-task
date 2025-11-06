package com.userservice.exception;

public class TokenAlreadyInvalidatedException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = """
            Token is already invalidated! 
            """;

    public TokenAlreadyInvalidatedException() {
        super(DEFAULT_MESSAGE);
    }

    public TokenAlreadyInvalidatedException(final String tokenID) {
        super(DEFAULT_MESSAGE + "TokenID:" + tokenID);
    }
}
