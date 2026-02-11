package com.buyone.gatewayservice.response;

public record ErrorResponse(String code, String message, Object details) {}
