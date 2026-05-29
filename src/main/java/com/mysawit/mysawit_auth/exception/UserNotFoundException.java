package com.mysawit.mysawit_auth.exception;

import java.io.Serial;
import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(UUID userId) {
        super("User not found: " + userId);
    }
}
