package com.mysawit.mysawit_auth.util;

import com.mysawit.mysawit_auth.dto.GoogleUserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class GoogleTokenVerifier {
    private static final String GOOGLE_TOKENINFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";

    @Value("${google.client-id}")
    private String expectedClientId;

    private final RestTemplate restTemplate;

    public GoogleTokenVerifier() {
        this.restTemplate = new RestTemplate();
    }

    public GoogleTokenVerifier(final RestTemplate restTemplate, final String expectedClientId) {
        this.restTemplate = restTemplate;
        this.expectedClientId = expectedClientId;
    }

    public GoogleUserInfo verify(final String idToken) {
        final Map<String, String> payload = fetchPayload(idToken);
        validateAudience(payload);

        final String googleId = payload.get("sub");
        final String email = payload.get("email");
        final String name = payload.getOrDefault("name", email);

        if (googleId == null || googleId.isBlank() || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Google token is missing required fields");
        }

        return GoogleUserInfo.builder()
                .googleId(googleId)
                .email(email)
                .name(name)
                .build();
    }

    private Map<String, String> fetchPayload(final String idToken) {
        try {
            final ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                    GOOGLE_TOKENINFO_URL + idToken,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getBody() == null) {
                throw new IllegalArgumentException("Empty response");
            }

            return response.getBody();
        } catch (RestClientException e) {
            throw new IllegalArgumentException("Invalid Google ID token", e);
        }
    }

    private void validateAudience(final Map<String, String> payload) {
        final String aud = payload.get("aud");

        if (aud == null || aud.isBlank()) {
            throw new IllegalArgumentException("Google token audience is missing");
        }

        final List<String> expectedIds = Arrays.asList(expectedClientId.split("\\s*,\\s*"));
        final List<String> tokenAudiences = Arrays.asList(aud.split("\\s*,\\s*"));
        final boolean isMatch = tokenAudiences.stream().anyMatch(expectedIds::contains);

        if (!isMatch) {
            throw new IllegalArgumentException("Google token audience does not match this application");
        }
    }
}