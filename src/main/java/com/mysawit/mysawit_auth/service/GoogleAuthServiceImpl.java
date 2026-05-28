package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.request.AuthRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.model.AuthProvider;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategy;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategyFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {
    private final AuthStrategyFactory strategyFactory;

    @Override
    @Transactional
    public AuthResponse loginOrRegister(final AuthRequest request) {
        final AuthStrategy strategy = strategyFactory.resolve(AuthProvider.GOOGLE);
        return strategy.authenticate(request);
    }
}
