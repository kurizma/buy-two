package com.buyone.userservice.auth;

import com.buyone.userservice.request.RegisterUserRequest;
import com.buyone.userservice.request.LoginRequest;
import com.buyone.userservice.response.LoginResponse;
import com.buyone.userservice.response.UserResponse;

public interface AuthService {
    UserResponse register(RegisterUserRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
}
