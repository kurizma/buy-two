package com.buyone.orderservice.model;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @NotBlank(message = "Street is required")
    private String street;
    
    @NotBlank(message = "City is required")
    private String city;
    private String state;
    
    @NotBlank(message = "ZIP/Postal code is required")
    @Pattern(regexp = "^[0-9]{5}$", message = "ZIP must be 5 digits")
    private String zipCode;
    
    @NotBlank(message = "Country is required")
    private String country;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone format")
    private String phone;
}
