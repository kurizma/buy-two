package com.buyone.productservice.response;

public record ErrorResponse(String code, String message, Object details) {}
