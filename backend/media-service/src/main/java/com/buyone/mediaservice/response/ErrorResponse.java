package com.buyone.mediaservice.response;

public record ErrorResponse(String code, String message, Object details) {}
