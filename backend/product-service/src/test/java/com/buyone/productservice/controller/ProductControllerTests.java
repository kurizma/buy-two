package com.buyone.productservice.controller;

import com.buyone.productservice.config.SecurityConfig;
import com.buyone.productservice.exception.GlobalExceptionHandler;
import com.buyone.productservice.service.ProductService;
import com.buyone.productservice.response.ProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class ProductControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ProductService productService;
    
    // -------- POST /products - validation tests --------
    
    @Test
    void createProduct_returns400_whenNameIsBlank() throws Exception {
        String body = """
            {
                "name": "",
                "description": "desc",
                "price": 10.0,
                "quantity": 1,
                "categoryId": "cat1"
            }
            """;
        
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-USER-ID", "seller-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.name").exists());
    }
    
    @Test
    void createProduct_returns400_whenPriceBelowMinimum() throws Exception {
        String body = """
            {
                "name": "Valid Product",
                "description": "desc",
                "price": -1.0,
                "quantity": 1,
                "categoryId": "cat1"
            }
            """;
        
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-USER-ID", "seller-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.price").exists());
    }
    
    @Test
    void createProduct_returns400_whenQuantityBelowMinimum() throws Exception {
        String body = """
            {
                "name": "Valid Product",
                "description": "desc",
                "price": 10.0,
                "quantity": 0,
                "categoryId": "cat1"
            }
            """;
        
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-USER-ID", "seller-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.quantity").exists());
    }
    
    @Test
    void createProduct_returns403_whenNotSeller() throws Exception {
        String body = """
            {
                "name": "Valid Product",
                "description": "desc",
                "price": 10.0,
                "quantity": 5,
                "categoryId": "cat1"
            }
            """;
        
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void createProduct_returns201_whenValid() throws Exception {
        String body = """
            {
                "name": "Valid Product",
                "description": "desc",
                "price": 10.0,
                "quantity": 5,
                "categoryId": "cat1"
            }
            """;
        
        ProductResponse resp = ProductResponse.builder()
                .id("p1").name("Valid Product").price(BigDecimal.valueOf(10.0)).quantity(5)
                .build();
        when(productService.createProduct(any(), anyString())).thenReturn(resp);
        
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-USER-ID", "seller-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Valid Product"));
    }
    
    // -------- PUT /products/{id} - validation tests --------
    
    @Test
    void updateProduct_returns400_whenNameTooShort() throws Exception {
        String body = """
            {
                "name": "A"
            }
            """;
        
        mockMvc.perform(put("/products/p1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-USER-ID", "seller-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.name").exists());
    }
    
    // -------- GET /products/{id} --------
    
    @Test
    void getProductById_returns200_whenFound() throws Exception {
        ProductResponse resp = ProductResponse.builder()
                .id("p1").name("Laptop").price(BigDecimal.valueOf(999)).quantity(5)
                .build();
        when(productService.getProductById("p1")).thenReturn(resp);
        
        mockMvc.perform(get("/products/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("p1"))
                .andExpect(jsonPath("$.data.name").value("Laptop"));
    }
    
    // -------- GET /products --------
    
    @Test
    void getProducts_returns200() throws Exception {
        ProductResponse p1 = ProductResponse.builder().id("p1").name("A").build();
        when(productService.getAllProducts()).thenReturn(List.of(p1));
        
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("p1"));
    }
    
    // -------- POST /products/stock/reserve - validation --------
    
    @Test
    void reserveStock_returns400_whenProductIdBlank() throws Exception {
        String body = """
            {
                "productId": "",
                "quantity": 1,
                "orderNumber": "ORD-001"
            }
            """;
        
        mockMvc.perform(post("/products/stock/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void reserveStock_returns400_whenQuantityLessThan1() throws Exception {
        String body = """
            {
                "productId": "prod-1",
                "quantity": 0,
                "orderNumber": "ORD-001"
            }
            """;
        
        mockMvc.perform(post("/products/stock/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
