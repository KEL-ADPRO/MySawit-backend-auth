package com.mysawit.mysawit_auth.service;

import com.mysawit.mysawit_auth.model.Role;

import java.util.UUID;

public interface TokenAuthorizationService {
    UUID requireRole(String token, Role requiredRole);
}