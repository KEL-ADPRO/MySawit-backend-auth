package com.mysawit.mysawit_auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

public class SelfDeletionException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public SelfDeletionException() {
        super("Admin cannot delete self");
    }
}
