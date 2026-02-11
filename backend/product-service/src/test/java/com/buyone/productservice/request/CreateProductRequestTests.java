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

class CreateProductRequestTests {

    private final Validator validator;

    public CreateProductRequestTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNoArgsConstructor() {
        CreateProductRequest request = new CreateProductRequest();
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
        CreateProductRequest request = new CreateProductRequest("Product", "Desc", 
                new BigDecimal("99.99"), 10, "cat1", images);
        
        assertEquals("Product", request.getName());
        assertEquals("Desc", request.getDescription());
        assertEquals(new BigDecimal("99.99"), request.getPrice());
        assertEquals(10, request.getQuantity());
        assertEquals("cat1", request.getCategoryId());
        assertEquals(1, request.getImages().size());
    }

    @Test
    void testBuilder() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Builder Product")
                .description("Built via builder")
                .price(new BigDecimal("49.99"))
                .quantity(5)
                .categoryId("cat2")
                .images(List.of("image.png"))
                .build();
        
        assertEquals("Builder Product", request.getName());
        assertEquals("Built via builder", request.getDescription());
        assertEquals(new BigDecimal("49.99"), request.getPrice());
        assertEquals(5, request.getQuantity());
    }

    @Test
    void testValidRequest() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Valid Product")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .build();
        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankName() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .build();
        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNameTooShort() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("A")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .build();
        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNameTooLong() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("A".repeat(101))
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .build();
        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testDescriptionTooLong() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Valid Name")
                .description("A".repeat(501))
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .build();
        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullPrice() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Valid Name")
                .price(null)
                .quantity(1)
                .build();
        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNegativePrice() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Valid Name")
                .price(new BigDecimal("-1.00"))
                .quantity(1)
                .build();
        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testZeroPrice() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Valid Name")
                .price(BigDecimal.ZERO)
                .quantity(1)
                .build();
        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullQuantity() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Valid Name")
                .price(new BigDecimal("10.00"))
                .quantity(null)
                .build();
        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testZeroQuantity() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Valid Name")
                .price(new BigDecimal("10.00"))
                .quantity(0)
                .build();
        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        CreateProductRequest req1 = new CreateProductRequest("N", "D", new BigDecimal("10"), 1, "c", List.of());
        CreateProductRequest req2 = new CreateProductRequest("N", "D", new BigDecimal("10"), 1, "c", List.of());
        CreateProductRequest req3 = new CreateProductRequest("X", "Y", new BigDecimal("20"), 2, "z", List.of());
        
        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
        assertNotEquals(req1, req3);
    }

    @Test
    void testToString() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Test")
                .build();
        String str = request.toString();
        assertNotNull(str);
        assertTrue(str.contains("Test"));
    }
}
