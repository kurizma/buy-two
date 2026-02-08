package com.buyone.orderservice.dto.response;

public record ErrorResponse(String code, String message, Object details) {}
