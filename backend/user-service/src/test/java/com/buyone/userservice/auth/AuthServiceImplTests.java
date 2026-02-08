package com.buyone.userservice.auth;

import com.buyone.userservice.exception.AuthException;
import com.buyone.userservice.exception.ConflictException;
import com.buyone.userservice.exception.ResourceNotFoundException;
import com.buyone.userservice.model.Role;
import com.buyone.userservice.model.User;
import com.buyone.userservice.repository.UserRepository;
import com.buyone.userservice.request.LoginRequest;
import com.buyone.userservice.request.RegisterUserRequest;
import com.buyone.userservice.response.LoginResponse;
import com.buyone.userservice.response.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTests {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @InjectMocks
    private AuthServiceImpl authService;
    
    // -------- register --------
    
    @Test
    void register_savesUserAndReturnsResponse_whenValid() {
        RegisterUserRequest req = RegisterUserRequest.builder()
                .name("Alice")
                .email("alice@example.com")
                .password("secret123")
                .role(Role.CLIENT)
                .avatar("avatar.png")
                .build();
        
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("ENC(secret123)");
        
        UserResponse response = authService.register(req);
        
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        
        assertThat(saved.getEmail()).isEqualTo("alice@example.com");
        assertThat(saved.getPassword()).isEqualTo("ENC(secret123)");
        assertThat(saved.getRole()).isEqualTo(Role.CLIENT);
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
        assertThat(response.getName()).isEqualTo("Alice");
    }
    
    @Test
    void register_throwsConflict_whenEmailAlreadyExists() {
        RegisterUserRequest req = RegisterUserRequest.builder()
                .name("Bob")
                .email("existing@example.com")
                .password("secret123")
                .role(Role.SELLER)
                .build();
        
        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(new User()));
        
        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email already exists");
    }
    
    // -------- login --------
    
    @Test
    void login_returnsTokenAndUserResponse_whenCredentialsValid() {
        LoginRequest req = LoginRequest.builder()
                .email("alice@example.com")
                .password("secret123")
                .build();
        
        User user = User.builder()
                .id("u1")
                .name("Alice")
                .email("alice@example.com")
                .password("ENC(secret123)")
                .role(Role.CLIENT)
                .avatar("avatar.png")
                .build();
        
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "ENC(secret123)")).thenReturn(true);
        when(jwtUtil.generateToken("u1", "alice@example.com", "CLIENT")).thenReturn("jwt-token-123");
        
        LoginResponse response = authService.login(req);
        
        assertThat(response.getToken()).isEqualTo("jwt-token-123");
        assertThat(response.getMessage()).isEqualTo("Login successful");
        assertThat(response.getUser().getId()).isEqualTo("u1");
        assertThat(response.getUser().getEmail()).isEqualTo("alice@example.com");
    }
    
    @Test
    void login_throwsResourceNotFound_whenEmailNotRegistered() {
        LoginRequest req = LoginRequest.builder()
                .email("unknown@example.com")
                .password("secret")
                .build();
        
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No user found with email");
    }
    
    @Test
    void login_throwsAuthException_whenPasswordMismatch() {
        LoginRequest req = LoginRequest.builder()
                .email("alice@example.com")
                .password("wrongpass")
                .build();
        
        User user = User.builder()
                .id("u1")
                .email("alice@example.com")
                .password("ENC(secret)")
                .role(Role.CLIENT)
                .build();
        
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "ENC(secret)")).thenReturn(false);
        
        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("Invalid email or password");
    }
}
