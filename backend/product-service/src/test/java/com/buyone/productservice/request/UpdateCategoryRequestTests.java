package com.buyone.productservice.request;

import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UpdateCategoryRequestTests {

    private final Validator validator;

    public UpdateCategoryRequestTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNoArgsConstructor() {
        UpdateCategoryRequest request = new UpdateCategoryRequest();
        assertNotNull(request);
        assertNull(request.getName());
        assertNull(request.getIcon());
        assertNull(request.getDescription());
    }

    @Test
    void testAllArgsConstructor() {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Electronics", "🔌", "Electronic items");
        
        assertEquals("Electronics", request.getName());
        assertEquals("🔌", request.getIcon());
        assertEquals("Electronic items", request.getDescription());
    }

    @Test
    void testSettersAndGetters() {
        UpdateCategoryRequest request = new UpdateCategoryRequest();
        
        request.setName("Books");
        request.setIcon("📚");
        request.setDescription("Books and magazines");
        
        assertEquals("Books", request.getName());
        assertEquals("📚", request.getIcon());
        assertEquals("Books and magazines", request.getDescription());
    }

    @Test
    void testValidRequest() {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Valid Category", null, null);
        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankName() {
        UpdateCategoryRequest request = new UpdateCategoryRequest("", "📚", "Desc");
        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullName() {
        UpdateCategoryRequest request = new UpdateCategoryRequest(null, "📚", "Desc");
        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testOptionalIcon() {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Category", null, "Desc");
        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testOptionalDescription() {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Category", "📚", null);
        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        UpdateCategoryRequest req1 = new UpdateCategoryRequest("N", "I", "D");
        UpdateCategoryRequest req2 = new UpdateCategoryRequest("N", "I", "D");
        UpdateCategoryRequest req3 = new UpdateCategoryRequest("X", "Y", "Z");
        
        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
        assertNotEquals(req1, req3);
    }

    @Test
    void testToString() {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Test", "📚", "Desc");
        String str = request.toString();
        assertNotNull(str);
        assertTrue(str.contains("Test"));
    }
}
