package com.buyone.userservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    CLIENT,
    SELLER;
    
    @JsonCreator
    public static Role fromString(String value) {
        if (value == null) return null;
        return Role.valueOf(value.trim().toUpperCase());
    }
    
    @JsonValue
    public String toValue() {
        return name();
    }
}
