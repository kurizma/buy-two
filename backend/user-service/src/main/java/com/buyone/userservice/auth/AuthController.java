package com.buyone.userservice.auth;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import com.buyone.userservice.request.RegisterUserRequest;
import com.buyone.userservice.response.UserResponse;
import com.buyone.userservice.request.LoginRequest;
import com.buyone.userservice.response.LoginResponse;
import com.buyone.userservice.auth.AuthService;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterUserRequest request) {
        UserResponse created = authService.register(request);
        return ResponseEntity.status(201).body(created); // "201 Created" on success
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request); // includes token, message, and UserResponse
        return ResponseEntity.ok(response); // "200 OK" with login payload
    }
}
