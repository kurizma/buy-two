package com.buyone.userservice.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.*;
import com.buyone.userservice.model.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {
    @Size(min = 2, max = 40, message = "Name must be between 2 and 40 characters")
    private String name;
    
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email; // Keep for admin update
    
    @Size(min = 8, max = 24, message = "Password must be between 8 and 24 characters")
    private String password;

    private String currentPassword;
    
    private Role role;
    private String avatar;
}
