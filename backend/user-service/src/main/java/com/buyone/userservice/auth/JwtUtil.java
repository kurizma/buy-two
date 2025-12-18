package com.buyone.userservice.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    
    // Inject from app config/secrets in production!
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;
    
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    public String generateToken(String userId, String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("id", userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSecretKey())
                .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("JWT Validation failed: " + e.getMessage());
            return false;
        }
    }
    
    public String extractEmail(String token) {
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token);
        return claimsJws.getPayload().getSubject();
    }
    
    public String extractRole(String token) {
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token);
        return claimsJws.getPayload().get("role", String.class);
    }
}
