package com.buyone.orderservice.model.cart;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartTests {

    @Test
    void testCartBuilder() {
        LocalDateTime now = LocalDateTime.now();
        List<CartItem> items = new ArrayList<>();
        
        Cart cart = Cart.builder()
                .id("cart-1")
                .userId("user-1")
                .items(items)
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("10.00"))
                .total(new BigDecimal("110.00"))
                .updatedAt(now)
                .build();

        assertEquals("cart-1", cart.getId());
        assertEquals("user-1", cart.getUserId());
        assertNotNull(cart.getItems());
        assertEquals(new BigDecimal("100.00"), cart.getSubtotal());
        assertEquals(new BigDecimal("10.00"), cart.getTax());
        assertEquals(new BigDecimal("110.00"), cart.getTotal());
        assertEquals(now, cart.getUpdatedAt());
    }

    @Test
    void testCartNoArgsConstructor() {
        Cart cart = new Cart();
        assertNull(cart.getId());
        assertNull(cart.getUserId());
    }

    @Test
    void testCartAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        List<CartItem> items = new ArrayList<>();
        
        Cart cart = new Cart("cart-1", "user-1", items, 
                new BigDecimal("100.00"), new BigDecimal("10.00"), 
                new BigDecimal("110.00"), now);

        assertEquals("cart-1", cart.getId());
        assertEquals("user-1", cart.getUserId());
    }

    @Test
    void testCartSetters() {
        Cart cart = new Cart();
        cart.setId("cart-2");
        cart.setUserId("user-2");
        cart.setSubtotal(new BigDecimal("50.00"));
        cart.setTax(new BigDecimal("5.00"));
        cart.setTotal(new BigDecimal("55.00"));
        cart.setUpdatedAt(LocalDateTime.now());
        cart.setItems(new ArrayList<>());

        assertEquals("cart-2", cart.getId());
        assertEquals("user-2", cart.getUserId());
        assertEquals(new BigDecimal("50.00"), cart.getSubtotal());
    }

    @Test
    void testCartEqualsAndHashCode() {
        Cart cart1 = Cart.builder().id("cart-1").userId("user-1").build();
        Cart cart2 = Cart.builder().id("cart-1").userId("user-1").build();
        Cart cart3 = Cart.builder().id("cart-2").userId("user-2").build();

        assertEquals(cart1, cart2);
        assertEquals(cart1.hashCode(), cart2.hashCode());
        assertNotEquals(cart1, cart3);
    }

    @Test
    void testCartToString() {
        Cart cart = Cart.builder().id("cart-1").userId("user-1").build();
        String toString = cart.toString();
        
        assertTrue(toString.contains("cart-1"));
        assertTrue(toString.contains("user-1"));
    }

    @Test
    void testCartWithItems() {
        CartItem item = CartItem.builder()
                .productId("prod-1")
                .sellerId("seller-1")
                .productName("Test Product")
                .price(new BigDecimal("25.00"))
                .quantity(2)
                .build();

        Cart cart = Cart.builder()
                .id("cart-1")
                .userId("user-1")
                .items(Arrays.asList(item))
                .build();

        assertEquals(1, cart.getItems().size());
        assertEquals("prod-1", cart.getItems().get(0).getProductId());
    }

    @Test
    void testCartDefaultItems() {
        Cart cart = Cart.builder()
                .id("cart-1")
                .userId("user-1")
                .build();

        assertNotNull(cart.getItems());
        assertTrue(cart.getItems().isEmpty());
    }
}
