package com.mysawit.mysawit_auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitExceededException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public RateLimitExceededException() {
        super("Too many requests. Please slow down and try again later.");
    }
}