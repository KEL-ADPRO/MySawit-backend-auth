package com.mysawit.mysawit_auth.util;

public interface TokenBlacklist {
    void blacklist(final String token);
    boolean isBlacklisted(final String token);
}
