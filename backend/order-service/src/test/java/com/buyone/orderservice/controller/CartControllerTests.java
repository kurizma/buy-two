package com.buyone.orderservice.controller;

import com.buyone.orderservice.config.SecurityConfig;
import com.buyone.orderservice.exception.BadRequestException;
import com.buyone.orderservice.exception.GlobalExceptionHandler;
import com.buyone.orderservice.model.cart.Cart;
import com.buyone.orderservice.model.cart.CartItem;
import com.buyone.orderservice.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class CartControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private CartService cartService;
    
    private Cart buildCart() {
        CartItem item = CartItem.builder()
                .productId("p1").sellerId("seller-1")
                .productName("Widget").price(BigDecimal.TEN)
                .quantity(2).build();
        return Cart.builder()
                .id("cart-1").userId("user-1")
                .items(new ArrayList<>(List.of(item)))
                .subtotal(BigDecimal.valueOf(20))
                .tax(BigDecimal.valueOf(2))
                .total(BigDecimal.valueOf(22))
                .build();
    }
    
    // ========== POST /api/cart/items ==========
    
    @Test
    void addItem_returns200_whenClientRole() throws Exception {
        Cart cart = buildCart();
        when(cartService.addItem(eq("user-1"), any(CartItem.class))).thenReturn(cart);
        
        String body = """
            {
                "productId": "p1",
                "sellerId": "seller-1",
                "productName": "Widget",
                "price": 10.0,
                "quantity": 2
            }
            """;
        
        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Item added to cart successfully"));
    }
    
    @Test
    void addItem_returns400_whenSellerRole() throws Exception {
        String body = """
            {
                "productId": "p1",
                "sellerId": "seller-1",
                "productName": "Widget",
                "price": 10.0,
                "quantity": 2
            }
            """;
        
        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void addItem_returns400_whenQuantityZero() throws Exception {
        String body = """
            {
                "productId": "p1",
                "sellerId": "seller-1",
                "productName": "Widget",
                "price": 10.0,
                "quantity": 0
            }
            """;
        
        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isBadRequest());
    }
    
    // ========== GET /api/cart ==========
    
    @Test
    void getCart_returns200_withCart() throws Exception {
        Cart cart = buildCart();
        when(cartService.getCart("user-1")).thenReturn(Optional.of(cart));
        
        mockMvc.perform(get("/api/cart")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cart fetched successfully"));
    }
    
    @Test
    void getCart_returns200_withEmptyMessage() throws Exception {
        when(cartService.getCart("user-1")).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/cart")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cart is empty"));
    }
    
    // ========== PUT /api/cart/items/{productId}/quantity/{quantity} ==========
    
    @Test
    void updateQuantity_returns200_whenValid() throws Exception {
        Cart cart = buildCart();
        when(cartService.updateQuantity("user-1", "p1", 5)).thenReturn(cart);
        
        mockMvc.perform(put("/api/cart/items/p1/quantity/5")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item quantity updated successfully"));
    }
    
    @Test
    void updateQuantity_returns400_whenSellerRole() throws Exception {
        mockMvc.perform(put("/api/cart/items/p1/quantity/5")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isBadRequest());
    }
    
    // ========== DELETE /api/cart/items/{productId} ==========
    
    @Test
    void removeItem_returns200() throws Exception {
        Cart cart = buildCart();
        when(cartService.removeItem("user-1", "p1")).thenReturn(cart);
        
        mockMvc.perform(delete("/api/cart/items/p1")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item removed from cart successfully"));
    }
    
    // ========== DELETE /api/cart ==========
    
    @Test
    void clearCart_returns200() throws Exception {
        Cart cart = Cart.builder().userId("user-1").items(List.of()).build();
        when(cartService.clearCart("user-1")).thenReturn(cart);
        
        mockMvc.perform(delete("/api/cart")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cart cleared successfully"));
    }
    
    @Test
    void clearCart_returns400_whenSellerRole() throws Exception {
        mockMvc.perform(delete("/api/cart")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isBadRequest());
    }
}
