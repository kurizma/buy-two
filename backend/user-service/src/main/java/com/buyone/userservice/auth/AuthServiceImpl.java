package com.buyone.userservice.auth;

import com.buyone.userservice.model.User;
import com.buyone.userservice.repository.UserRepository;
import com.buyone.userservice.request.RegisterUserRequest;
import com.buyone.userservice.request.LoginRequest;
import com.buyone.userservice.response.LoginResponse;
import com.buyone.userservice.response.UserResponse;
import com.buyone.userservice.exception.BadRequestException;
import com.buyone.userservice.exception.ConflictException;
import com.buyone.userservice.exception.ResourceNotFoundException;
import com.buyone.userservice.exception.AuthException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    public UserResponse register(RegisterUserRequest registerRequest) {
        // validate and check for duplicate email, etc.
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists");
        }
        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .avatar(registerRequest.getAvatar())
                .build();
        userRepository.save(user);
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getAvatar());
    }
    
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No user found with email: " + loginRequest.getEmail()));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid email or password");
        }
        String roleType = user.getRole().name(); // changing role (enum) to string
        String token = jwtUtil.generateToken(user.getId(),user.getEmail(), roleType);
        return new LoginResponse("Login successful", token,
                new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getAvatar()));
    }
}
