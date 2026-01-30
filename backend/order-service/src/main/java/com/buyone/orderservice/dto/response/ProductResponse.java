package com.buyone.orderservice.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String userId;      // Maps to sellerId
    private String categoryId;
    private List<String> images;
}
