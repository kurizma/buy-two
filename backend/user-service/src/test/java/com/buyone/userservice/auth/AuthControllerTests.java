package com.buyone.userservice.auth;

import com.buyone.userservice.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
}
