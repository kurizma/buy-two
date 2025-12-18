package com.buyone.userservice.service;

import com.buyone.userservice.request.RegisterUserRequest;
import com.buyone.userservice.request.UpdateUserRequest;
import com.buyone.userservice.response.UserResponse;
import com.buyone.userservice.model.User;
import com.buyone.userservice.model.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {
    // Creation
    UserResponse createUser(RegisterUserRequest request);
    
    // Queries for API (DTOs)
    UserResponse getUserById(String id);
    UserResponse getUserByEmail(String email);
    
    List<UserResponse> getAllUsers();
    List<UserResponse> getUsersByRole(Role role);
    
    // Updates
    UserResponse updateUser(String id, UpdateUserRequest request);              // admin/internal
    UserResponse updateUserByEmail(String email, UpdateUserRequest request);   // for /me
    
    // Internal lookups (entities, not exposed to controllers)
    Optional<User> getUserEntityByEmail(String email);
    User getUserEntityById(String id);
    
    // Deletion
    void deleteUser(String id);
}


// Methods should use DTOs for incoming and outgoing data (except for internal lookups).