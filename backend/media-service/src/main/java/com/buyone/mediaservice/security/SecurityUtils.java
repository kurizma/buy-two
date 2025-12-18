package com.buyone.mediaservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    
    public String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject(); // 'sub' from JWT, which user-service set as email
        }
        return null;
    }
    
    public String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("role"); // exactly as user-service sets it
        }
        return null;
    }
    
    public boolean isCurrentUserSeller() {
        String role = getCurrentUserRole();
        return "SELLER".equalsIgnoreCase(role);
    }
    
}
