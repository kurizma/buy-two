package com.buyone.orderservice.dto.request.order;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderSearchRequest {
    private String keyword;  // Product name/order number search

    private String status;   // Optional filter: PENDING, etc.

    private String startDate; // ISO-8601 string (e.g. "2025-01-01T00:00:00")
    private String endDate;   // ISO-8601 string (e.g. "2025-12-31T23:59:59")

    @Min(0)
    @Builder.Default
    private Integer page = 0;

    @Min(1)
    @Builder.Default
    private Integer size = 10;
}
