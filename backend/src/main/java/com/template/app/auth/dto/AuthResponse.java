package com.template.app.auth.dto;

public record AuthResponse(
        String accessToken,
        UserResponse user
) {}
