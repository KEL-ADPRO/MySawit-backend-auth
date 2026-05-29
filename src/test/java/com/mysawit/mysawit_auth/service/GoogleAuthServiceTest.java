package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.request.AuthRequest;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.model.AuthProvider;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategy;
import com.mysawit.mysawit_auth.service.strategy.AuthStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoogleAuthServiceTest {
    @Mock
    private AuthStrategyFactory strategyFactory;

    @Mock
    private AuthStrategy googleStrategy;

    @InjectMocks
    private GoogleAuthServiceImpl authService;

    private AuthResponse mandorResponse;

    @BeforeEach
    void setUp() {
        mandorResponse = AuthResponse.builder()
                .token("dummy.jwt.token")
                .username("Mandor Sawit")
                .name("Burhan")
                .email("burhan@gmail.com")
                .role(Role.MANDOR)
                .nomorSertifMandor("CERT-001")
                .build();
    }

    @Test
    void googleLoginOrRegisterSuccess() {
        String PLACEHOLDER_TOKEN = "placeholder.google.id.token";
        final AuthRequest request = AuthRequest.builder()
                .idToken(PLACEHOLDER_TOKEN)
                .role(Role.MANDOR)
                .build();

        doReturn(googleStrategy).when(strategyFactory).resolve(AuthProvider.GOOGLE);
        when(googleStrategy.authenticate(any(AuthRequest.class))).thenReturn(mandorResponse);

        final AuthResponse response = authService.loginOrRegister(request);

        assertNotNull(response);
        assertEquals("dummy.jwt.token", response.getToken());
        assertEquals("Burhan", response.getName());
        assertEquals("burhan@gmail.com", response.getEmail());
        assertEquals(Role.MANDOR, response.getRole());
        assertEquals("CERT-001", response.getNomorSertifMandor());

        verify(strategyFactory).resolve(AuthProvider.GOOGLE);
        verify(googleStrategy).authenticate(any(AuthRequest.class));
    }
}
