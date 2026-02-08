package com.buyone.orderservice.controller;

import com.buyone.orderservice.config.SecurityConfig;
import com.buyone.orderservice.exception.BadRequestException;
import com.buyone.orderservice.exception.GlobalExceptionHandler;
import com.buyone.orderservice.exception.ResourceNotFoundException;
import com.buyone.orderservice.model.Address;
import com.buyone.orderservice.model.order.Order;
import com.buyone.orderservice.model.order.OrderItem;
import com.buyone.orderservice.model.order.OrderStatus;
import com.buyone.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class OrderControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private OrderService orderService;
    
    private Order buildOrder(String orderNumber, OrderStatus status) {
        return Order.builder()
                .orderNumber(orderNumber)
                .userId("user-1")
                .status(status)
                .subtotal(BigDecimal.valueOf(100))
                .tax(BigDecimal.valueOf(10))
                .total(BigDecimal.valueOf(110))
                .createdAt(LocalDateTime.now())
                .shippingAddress(Address.builder()
                        .street("123 Main").city("NYC")
                        .zipCode("10001").country("US").build())
                .items(List.of(OrderItem.builder()
                        .productId("p1").productName("Widget")
                        .sellerId("seller-1").price(BigDecimal.TEN)
                        .quantity(10).build()))
                .build();
    }
    
    // ========== POST /api/orders/checkout ==========
    
    @Test
    void checkout_returns201_whenClientWithValidAddress() throws Exception {
        Order order = buildOrder("ORD-001", OrderStatus.PENDING);
        when(orderService.createOrderFromCart(eq("user-1"), any(Address.class))).thenReturn(order);
        
        String body = """
            {
                "shippingAddress": {
                    "street": "123 Main",
                    "city": "NYC",
                    "zipCode": "10001",
                    "country": "US"
                }
            }
            """;
        
        mockMvc.perform(post("/api/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD-001"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }
    
    @Test
    void checkout_returns400_whenRoleIsSeller() throws Exception {
        String body = """
            {
                "shippingAddress": {
                    "street": "123 Main",
                    "city": "NYC",
                    "zipCode": "10001",
                    "country": "US"
                }
            }
            """;
        
        mockMvc.perform(post("/api/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void checkout_returns400_whenAddressMissingStreet() throws Exception {
        String body = """
            {
                "shippingAddress": {
                    "city": "NYC",
                    "zipCode": "10001",
                    "country": "US"
                }
            }
            """;
        
        mockMvc.perform(post("/api/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void checkout_returns400_whenZipCodeInvalid() throws Exception {
        String body = """
            {
                "shippingAddress": {
                    "street": "123 Main",
                    "city": "NYC",
                    "zipCode": "ABCDE",
                    "country": "US"
                }
            }
            """;
        
        mockMvc.perform(post("/api/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isBadRequest());
    }
    
    // ========== GET /api/orders/buyer ==========
    
    @Test
    void getBuyerOrders_returns200_withOrderList() throws Exception {
        Order o = buildOrder("ORD-001", OrderStatus.PENDING);
        when(orderService.getBuyerOrders("user-1")).thenReturn(List.of(o));
        
        mockMvc.perform(get("/api/orders/buyer")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].orderNumber").value("ORD-001"));
    }
    
    @Test
    void getBuyerOrders_returns400_whenSellerRole() throws Exception {
        mockMvc.perform(get("/api/orders/buyer")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isBadRequest());
    }
    
    // ========== GET /api/orders/{orderNumber} ==========
    
    @Test
    void getOrder_returns200_whenFound() throws Exception {
        Order o = buildOrder("ORD-001", OrderStatus.CONFIRMED);
        when(orderService.getOrder("ORD-001")).thenReturn(Optional.of(o));
        
        mockMvc.perform(get("/api/orders/ORD-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderNumber").value("ORD-001"))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }
    
    @Test
    void getOrder_returns400_whenNotFound() throws Exception {
        when(orderService.getOrder("BAD")).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/orders/BAD"))
                .andExpect(status().isBadRequest());
    }
    
    // ========== PUT /api/orders/{orderNumber}/status ==========
    
    @Test
    void updateStatus_returns200_whenSellerUpdates() throws Exception {
        Order o = buildOrder("ORD-001", OrderStatus.CONFIRMED);
        when(orderService.updateStatus("ORD-001", "seller-1", OrderStatus.CONFIRMED))
                .thenReturn(Optional.of(o));
        
        mockMvc.perform(put("/api/orders/ORD-001/status")
                        .param("status", "CONFIRMED")
                        .header("X-USER-ID", "seller-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }
    
    @Test
    void updateStatus_returns400_whenClientRole() throws Exception {
        mockMvc.perform(put("/api/orders/ORD-001/status")
                        .param("status", "CONFIRMED")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isBadRequest());
    }
    
    // ========== POST /api/orders/{orderNumber}/confirm ==========
    
    @Test
    void confirmOrder_returns200_whenBuyerConfirms() throws Exception {
        Order o = buildOrder("ORD-001", OrderStatus.CONFIRMED);
        when(orderService.confirmOrder("ORD-001", "user-1")).thenReturn(Optional.of(o));
        
        mockMvc.perform(post("/api/orders/ORD-001/confirm")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order confirmed successfully!"));
    }
    
    @Test
    void confirmOrder_returns400_whenNotFound() throws Exception {
        when(orderService.confirmOrder("ORD-BAD", "user-1")).thenReturn(Optional.empty());
        
        mockMvc.perform(post("/api/orders/ORD-BAD/confirm")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isBadRequest());
    }
    
    // ========== POST /api/orders/{orderNumber}/cancel ==========
    
    @Test
    void cancelOrder_returns200_whenSuccess() throws Exception {
        doNothing().when(orderService).cancelOrder("ORD-001", "user-1");
        
        mockMvc.perform(post("/api/orders/ORD-001/cancel")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order cancelled successfully"));
    }
    
    @Test
    void cancelOrder_returns400_whenSellerRole() throws Exception {
        mockMvc.perform(post("/api/orders/ORD-001/cancel")
                        .header("X-USER-ID", "seller-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isBadRequest());
    }
    
    // ========== POST /api/orders/{orderNumber}/redo ==========
    
    @Test
    void redoOrder_returns201_whenSuccess() throws Exception {
        Order o = buildOrder("ORD-002", OrderStatus.PENDING);
        when(orderService.redoOrder("ORD-001", "user-1")).thenReturn(Optional.of(o));
        
        mockMvc.perform(post("/api/orders/ORD-001/redo")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.orderNumber").value("ORD-002"));
    }
    
    // ========== GET /api/orders/seller ==========
    
    @Test
    void getSellerOrders_returns200_whenSellerRole() throws Exception {
        Order o = buildOrder("ORD-001", OrderStatus.PENDING);
        Page<Order> page = new PageImpl<>(List.of(o), PageRequest.of(0, 10), 1);
        when(orderService.getSellerOrders(eq("seller-1"), any())).thenReturn(page);
        
        mockMvc.perform(get("/api/orders/seller")
                        .param("page", "0")
                        .param("size", "10")
                        .header("X-USER-ID", "seller-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    void getSellerOrders_returns400_whenClientRole() throws Exception {
        mockMvc.perform(get("/api/orders/seller")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isBadRequest());
    }
}
