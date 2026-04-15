package com.mysawit.mysawit_auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCredentialException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidCredentialException() {
        super("Invalid credentials");
    }
}
