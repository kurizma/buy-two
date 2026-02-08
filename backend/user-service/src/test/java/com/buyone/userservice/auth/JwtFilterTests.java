package com.buyone.userservice.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTests {
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private FilterChain filterChain;
    
    @InjectMocks
    private JwtFilter jwtFilter;
    
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    
    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }
    
    @Test
    void doFilterInternal_setsAuthentication_whenValidBearerToken() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer valid-token");
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(jwtUtil.extractEmail("valid-token")).thenReturn("alice@example.com");
        when(jwtUtil.extractRole("valid-token")).thenReturn("CLIENT");
        
        jwtFilter.doFilterInternal(request, response, filterChain);
        
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo("alice@example.com");
        assertThat(auth.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));
        verify(filterChain).doFilter(request, response);
    }
    
    @Test
    void doFilterInternal_doesNotSetAuth_whenNoAuthorizationHeader() throws ServletException, IOException {
        // No Authorization header
        jwtFilter.doFilterInternal(request, response, filterChain);
        
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
    
    @Test
    void doFilterInternal_doesNotSetAuth_whenHeaderNotBearer() throws ServletException, IOException {
        request.addHeader("Authorization", "Basic some-creds");
        
        jwtFilter.doFilterInternal(request, response, filterChain);
        
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
    
    @Test
    void doFilterInternal_doesNotSetAuth_whenTokenInvalid() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer bad-token");
        when(jwtUtil.validateToken("bad-token")).thenReturn(false);
        
        jwtFilter.doFilterInternal(request, response, filterChain);
        
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
    
    @Test
    void shouldNotFilter_returnsTrue_forLoginPath() {
        request.setRequestURI("/auth/login");
        assertThat(jwtFilter.shouldNotFilter(request)).isTrue();
    }
    
    @Test
    void shouldNotFilter_returnsTrue_forRegisterPath() {
        request.setRequestURI("/auth/register");
        assertThat(jwtFilter.shouldNotFilter(request)).isTrue();
    }
    
    @Test
    void shouldNotFilter_returnsFalse_forApiPath() {
        request.setRequestURI("/api/users/me");
        assertThat(jwtFilter.shouldNotFilter(request)).isFalse();
    }
}
