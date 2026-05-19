package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.dto.request.GoogleAuthRequest;
import com.mysawit.mysawit_auth.dto.GoogleUserInfo;
import com.mysawit.mysawit_auth.dto.response.AuthResponse;
import com.mysawit.mysawit_auth.model.Role;
import com.mysawit.mysawit_auth.model.User;
import com.mysawit.mysawit_auth.repository.AuthRepository;
import com.mysawit.mysawit_auth.util.GoogleTokenVerifier;
import com.mysawit.mysawit_auth.util.JwtUtil;
import com.mysawit.mysawit_auth.util.PasswordHasher;
import com.mysawit.mysawit_auth.validator.RegistrationValidator;
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
    private AuthRepository authRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GoogleTokenVerifier googleTokenVerifier;

    @Mock
    private RegistrationValidator registrationValidator;

    @InjectMocks
    private GoogleAuthServiceImpl googleAuthService;

    private User googleMandorUser;
    private GoogleUserInfo googleMandorInfo;
    private final String PLACEHOLDER_TOKEN = "placeholder.google.id.token";
    private final String GOOGLE_ID = "google-sub-123";

    @BeforeEach
    void setUp() {
        googleMandorUser = User.builder()
                .googleId(GOOGLE_ID)
                .username("Mandor Sawit")
                .name("Burhan")
                .email("burhan@gmail.com")
                .password("mandor123")
                .role(Role.MANDOR)
                .nomorSertifMandor("CERT-001")
                .build();

        googleMandorInfo = GoogleUserInfo.builder()
                .googleId(GOOGLE_ID)
                .email("burhan@gmail.com")
                .name("Burhan")
                .build();
    }

    @Test
    void googleLoginSuccess() {
        when(googleTokenVerifier.verify(PLACEHOLDER_TOKEN)).thenReturn(googleMandorInfo);
        when(authRepository.findByGoogleId(GOOGLE_ID)).thenReturn(googleMandorUser);
        when(jwtUtil.generateToken(googleMandorUser.getId(), googleMandorUser.getRole())).thenReturn("dummy.jwt.token");

        final GoogleAuthRequest request = GoogleAuthRequest.builder()
                .idToken(PLACEHOLDER_TOKEN)
                .role(Role.MANDOR)
                .build();

        final AuthResponse response = googleAuthService.loginOrRegister(request);

        assertNotNull(response);
        assertEquals("dummy.jwt.token", response.getToken());
        assertEquals("Burhan", response.getName());
        assertEquals("burhan@gmail.com", response.getEmail());
        assertEquals(Role.MANDOR, response.getRole());

        verify(authRepository, never()).save(any(User.class));
    }

    @Test
    void googleRegisterSuccess() {
        when(googleTokenVerifier.verify(PLACEHOLDER_TOKEN)).thenReturn(googleMandorInfo);
        when(authRepository.findByGoogleId(GOOGLE_ID)).thenReturn(null);
        when(authRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtUtil.generateToken(any(), any())).thenReturn("dummy.jwt.token");

        final GoogleAuthRequest request = GoogleAuthRequest.builder()
                .idToken(PLACEHOLDER_TOKEN)
                .username("Mandor Sawit")
                .role(Role.MANDOR)
                .nomorSertifMandor("CERT-001")
                .build();

        final AuthResponse response = googleAuthService.loginOrRegister(request);

        assertNotNull(response);
        assertEquals("dummy.jwt.token", response.getToken());
        assertEquals("Mandor Sawit", response.getUsername());
        assertEquals("Burhan", response.getName());
        assertEquals(Role.MANDOR, response.getRole());
        assertEquals("CERT-001", response.getNomorSertifMandor());

        verify(registrationValidator).validateRequiredFields(request.getUsername(), googleMandorInfo.getName(), googleMandorInfo.getEmail(), request.getRole());
        verify(registrationValidator).assertEmailUnique(googleMandorInfo.getEmail());
        verify(registrationValidator).assertMandorCertPresent(request.getRole(), request.getNomorSertifMandor());
        verify(authRepository, times(1)).save(any(User.class));
    }
}
