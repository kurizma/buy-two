package com.buyone.orderservice.model;

public enum OrderStatus {
    PENDING,      // Just created
    CONFIRMED,    // Payment accepted
    SHIPPED,      // Seller shipped
    DELIVERED,    // Arrived
    CANCELLED     // Buyer/seller cancelled
}
