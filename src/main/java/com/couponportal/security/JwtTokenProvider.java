package com.couponportal.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HexFormat;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long      accessTokenExpiry;

    public JwtTokenProvider(
            @Value("${jwt.secret}")               String secret,
            @Value("${jwt.access-token-expiry}")  long   accessTokenExpiry
    ) {
        byte[] keyBytes   = HexFormat.of().parseHex(secret);
        this.secretKey         = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiry = accessTokenExpiry;
    }

    // ── Generate access token ───────────────────────────────

    public String generateToken(UserDetails userDetails, Long tenantId) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles",    userDetails.getAuthorities()
                        .stream().map(Object::toString).toList())
                .claim("tenantId", tenantId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiry))
                .signWith(secretKey)
                .compact();
    }

    // ── Extract fields ──────────────────────────────────────

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Long extractTenantId(String token) {
        return parseClaims(token).get("tenantId", Long.class);
    }

    // ── Validate ────────────────────────────────────────────

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    // ── Internal ────────────────────────────────────────────

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
