package com.mysawit.mysawit_auth.service;

public interface LoginAttemptService {
    void recordSuccess(String email);
    void recordFailure(String email);
    boolean isLocked(String email);
    void assertNotLocked(String email);
}
