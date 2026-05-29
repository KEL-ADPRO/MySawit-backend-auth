package com.mysawit.mysawit_auth.service.strategy;

import com.mysawit.mysawit_auth.model.AuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthStrategyFactoryTest {

    @Mock
    private AuthStrategy passwordStrategy;

    @Mock
    private AuthStrategy googleStrategy;

    private AuthStrategyFactory authStrategyFactory;

    @BeforeEach
    void setUp() {
        when(passwordStrategy.getProviderType()).thenReturn(AuthProvider.PASSWORD);
        when(googleStrategy.getProviderType()).thenReturn(AuthProvider.GOOGLE);

        authStrategyFactory = new AuthStrategyFactory(Arrays.asList(passwordStrategy, googleStrategy));
    }

    @Test
    void resolvePasswordStrategy() {
        final AuthStrategy strategy = authStrategyFactory.resolve(AuthProvider.PASSWORD);

        assertNotNull(strategy);
        assertEquals(passwordStrategy, strategy);
    }

    @Test
    void resolveGoogleStrategy() {
        final AuthStrategy strategy = authStrategyFactory.resolve(AuthProvider.GOOGLE);

        assertNotNull(strategy);
        assertEquals(googleStrategy, strategy);
    }

    @Test
    void resolveUnknownStrategy() {
        AuthStrategyFactory limitedFactory = new AuthStrategyFactory(List.of(passwordStrategy));

        assertThrows(IllegalArgumentException.class, () -> limitedFactory.resolve(AuthProvider.GOOGLE));
    }
    
    @Test
    void resolveNullProvider() {
        assertThrows(IllegalArgumentException.class, () -> authStrategyFactory.resolve(null));
    }
}
