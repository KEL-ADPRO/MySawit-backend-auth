package com.mysawit.mysawit_auth.exception;

import java.io.Serial;

public class InvalidCredentialException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidCredentialException() {
        super("Invalid credentials");
    }
}
