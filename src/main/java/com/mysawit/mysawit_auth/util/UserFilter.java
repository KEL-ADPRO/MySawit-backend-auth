package com.mysawit.mysawit_auth.util;

import com.mysawit.mysawit_auth.model.Role;

public record UserFilter(String name, String email, Role role) {
    public boolean hasName()  { return name != null && !name.isBlank(); }
    public boolean hasEmail() { return email != null && !email.isBlank(); }
    public boolean hasRole()  { return role != null; }
}
