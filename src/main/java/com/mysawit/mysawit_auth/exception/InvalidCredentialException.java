package com.mysawit.mysawit_auth.exception;

public class InvalidCredentialException extends RuntimeException {
    public InvalidCredentialException() {
        super("Invalid credentials");
    }
}
