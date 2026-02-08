package com.buyone.userservice.response;

public record ErrorResponse(String code, String message, Object details) {}
