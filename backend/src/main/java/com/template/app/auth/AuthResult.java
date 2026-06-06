package com.template.app.auth;

import com.template.app.auth.dto.AuthResponse;

public record AuthResult(AuthResponse response, String refreshToken) {}
