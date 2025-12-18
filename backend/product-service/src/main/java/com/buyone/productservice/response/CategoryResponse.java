package com.buyone.productservice.response;

public record CategoryResponse(
        String id,
        String slug,
        String name,
        String icon,
        String description
) {}
