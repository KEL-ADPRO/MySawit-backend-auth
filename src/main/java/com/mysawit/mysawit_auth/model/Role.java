package com.mysawit.mysawit_auth.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ADMIN,
    MANDOR,
    BURUH,
    @JsonAlias("SUPIR_TRUK")
    SUPIR;

    /**
     * Case-insensitive deserializer: accepts SUPIR, SUPIR_TRUK, supir, supir_truk, etc.
     */
    @JsonCreator
    public static Role fromString(final String value) {
        if (value == null) return null;
        final String normalized = value.toUpperCase().trim();
        if ("SUPIR_TRUK".equals(normalized)) return SUPIR;
        return Role.valueOf(normalized);
    }
}
