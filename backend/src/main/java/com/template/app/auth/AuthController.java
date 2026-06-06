package com.template.app.auth;

import com.template.app.auth.dto.AuthResponse;
import com.template.app.auth.dto.LoginRequest;
import com.template.app.auth.dto.RegisterRequest;
import com.template.app.auth.dto.UserResponse;
import com.template.app.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final long refreshExpirationMs;

    public AuthController(
            AuthService authService,
            @Value("${app.jwt.refresh-expiration-ms}") long refreshExpirationMs
    ) {
        this.authService = authService;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        AuthResult result = authService.register(request);
        setRefreshCookie(response, result.refreshToken());
        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        AuthResult result = authService.login(request);
        setRefreshCookie(response, result.refreshToken());
        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = AuthService.REFRESH_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        AuthResult result = authService.refresh(refreshToken);
        setRefreshCookie(response, result.refreshToken());
        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = AuthService.REFRESH_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        authService.logout(refreshToken);
        clearRefreshCookie(response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.toUserResponse(user));
    }

    private void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(AuthService.REFRESH_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/api/auth");
        cookie.setMaxAge((int) (refreshExpirationMs / 1000));
        response.addCookie(cookie);
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(AuthService.REFRESH_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
