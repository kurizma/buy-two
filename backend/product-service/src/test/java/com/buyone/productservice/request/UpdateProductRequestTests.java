package com.buyone.productservice.request;

import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UpdateProductRequestTests {

    private final Validator validator;

    public UpdateProductRequestTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNoArgsConstructor() {
        UpdateProductRequest request = new UpdateProductRequest();
        assertNotNull(request);
        assertNull(request.getName());
        assertNull(request.getDescription());
        assertNull(request.getPrice());
        assertNull(request.getQuantity());
        assertNull(request.getCategoryId());
        assertNull(request.getImages());
    }

    @Test
    void testAllArgsConstructor() {
        List<String> images = List.of("img1.jpg");
        UpdateProductRequest request = new UpdateProductRequest("Updated", "Desc", 
                new BigDecimal("199.99"), 20, "cat1", images);
        
        assertEquals("Updated", request.getName());
        assertEquals("Desc", request.getDescription());
        assertEquals(new BigDecimal("199.99"), request.getPrice());
        assertEquals(20, request.getQuantity());
        assertEquals("cat1", request.getCategoryId());
        assertEquals(1, request.getImages().size());
    }

    @Test
    void testBuilder() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .name("Builder Update")
                .price(new BigDecimal("59.99"))
                .quantity(15)
                .build();
        
        assertEquals("Builder Update", request.getName());
        assertEquals(new BigDecimal("59.99"), request.getPrice());
        assertEquals(15, request.getQuantity());
    }

    @Test
    void testValidPartialUpdate() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .name("Just New Name")
                .build();
        Set<ConstraintViolation<UpdateProductRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEmptyRequest() {
        UpdateProductRequest request = new UpdateProductRequest();
        Set<ConstraintViolation<UpdateProductRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNameTooShort() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .name("A")
                .build();
        Set<ConstraintViolation<UpdateProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNameTooLong() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .name("A".repeat(101))
                .build();
        Set<ConstraintViolation<UpdateProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testDescriptionTooLong() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .description("A".repeat(501))
                .build();
        Set<ConstraintViolation<UpdateProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNegativePrice() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .price(new BigDecimal("-1.00"))
                .build();
        Set<ConstraintViolation<UpdateProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testZeroQuantity() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .quantity(0)
                .build();
        Set<ConstraintViolation<UpdateProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        UpdateProductRequest req1 = new UpdateProductRequest("N", "D", new BigDecimal("10"), 1, "c", List.of());
        UpdateProductRequest req2 = new UpdateProductRequest("N", "D", new BigDecimal("10"), 1, "c", List.of());
        UpdateProductRequest req3 = new UpdateProductRequest("X", "Y", new BigDecimal("20"), 2, "z", List.of());
        
        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
        assertNotEquals(req1, req3);
    }

    @Test
    void testToString() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .name("Test Update")
                .build();
        String str = request.toString();
        assertNotNull(str);
        assertTrue(str.contains("Test Update"));
    }
}
