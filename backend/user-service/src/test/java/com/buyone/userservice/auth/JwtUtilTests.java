package com.buyone.userservice.auth;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTests {
    
    private JwtUtil jwtUtil;
    
    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Must be >= 256 bits (32 bytes) for HS256
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "my-super-secret-key-for-testing-jwt-tokens-12345");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 3600000L); // 1 hour
    }
    
    @Test
    void generateToken_returnsNonEmptyString() {
        String token = jwtUtil.generateToken("user-1", "alice@example.com", "CLIENT");
        
        assertThat(token).isNotBlank();
    }
    
    @Test
    void validateToken_returnsTrue_forValidToken() {
        String token = jwtUtil.generateToken("user-1", "alice@example.com", "CLIENT");
        
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }
    
    @Test
    void validateToken_returnsFalse_forInvalidToken() {
        assertThat(jwtUtil.validateToken("invalid.jwt.token")).isFalse();
    }
    
    @Test
    void validateToken_returnsFalse_forNullToken() {
        assertThat(jwtUtil.validateToken(null)).isFalse();
    }
    
    @Test
    void extractEmail_returnsSubject() {
        String token = jwtUtil.generateToken("user-1", "alice@example.com", "CLIENT");
        
        String email = jwtUtil.extractEmail(token);
        
        assertThat(email).isEqualTo("alice@example.com");
    }
    
    @Test
    void extractRole_returnsRoleClaim() {
        String token = jwtUtil.generateToken("user-1", "alice@example.com", "SELLER");
        
        String role = jwtUtil.extractRole(token);
        
        assertThat(role).isEqualTo("SELLER");
    }
    
    @Test
    void extractEmail_andRole_areConsistent_acrossMultipleTokens() {
        String token1 = jwtUtil.generateToken("u1", "a@test.com", "CLIENT");
        String token2 = jwtUtil.generateToken("u2", "b@test.com", "SELLER");
        
        assertThat(jwtUtil.extractEmail(token1)).isEqualTo("a@test.com");
        assertThat(jwtUtil.extractRole(token1)).isEqualTo("CLIENT");
        assertThat(jwtUtil.extractEmail(token2)).isEqualTo("b@test.com");
        assertThat(jwtUtil.extractRole(token2)).isEqualTo("SELLER");
    }
    
    @Test
    void validateToken_returnsFalse_forExpiredToken() {
        // Set expiry to 0ms
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 0L);
        String token = jwtUtil.generateToken("user-1", "alice@example.com", "CLIENT");
        
        // Token expires immediately
        assertThat(jwtUtil.validateToken(token)).isFalse();
    }
}
