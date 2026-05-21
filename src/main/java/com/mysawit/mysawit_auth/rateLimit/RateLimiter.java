package com.mysawit.mysawit_auth.rateLimit;

public interface RateLimiter {
    boolean tryConsume(String key);
}
