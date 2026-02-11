package com.buyone.discovery_service.response;

public record ErrorResponse(String code, String message, Object details) {}
