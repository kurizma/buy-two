package com.buyone.userservice.controller;

import com.buyone.userservice.exception.GlobalExceptionHandler;
import com.buyone.userservice.exception.ResourceNotFoundException;
import com.buyone.userservice.auth.JwtFilter;
import com.buyone.userservice.auth.JwtUtil;
import com.buyone.userservice.model.Role;
import com.buyone.userservice.request.UpdateUserRequest;
import com.buyone.userservice.response.UserResponse;
import com.buyone.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private JwtFilter jwtFilter;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    // ========== GET /api/users/{id} ==========
    
    @Test
    void getUserById_returns200_whenFound() throws Exception {
        UserResponse user = UserResponse.builder()
                .id("u1").name("Alice").email("alice@example.com").role(Role.CLIENT).build();
        when(userService.getUserById("u1")).thenReturn(user);
        
        mockMvc.perform(get("/api/users/u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("u1"))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }
    
    @Test
    void getUserById_returns404_whenNotFound() throws Exception {
        when(userService.getUserById("bad")).thenThrow(new ResourceNotFoundException("User not found"));
        
        mockMvc.perform(get("/api/users/bad"))
                .andExpect(status().isNotFound());
    }
    
    // ========== GET /api/users ==========
    
    @Test
    void getAllUsers_returns200_withList() throws Exception {
        UserResponse u1 = UserResponse.builder().id("u1").name("Alice").role(Role.CLIENT).build();
        UserResponse u2 = UserResponse.builder().id("u2").name("Bob").role(Role.SELLER).build();
        when(userService.getAllUsers()).thenReturn(List.of(u1, u2));
        
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
    
    // ========== GET /api/users/me ==========
    
    @Test
    void getCurrentUser_returns200_withPrincipal() throws Exception {
        UserResponse user = UserResponse.builder()
                .id("u1").name("Alice").email("alice@example.com").role(Role.CLIENT).build();
        when(userService.getUserByEmail("alice@example.com")).thenReturn(user);
        
        mockMvc.perform(get("/api/users/me")
                        .principal(() -> "alice@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }
    
    // ========== GET /api/users/sellers ==========
    
    @Test
    void getSellers_returns200_withSellerList() throws Exception {
        UserResponse seller = UserResponse.builder().id("s1").name("SellerA").role(Role.SELLER).build();
        when(userService.getUsersByRole(Role.SELLER)).thenReturn(List.of(seller));
        
        mockMvc.perform(get("/api/users/sellers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].role").value("SELLER"));
    }
    
    // ========== GET /api/users/clients ==========
    
    @Test
    void getClients_returns200_withClientList() throws Exception {
        UserResponse client = UserResponse.builder().id("c1").name("ClientA").role(Role.CLIENT).build();
        when(userService.getUsersByRole(Role.CLIENT)).thenReturn(List.of(client));
        
        mockMvc.perform(get("/api/users/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].role").value("CLIENT"));
    }
    
    // ========== PUT /api/users/me ==========
    
    @Test
    void updateCurrentUser_returns200_whenValid() throws Exception {
        UserResponse updated = UserResponse.builder()
                .id("u1").name("Alice Updated").email("alice@example.com").role(Role.CLIENT).build();
        when(userService.updateUserByEmail(eq("alice@example.com"), any(UpdateUserRequest.class)))
                .thenReturn(updated);
        
        String body = """
            {
                "name": "Alice Updated"
            }
            """;
        
        mockMvc.perform(put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .principal(() -> "alice@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Updated"));
    }
    
    // ========== PUT /api/users/{id} ==========
    
    @Test
    void updateUser_returns200_whenValid() throws Exception {
        UserResponse updated = UserResponse.builder()
                .id("u1").name("NewName").email("new@example.com").role(Role.CLIENT).build();
        when(userService.updateUser(eq("u1"), any(UpdateUserRequest.class))).thenReturn(updated);
        
        String body = """
            {
                "name": "NewName",
                "email": "new@example.com"
            }
            """;
        
        mockMvc.perform(put("/api/users/u1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"));
    }
    
    @Test
    void updateUser_returns404_whenUserNotFound() throws Exception {
        when(userService.updateUser(eq("bad"), any(UpdateUserRequest.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));
        
        String body = """
            {
                "name": "NewName"
            }
            """;
        
        mockMvc.perform(put("/api/users/bad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }
    
    // ========== DELETE /api/users/{id} ==========
    
    @Test
    void deleteUser_returns204_whenExists() throws Exception {
        doNothing().when(userService).deleteUser("u1");
        
        mockMvc.perform(delete("/api/users/u1"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void deleteUser_returns404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found")).when(userService).deleteUser("bad");
        
        mockMvc.perform(delete("/api/users/bad"))
                .andExpect(status().isNotFound());
    }
}
