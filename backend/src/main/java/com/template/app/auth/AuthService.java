package com.template.app.auth;

import com.template.app.auth.dto.AuthResponse;
import com.template.app.auth.dto.LoginRequest;
import com.template.app.auth.dto.RegisterRequest;
import com.template.app.auth.dto.UserResponse;
import com.template.app.user.User;
import com.template.app.user.UserRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    public static final String REFRESH_COOKIE_NAME = "refresh_token";

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long refreshExpirationMs;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${app.jwt.refresh-expiration-ms}") long refreshExpirationMs
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Transactional
    public AuthResult register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User();
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        User saved = userRepository.save(user);

        return buildAuthResult(saved);
    }

    @Transactional
    public AuthResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return buildAuthResult(user);
    }

    @Transactional
    public AuthResult refresh(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token missing");
        }

        String tokenHash = hashToken(rawRefreshToken);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return buildAuthResult(refreshToken.getUser());
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            return;
        }

        refreshTokenRepository.findByTokenHashAndRevokedFalse(hashToken(rawRefreshToken))
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt());
    }

    private AuthResult buildAuthResult(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = persistRefreshToken(user);
        return new AuthResult(new AuthResponse(accessToken, toUserResponse(user)), refreshToken);
    }

    private String persistRefreshToken(User user) {
        String rawToken = generateRawToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashToken(rawToken));
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }
}
