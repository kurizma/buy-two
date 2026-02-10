package com.buyone.orderservice.model.order;

import com.buyone.orderservice.model.Address;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTests {

    @Test
    void testOrderBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Address address = Address.builder()
                .street("123 Main St")
                .city("New York")
                .state("NY")
                .zipCode("10001")
                .country("USA")
                .build();

        Order order = Order.builder()
                .id("order-1")
                .userId("user-1")
                .orderNumber("ORD-12345")
                .status(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.CARD)
                .shippingAddress(address)
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("10.00"))
                .total(new BigDecimal("110.00"))
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals("order-1", order.getId());
        assertEquals("user-1", order.getUserId());
        assertEquals("ORD-12345", order.getOrderNumber());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(PaymentMethod.CARD, order.getPaymentMethod());
        assertNotNull(order.getShippingAddress());
        assertEquals(new BigDecimal("100.00"), order.getSubtotal());
    }

    @Test
    void testOrderDefaultStatus() {
        Order order = Order.builder()
                .id("order-1")
                .userId("user-1")
                .build();

        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void testOrderDefaultPaymentMethod() {
        Order order = Order.builder()
                .id("order-1")
                .userId("user-1")
                .build();

        assertEquals(PaymentMethod.PAY_ON_DELIVERY, order.getPaymentMethod());
    }

    @Test
    void testOrderDefaultItems() {
        Order order = Order.builder()
                .id("order-1")
                .build();

        assertNotNull(order.getItems());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void testOrderNoArgsConstructor() {
        Order order = new Order();
        assertNull(order.getId());
        assertNull(order.getUserId());
    }

    @Test
    void testOrderAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Address address = Address.builder().street("123 Main St").city("NYC").build();
        List<OrderItem> items = new ArrayList<>();

        Order order = new Order("order-1", "user-1", "ORD-123", items,
                OrderStatus.CONFIRMED, PaymentMethod.PAYPAL, address,
                new BigDecimal("50.00"), new BigDecimal("5.00"),
                new BigDecimal("5.00"), new BigDecimal("60.00"), now, now);

        assertEquals("order-1", order.getId());
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void testOrderSetters() {
        Order order = new Order();
        order.setId("order-2");
        order.setUserId("user-2");
        order.setOrderNumber("ORD-456");
        order.setStatus(OrderStatus.SHIPPED);
        order.setPaymentMethod(PaymentMethod.CARD);
        order.setSubtotal(new BigDecimal("200.00"));
        order.setTax(new BigDecimal("20.00"));
        order.setTotal(new BigDecimal("220.00"));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setItems(new ArrayList<>());

        assertEquals("order-2", order.getId());
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    void testOrderWithItems() {
        OrderItem item = OrderItem.builder()
                .productId("prod-1")
                .productName("Test Product")
                .sellerId("seller-1")
                .price(new BigDecimal("50.00"))
                .quantity(2)
                .build();

        Order order = Order.builder()
                .id("order-1")
                .userId("user-1")
                .items(Arrays.asList(item))
                .build();

        assertEquals(1, order.getItems().size());
        assertEquals("prod-1", order.getItems().get(0).getProductId());
    }

    @Test
    void testOrderEqualsAndHashCode() {
        Order order1 = Order.builder().id("order-1").userId("user-1").build();
        Order order2 = Order.builder().id("order-1").userId("user-1").build();
        Order order3 = Order.builder().id("order-2").userId("user-2").build();

        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());
        assertNotEquals(order1, order3);
    }

    @Test
    void testOrderToString() {
        Order order = Order.builder()
                .id("order-1")
                .userId("user-1")
                .orderNumber("ORD-12345")
                .build();

        String toString = order.toString();
        assertTrue(toString.contains("order-1"));
        assertTrue(toString.contains("ORD-12345"));
    }

    @Test
    void testOrderStatusTransitions() {
        Order order = Order.builder()
                .id("order-1")
                .status(OrderStatus.PENDING)
                .build();

        assertEquals(OrderStatus.PENDING, order.getStatus());
        
        order.setStatus(OrderStatus.CONFIRMED);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        
        order.setStatus(OrderStatus.SHIPPED);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        
        order.setStatus(OrderStatus.DELIVERED);
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void testOrderCancellation() {
        Order order = Order.builder()
                .id("order-1")
                .status(OrderStatus.PENDING)
                .build();

        order.setStatus(OrderStatus.CANCELLED);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
}
