package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.request.GoogleAuthRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {

    @Override
    public AuthResponse loginOrRegister(GoogleAuthRequest request) {
        return null;
    }
}
