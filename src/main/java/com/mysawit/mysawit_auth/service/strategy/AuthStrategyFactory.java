package com.mysawit.mysawit_auth.service.strategy;

import com.mysawit.mysawit_auth.model.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AuthStrategyFactory {
    private final Map<AuthProvider,  AuthStrategy<?>> strategies;

    public AuthStrategyFactory(final List<AuthStrategy<?>> strategyList) {
        this.strategies = strategyList.stream().collect(Collectors.toMap(AuthStrategy::getProviderType, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <T> AuthStrategy<T> resolve(final AuthProvider provider) {
        final AuthStrategy<?> strategy = strategies.get(provider);
        if (strategy == null) {
            throw new IllegalArgumentException("No authentication strategy registered for provider: " + provider);
        }
        return (AuthStrategy<T>) strategy;
    }
}
