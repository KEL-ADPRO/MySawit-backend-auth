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

    private final String expectedClientId;
    private final RestTemplate restTemplate;

    public GoogleTokenVerifier(
            final RestTemplate restTemplate,
            @Value("${google.client-id}") final String expectedClientId) {
        this.restTemplate = restTemplate;
        this.expectedClientId = expectedClientId;
    }

    public GoogleUserInfo verify(final String idToken) {
        final Map<String, Object> payload = fetchPayload(idToken);
        validateAudience(payload);

        final String googleId = ObjectUtils.toString(payload.get("sub"));
        final String email = ObjectUtils.toString(payload.get("email"));

        Object nameObj = payload.get("name");
        final String name = (nameObj != null) ? nameObj.toString() : email;

        if (googleId.isBlank() || email.isBlank()) {
            throw new IllegalArgumentException("Google token is missing required fields (sub or email)");
        }

        return GoogleUserInfo.builder()
                .googleId(googleId)
                .email(email)
                .name(name)
                .build();
    }

    private Map<String, Object> fetchPayload(final String idToken) {
        try {
            final ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    GOOGLE_TOKENINFO_URL + idToken,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            if (response.getBody() == null) {
                throw new IllegalArgumentException("Empty response body from Google API");
            }

            return response.getBody();
        } catch (RestClientException e) {
            throw new IllegalArgumentException("Invalid Google ID token connection error", e);
        }
    }

    private void validateAudience(final Map<String, Object> payload) {
        final String aud = ObjectUtils.toString(payload.get("aud"));

        if (aud.isBlank()) {
            throw new IllegalArgumentException("Google token audience (aud) is missing");
        }

        final List<String> expectedIds = Arrays.asList(expectedClientId.split("\\s*,\\s*"));
        final List<String> tokenAudiences = Arrays.asList(aud.split("\\s*,\\s*"));
        final boolean isMatch = tokenAudiences.stream().anyMatch(expectedIds::contains);

        if (!isMatch) {
            throw new IllegalArgumentException("Google token audience does not match this application");
        }
    }

    private static class ObjectUtils {
        public static String toString(Object obj) {
            return (obj == null) ? "" : obj.toString().trim();
        }
    }
}