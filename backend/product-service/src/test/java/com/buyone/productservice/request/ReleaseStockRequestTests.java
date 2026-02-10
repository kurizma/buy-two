package com.buyone.productservice.request;

import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ReleaseStockRequestTests {

    private final Validator validator;

    public ReleaseStockRequestTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNoArgsConstructor() {
        ReleaseStockRequest request = new ReleaseStockRequest();
        assertNotNull(request);
        assertNull(request.getProductId());
        assertEquals(0, request.getQuantity());
    }

    @Test
    void testAllArgsConstructor() {
        ReleaseStockRequest request = new ReleaseStockRequest("prod1", 5);
        
        assertEquals("prod1", request.getProductId());
        assertEquals(5, request.getQuantity());
    }

    @Test
    void testSettersAndGetters() {
        ReleaseStockRequest request = new ReleaseStockRequest();
        
        request.setProductId("prod2");
        request.setQuantity(10);
        
        assertEquals("prod2", request.getProductId());
        assertEquals(10, request.getQuantity());
    }

    @Test
    void testValidRequest() {
        ReleaseStockRequest request = new ReleaseStockRequest("prod1", 1);
        Set<ConstraintViolation<ReleaseStockRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankProductId() {
        ReleaseStockRequest request = new ReleaseStockRequest("", 1);
        Set<ConstraintViolation<ReleaseStockRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testZeroQuantity() {
        ReleaseStockRequest request = new ReleaseStockRequest("prod1", 0);
        Set<ConstraintViolation<ReleaseStockRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        ReleaseStockRequest req1 = new ReleaseStockRequest("p1", 5);
        ReleaseStockRequest req2 = new ReleaseStockRequest("p1", 5);
        ReleaseStockRequest req3 = new ReleaseStockRequest("p2", 10);
        
        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
        assertNotEquals(req1, req3);
    }

    @Test
    void testToString() {
        ReleaseStockRequest request = new ReleaseStockRequest("prod", 5);
        String str = request.toString();
        assertNotNull(str);
        assertTrue(str.contains("prod"));
    }
}
