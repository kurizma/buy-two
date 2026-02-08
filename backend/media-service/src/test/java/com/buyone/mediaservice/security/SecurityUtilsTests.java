package com.buyone.mediaservice.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityUtilsTests {
    
    private final SecurityUtils securityUtils = new SecurityUtils();
    
    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }
    
    private void setJwtAuth(String subject, String role) {
        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                Map.of("sub", subject, "role", role)
        );
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    @Test
    void getCurrentUserId_returnsSubject_whenAuthenticated() {
        setJwtAuth("alice@example.com", "SELLER");
        assertThat(securityUtils.getCurrentUserId()).isEqualTo("alice@example.com");
    }
    
    @Test
    void getCurrentUserId_returnsNull_whenNoAuthentication() {
        assertThat(securityUtils.getCurrentUserId()).isNull();
    }
    
    @Test
    void getCurrentUserRole_returnsRole_whenAuthenticated() {
        setJwtAuth("alice@example.com", "CLIENT");
        assertThat(securityUtils.getCurrentUserRole()).isEqualTo("CLIENT");
    }
    
    @Test
    void getCurrentUserRole_returnsNull_whenNoAuthentication() {
        assertThat(securityUtils.getCurrentUserRole()).isNull();
    }
    
    @Test
    void isCurrentUserSeller_returnsTrue_whenSeller() {
        setJwtAuth("seller@example.com", "SELLER");
        assertThat(securityUtils.isCurrentUserSeller()).isTrue();
    }
    
    @Test
    void isCurrentUserSeller_returnsFalse_whenClient() {
        setJwtAuth("client@example.com", "CLIENT");
        assertThat(securityUtils.isCurrentUserSeller()).isFalse();
    }
    
    @Test
    void isCurrentUserSeller_returnsFalse_whenNoAuth() {
        assertThat(securityUtils.isCurrentUserSeller()).isFalse();
    }
}
