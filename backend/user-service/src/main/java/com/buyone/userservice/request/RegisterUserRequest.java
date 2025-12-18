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
public class RegisterUserRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 40, message = "Name must be between 2 and 40 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 24, message = "Password must be between 8 and 24 characters")
    private String password;
    
    @NotNull(message = "Role is required")
    private Role role;

    private String avatar;
    // Optional: Add other fields if needed, with validations
}
