package com.buyone.userservice.auth;

import com.buyone.userservice.exception.GlobalExceptionHandler;
import com.buyone.userservice.model.Role;
import com.buyone.userservice.response.LoginResponse;
import com.buyone.userservice.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private AuthService authService;
    
    @MockBean
    private JwtFilter jwtFilter;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    // -------- POST /auth/register - validation --------
    
    @Test
    void register_returns400_whenNameIsBlank() throws Exception {
        String body = """
            {
                "name": "",
                "email": "test@example.com",
                "password": "password123",
                "role": "CLIENT"
            }
            """;
        
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.details.name").exists());
    }
    
    @Test
    void register_returns400_whenEmailInvalid() throws Exception {
        String body = """
            {
                "name": "Alice",
                "email": "not-an-email",
                "password": "password123",
                "role": "CLIENT"
            }
            """;
        
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.email").exists());
    }
    
    @Test
    void register_returns400_whenPasswordTooShort() throws Exception {
        String body = """
            {
                "name": "Alice",
                "email": "alice@example.com",
                "password": "short",
                "role": "CLIENT"
            }
            """;
        
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.password").exists());
    }
    
    @Test
    void register_returns400_whenRoleIsNull() throws Exception {
        String body = """
            {
                "name": "Alice",
                "email": "alice@example.com",
                "password": "password123"
            }
            """;
        
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.role").exists());
    }
    
    // -------- POST /auth/login - validation --------
    
    @Test
    void login_returns400_whenEmailIsBlank() throws Exception {
        String body = """
            {
                "email": "",
                "password": "password123"
            }
            """;
        
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.email").exists());
    }
    
    @Test
    void login_returns400_whenPasswordIsBlank() throws Exception {
        String body = """
            {
                "email": "alice@example.com",
                "password": ""
            }
            """;
        
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.password").exists());
    }
    
    // -------- POST /auth/register - happy path --------
    
    @Test
    void register_returns201_whenValid() throws Exception {
        UserResponse created = UserResponse.builder()
                .id("u1").name("Alice").email("alice@example.com").role(Role.CLIENT).build();
        when(authService.register(any())).thenReturn(created);
        
        String body = """
            {
                "name": "Alice",
                "email": "alice@example.com",
                "password": "password123",
                "role": "CLIENT"
            }
            """;
        
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("u1"))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }
    
    // -------- POST /auth/login - happy path --------
    
    @Test
    void login_returns200_whenValid() throws Exception {
        UserResponse user = UserResponse.builder()
                .id("u1").name("Alice").email("alice@example.com").role(Role.CLIENT).build();
        LoginResponse loginRes = LoginResponse.builder()
                .message("Login successful").token("jwt-token-123").user(user).build();
        when(authService.login(any())).thenReturn(loginRes);
        
        String body = """
            {
                "email": "alice@example.com",
                "password": "password123"
            }
            """;
        
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.user.name").value("Alice"));
    }
}
