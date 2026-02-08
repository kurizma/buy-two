package com.buyone.productservice.controller;

import com.buyone.productservice.request.CreateProductRequest;
import com.buyone.productservice.request.UpdateProductRequest;
import com.buyone.productservice.request.ReserveStockRequest;
import com.buyone.productservice.request.ReleaseStockRequest;
import com.buyone.productservice.response.ProductResponse;
import com.buyone.productservice.response.ApiResponse;
import com.buyone.productservice.exception.ForbiddenException;
import com.buyone.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
@Validated
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    // GET /products (public) or GET /products?sellerId=... (public)
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts(
            @RequestParam(required = false) String sellerId) {

        List<ProductResponse> products;

        if (sellerId != null) {
            products = productService.getProductsBySeller(sellerId);
        } else {
            products = productService.getAllProducts();
        }

        return ResponseEntity.ok(okResponse("Products fetched successfully", products));
    }

    // GET /products/search (public - faceted search with pagination)
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> results = productService.searchProducts(
            keyword, minPrice, maxPrice, categoryId, pageable
        );
        
        return ResponseEntity.ok(
            ApiResponse.<Page<ProductResponse>>builder()
                .success(true)
                .message("Search results fetched successfully")
                .data(results)
                .build()
        );
    }
    
    
    // GET /products/{id} (public)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable String id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(okResponse("Product fetched successfully", product));
    }
    
    // POST /products (seller only)
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request,
            @RequestHeader("X-USER-ID") String sellerId,
            @RequestHeader("X-USER-ROLE") String role
    ) {
        if (!"SELLER".equals(role)) {
            throw new ForbiddenException("Only sellers can create products.");
        }
        
        ProductResponse product = productService.createProduct(request, sellerId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(okResponse("Product created successfully", product));
    }
    
    // PUT /products/{id} (seller only & must own)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request,
            @RequestHeader("X-USER-ID") String sellerId,
            @RequestHeader("X-USER-ROLE") String role
    ) {
        if (!"SELLER".equals(role)) {
            throw new ForbiddenException("Only sellers can update products.");
        }
        
        ProductResponse product = productService.updateProduct(id, request, sellerId);
        return ResponseEntity.ok(okResponse("Product updated successfully", product));
    }
    
    // DELETE /products/{id} (seller only & must own)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable String id,
            @RequestHeader("X-USER-ID") String sellerId,
            @RequestHeader("X-USER-ROLE") String role
    ) {
        if (!"SELLER".equals(role)) {
            throw new ForbiddenException("Only sellers can delete products.");
        }
        
        productService.deleteProduct(id, sellerId);
        return ResponseEntity.ok(okResponse("Product deleted successfully", null));
    }
    
    @PostMapping("/stock/reserve")
    public ResponseEntity<ApiResponse<Void>> reserveStock(
            @Valid @RequestBody ReserveStockRequest request) {
        
        productService.reserveStock(
                request.getProductId(),
                request.getQuantity(),
                request.getOrderNumber()
        );
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Stock reserved successfully")
                .build());
    }
    
    @PostMapping("/stock/release")
    public ResponseEntity<ApiResponse<Void>> releaseStock(
            @Valid @RequestBody ReleaseStockRequest request) {
        
        productService.releaseStock(
                request.getProductId(),
                request.getQuantity()
        );
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Stock released successfully")
                .build());
    }
    
    @PostMapping("/stock/commit/{orderNumber}")
    public ResponseEntity<Void> commitStock(@PathVariable String orderNumber) {
        productService.commitReservations(orderNumber);  // We'll add this method next
        return ResponseEntity.ok().build();
    }
    
    
    
    // Helper to build ApiResponse consistently
    private <T> ApiResponse<T> okResponse(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
}
