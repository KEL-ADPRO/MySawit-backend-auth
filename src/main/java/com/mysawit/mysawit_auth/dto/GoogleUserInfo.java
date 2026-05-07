package com.mysawit.mysawit_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserInfo {
    private String googleId;
    private String email;
    private String name;
}
