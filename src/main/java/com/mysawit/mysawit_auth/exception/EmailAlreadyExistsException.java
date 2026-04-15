package com.mysawit.mysawit_auth.exception;

import java.io.Serial;

public class EmailAlreadyExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public EmailAlreadyExistsException(String email) {
        super("Email " + email + " is already registered");
    }
}
