package com.buyone.orderservice.dto.request.order;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderSearchRequest {
    private String keyword;  // Product name/status search
    
    private String status;   // Optional filter: PENDING, etc.
    
    @Min(0)
    @Builder.Default
    private Integer page = 0;
    
    @Min(1)
    @Builder.Default
    private Integer size = 10;
}
