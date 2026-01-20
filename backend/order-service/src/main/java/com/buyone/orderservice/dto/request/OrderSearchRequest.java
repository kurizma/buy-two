package com.buyone.orderservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderSearchRequest {
    @NotBlank(message = "Keyword cannot be blank for search")
    private String keyword;  // Product name/status search
    
    private String status;   // Optional filter: PENDING, etc.
    
    @Builder.Default
    private Integer page = 0;
    
    @Builder.Default
    private Integer size = 10;
}
