package com.mysawit.mysawit_auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.time.Instant;

@Getter
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class AccountLockedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Instant lockedUntil;

    public AccountLockedException(final Instant lockedUntil) {
        super("Account is temporarily locked due to too many failed login attempts. Try again later.");
        this.lockedUntil = lockedUntil;
    }

}