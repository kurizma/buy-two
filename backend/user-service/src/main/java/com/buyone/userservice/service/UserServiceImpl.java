package com.buyone.userservice.service;

import com.buyone.userservice.model.User;
import com.buyone.userservice.model.Role;
import com.buyone.userservice.repository.UserRepository;
import com.buyone.userservice.request.RegisterUserRequest;
import com.buyone.userservice.request.UpdateUserRequest;
import com.buyone.userservice.response.UserResponse;
import com.buyone.userservice.exception.BadRequestException;
import com.buyone.userservice.exception.ConflictException;
import com.buyone.userservice.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // Creation //
    @Override
    public UserResponse createUser(RegisterUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email cannot be empty");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists: " + request.getEmail());
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Password cannot be empty");
        }
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .avatar(request.getAvatar())
                .build();
        
        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }

    
    // -------------------------- //
    
    // for DTOs
    
    @Override
    public UserResponse getUserById(String id) {
        return userRepository.findById(id)
                .map(this::toUserResponse)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found with ID " + id)
                );
    }
    
    @Override
    public UserResponse getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email must not be empty");
        }
        return userRepository.findByEmail(email)
                .map(this::toUserResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No user found with email: " + email)
                );
    }
    
    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        return users.stream()
                .filter(u -> u.getEmail() != null && u.getRole() != null)
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }
    
    
    // -------------------------- //
    
    // updates
    @Override
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        if (request.getEmail() != null && userRepository.findByEmail(request.getEmail()).isPresent()
                && !request.getEmail().equals(existingUser.getEmail())) {
            throw new ConflictException("Email already exists: " + request.getEmail());
        }
        
        if (request.getName() != null ) {
            existingUser.setName(request.getName());
        }
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            existingUser.setRole(request.getRole());
        }

        // Always set avatar from request (can be null to clear)
        existingUser.setAvatar(request.getAvatar()); // may be null -> clears avatar   
            
        User updatedUser = userRepository.save(existingUser);
        return toUserResponse(updatedUser);
    }
    
    @Override
    public UserResponse updateUserByEmail(String email, UpdateUserRequest request) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email must not be empty");
        }
        
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No user found with email: " + email)
                );
        
        // Decide allowed fields for self-update (no role change, maybe no email change)
        if (request.getName() != null) {
            existingUser.setName(request.getName());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        existingUser.setAvatar(request.getAvatar()); // may be null -> clears avatar
        // if you want to allow email change via /me, handle it carefully & check uniqueness
        
        User updatedUser = userRepository.save(existingUser);
        return toUserResponse(updatedUser);
    }
    
    // -------------------------- //
    
    // for internal lookup
    @Override
    public Optional<User> getUserEntityByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email must not be empty");
        }
        return userRepository.findByEmail(email);
    }
    
    @Override
    public User getUserEntityById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
    
    // -------------------------- //
    
    // Deletion
    
    @Override
    @Transactional
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete — user not found with ID: " + id);
        }
        // Cascade deletes/logic for related entities if needed
        userRepository.deleteById(id);
    }
    
    // -------------------------- //
    
    // Mapping helper
    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .avatar(user.getAvatar())
                .build();
    }
}
