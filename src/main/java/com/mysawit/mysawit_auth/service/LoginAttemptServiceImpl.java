package com.mysawit.mysawit_auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService {

    @Override
    public void recordSuccess(String email) {

    }

    @Override
    public void recordFailure(String email) {

    }

    @Override
    public boolean isLocked(String email) {
        return false;
    }

    @Override
    public void assertNotLocked(String email) {

    }
}
