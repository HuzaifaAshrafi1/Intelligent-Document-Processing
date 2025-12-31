package com.legal.pipeline.domain.dto;

import java.util.Collection;

/**
 * DTO for authentication response.
 */
public class AuthResponse {
    private String token;
    private String username;
    private Collection<?> authorities;

    public AuthResponse() {}

    public AuthResponse(String token, String username, Collection<?> authorities) {
        this.token = token;
        this.username = username;
        this.authorities = authorities;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Collection<?> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<?> authorities) {
        this.authorities = authorities;
    }
}
