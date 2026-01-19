package com.buyone.orderservice.response;

public record CategoryResponse(
        String id,
        String slug,
        String name,
        String icon,
        String description
) {}
