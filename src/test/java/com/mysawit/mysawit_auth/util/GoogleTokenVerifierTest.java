package com.mysawit.mysawit_auth.util;

import com.mysawit.mysawit_auth.dto.GoogleUserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoogleTokenVerifierTest {

    @Mock
    private RestTemplate restTemplate;

    private GoogleTokenVerifier googleTokenVerifier;

    private static final String CLIENT_ID = "test-client-id";
    private static final String GOOGLE_TOKENINFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";

    @BeforeEach
    void setUp() {
        googleTokenVerifier = new GoogleTokenVerifier(restTemplate, CLIENT_ID);
    }

    @Test
    void verifySuccess() {
        final String idToken = "valid-token";
        final Map<String, String> payload = new HashMap<>();
        payload.put("sub", "google-user-id");
        payload.put("email", "test@gmail.com");
        payload.put("name", "Test User");
        payload.put("aud", CLIENT_ID);

        when(restTemplate.exchange(
                eq(GOOGLE_TOKENINFO_URL + idToken),
                eq(HttpMethod.GET),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, String>>>any()
        )).thenReturn(ResponseEntity.ok(payload));

        GoogleUserInfo userInfo = googleTokenVerifier.verify(idToken);

        assertNotNull(userInfo);
        assertEquals("google-user-id", userInfo.getGoogleId());
        assertEquals("test@gmail.com", userInfo.getEmail());
        assertEquals("Test User", userInfo.getName());
    }

    @Test
    void verifyFallbackName() {
        final String idToken = "valid-token";
        final Map<String, String> payload = new HashMap<>();
        payload.put("sub", "google-user-id");
        payload.put("email", "test@gmail.com");
        payload.put("aud", CLIENT_ID);

        when(restTemplate.exchange(
                eq(GOOGLE_TOKENINFO_URL + idToken),
                eq(HttpMethod.GET),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, String>>>any()
        )).thenReturn(ResponseEntity.ok(payload));

        GoogleUserInfo userInfo = googleTokenVerifier.verify(idToken);

        assertNotNull(userInfo);
        assertEquals("test@gmail.com", userInfo.getName(), "Name should fallback to email if missing");
    }

    @Test
    void verifyRestClientException() {
        final String idToken = "invalid-token";

        when(restTemplate.exchange(
                eq(GOOGLE_TOKENINFO_URL + idToken),
                eq(HttpMethod.GET),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, String>>>any()
        )).thenThrow(new RestClientException("Bad Request"));

        assertThrows(IllegalArgumentException.class, () -> googleTokenVerifier.verify(idToken));
    }

    @Test
    void verifyEmptyResponse() {
        final String idToken = "valid-token";

        when(restTemplate.exchange(
                eq(GOOGLE_TOKENINFO_URL + idToken),
                eq(HttpMethod.GET),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, String>>>any()
        )).thenReturn(ResponseEntity.ok(null));

        assertThrows(IllegalArgumentException.class, () -> googleTokenVerifier.verify(idToken));
    }

    @Test
    void verifyMissingAudience() {
        final String idToken = "valid-token";
        final Map<String, String> payload = new HashMap<>();
        payload.put("sub", "google-user-id");
        payload.put("email", "test@gmail.com");

        when(restTemplate.exchange(
                eq(GOOGLE_TOKENINFO_URL + idToken),
                eq(HttpMethod.GET),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, String>>>any()
        )).thenReturn(ResponseEntity.ok(payload));

        assertThrows(IllegalArgumentException.class, () -> googleTokenVerifier.verify(idToken));
    }

    @Test
    void verifyWrongAudience() {
        final String idToken = "valid-token";
        final Map<String, String> payload = new HashMap<>();
        payload.put("sub", "google-user-id");
        payload.put("email", "test@gmail.com");
        payload.put("aud", "wrong-client-id");

        when(restTemplate.exchange(
                eq(GOOGLE_TOKENINFO_URL + idToken),
                eq(HttpMethod.GET),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, String>>>any()
        )).thenReturn(ResponseEntity.ok(payload));

        assertThrows(IllegalArgumentException.class, () -> googleTokenVerifier.verify(idToken));
    }

    @Test
    void verifyMissingRequiredFields() {
        final String idToken = "valid-token";
        final Map<String, String> payload = new HashMap<>();
        payload.put("aud", CLIENT_ID);

        when(restTemplate.exchange(
                eq(GOOGLE_TOKENINFO_URL + idToken),
                eq(HttpMethod.GET),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, String>>>any()
        )).thenReturn(ResponseEntity.ok(payload));

        assertThrows(IllegalArgumentException.class, () -> googleTokenVerifier.verify(idToken));
    }
}
