package com.buyone.productservice.request;

import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ReserveStockRequestTests {

    private final Validator validator;

    public ReserveStockRequestTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNoArgsConstructor() {
        ReserveStockRequest request = new ReserveStockRequest();
        assertNotNull(request);
        assertNull(request.getProductId());
        assertEquals(0, request.getQuantity());
        assertNull(request.getOrderNumber());
    }

    @Test
    void testAllArgsConstructor() {
        ReserveStockRequest request = new ReserveStockRequest("prod1", 5, "ORD-001");
        
        assertEquals("prod1", request.getProductId());
        assertEquals(5, request.getQuantity());
        assertEquals("ORD-001", request.getOrderNumber());
    }

    @Test
    void testSettersAndGetters() {
        ReserveStockRequest request = new ReserveStockRequest();
        
        request.setProductId("prod2");
        request.setQuantity(10);
        request.setOrderNumber("ORD-002");
        
        assertEquals("prod2", request.getProductId());
        assertEquals(10, request.getQuantity());
        assertEquals("ORD-002", request.getOrderNumber());
    }

    @Test
    void testValidRequest() {
        ReserveStockRequest request = new ReserveStockRequest("prod1", 1, "ORD-001");
        Set<ConstraintViolation<ReserveStockRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankProductId() {
        ReserveStockRequest request = new ReserveStockRequest("", 1, "ORD-001");
        Set<ConstraintViolation<ReserveStockRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testZeroQuantity() {
        ReserveStockRequest request = new ReserveStockRequest("prod1", 0, "ORD-001");
        Set<ConstraintViolation<ReserveStockRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testBlankOrderNumber() {
        ReserveStockRequest request = new ReserveStockRequest("prod1", 1, "");
        Set<ConstraintViolation<ReserveStockRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        ReserveStockRequest req1 = new ReserveStockRequest("p1", 5, "o1");
        ReserveStockRequest req2 = new ReserveStockRequest("p1", 5, "o1");
        ReserveStockRequest req3 = new ReserveStockRequest("p2", 10, "o2");
        
        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
        assertNotEquals(req1, req3);
    }

    @Test
    void testToString() {
        ReserveStockRequest request = new ReserveStockRequest("prod", 5, "order");
        String str = request.toString();
        assertNotNull(str);
        assertTrue(str.contains("prod"));
    }
}
