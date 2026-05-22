package com.mysawit.mysawit_auth.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum Role {
    ADMIN,
    MANDOR,
    BURUH,
    @JsonAlias("SUPIR_TRUK")
    SUPIR;

    @JsonCreator
    public static Role fromString(final String value) {
        if (value == null) {
            return null;
        }

        final String normalized = value.toUpperCase(Locale.ENGLISH).trim();

        final String jsonAlias = "SUPIR_TRUK";
        if (jsonAlias.equals(normalized)) {
            return SUPIR;
        }

        return Role.valueOf(normalized);
    }
}
