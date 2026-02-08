package com.buyone.orderservice.service;

import com.buyone.orderservice.client.ProductClient;
import com.buyone.orderservice.dto.response.ApiResponse;
import com.buyone.orderservice.dto.response.ProductResponse;
import com.buyone.orderservice.exception.BadRequestException;
import com.buyone.orderservice.model.cart.Cart;
import com.buyone.orderservice.model.cart.CartItem;
import com.buyone.orderservice.repository.CartRepository;
import com.buyone.orderservice.service.impl.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CartServiceImplTests {
    
    @Mock
    private CartRepository cartRepository;
    
    @Mock
    private ProductClient productClient;
    
    @InjectMocks
    private CartServiceImpl cartService;
    
    private void setTaxRate() {
        ReflectionTestUtils.setField(cartService, "taxRate", 0.1);
    }
    
    // -------- addItem --------
    
    @Test
    void addItem_addsNewItem_whenNotInCart() {
        setTaxRate();
        String userId = "user-1";
        CartItem item = CartItem.builder()
                .productId("prod-1").sellerId("seller-1")
                .quantity(2).build();
        
        when(cartRepository.findById(userId)).thenReturn(Optional.empty());
        
        ProductResponse product = new ProductResponse();
        product.setId("prod-1");
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(500));
        product.setQuantity(10);
        product.setImages(List.of("img.jpg"));
        
        when(productClient.getById("prod-1"))
                .thenReturn(ApiResponse.<ProductResponse>builder().success(true).data(product).build());
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));
        
        Cart result = cartService.addItem(userId, item);
        
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getProductName()).isEqualTo("Laptop");
        assertThat(result.getSubtotal()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }
    
    @Test
    void addItem_mergesQuantity_whenSameProductExists() {
        setTaxRate();
        String userId = "user-1";
        CartItem existingItem = CartItem.builder()
                .productId("prod-1").sellerId("seller-1")
                .productName("Laptop").price(BigDecimal.valueOf(500))
                .quantity(1).build();
        Cart cart = Cart.builder().id(userId).userId(userId)
                .items(new ArrayList<>(List.of(existingItem))).build();
        
        when(cartRepository.findById(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));
        
        CartItem newItem = CartItem.builder()
                .productId("prod-1").sellerId("seller-1")
                .quantity(2).build();
        
        Cart result = cartService.addItem(userId, newItem);
        
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(3);
    }
    
    @Test
    void addItem_throwsBadRequest_whenInsufficientStock() {
        setTaxRate();
        String userId = "user-1";
        CartItem item = CartItem.builder()
                .productId("prod-1").sellerId("seller-1")
                .quantity(100).build();
        
        when(cartRepository.findById(userId)).thenReturn(Optional.empty());
        
        ProductResponse product = new ProductResponse();
        product.setId("prod-1");
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(500));
        product.setQuantity(5);
        
        when(productClient.getById("prod-1"))
                .thenReturn(ApiResponse.<ProductResponse>builder().success(true).data(product).build());
        
        assertThatThrownBy(() -> cartService.addItem(userId, item))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Insufficient stock");
    }
    
    @Test
    void addItem_throwsBadRequest_whenProductNotFound() {
        setTaxRate();
        CartItem item = CartItem.builder()
                .productId("prod-x").sellerId("seller-1")
                .quantity(1).build();
        
        when(cartRepository.findById("user-1")).thenReturn(Optional.empty());
        when(productClient.getById("prod-x"))
                .thenReturn(ApiResponse.<ProductResponse>builder().success(false).data(null).build());
        
        assertThatThrownBy(() -> cartService.addItem("user-1", item))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Product not found");
    }
    
    @Test
    void addItem_throwsBadRequest_whenQuantityZero() {
        CartItem item = CartItem.builder()
                .productId("prod-1").sellerId("seller-1")
                .quantity(0).build();
        
        assertThatThrownBy(() -> cartService.addItem("user-1", item))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Quantity must be positive");
    }
    
    @Test
    void addItem_throwsBadRequest_whenProductIdEmpty() {
        CartItem item = CartItem.builder()
                .productId("").sellerId("seller-1")
                .quantity(1).build();
        
        assertThatThrownBy(() -> cartService.addItem("user-1", item))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Product ID is required");
    }
    
    @Test
    void addItem_throwsBadRequest_whenSellerIdNull() {
        CartItem item = CartItem.builder()
                .productId("prod-1").sellerId(null)
                .quantity(1).build();
        
        assertThatThrownBy(() -> cartService.addItem("user-1", item))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Seller ID is required");
    }
    
    // -------- getCart --------
    
    @Test
    void getCart_returnsOptional_whenExists() {
        Cart cart = Cart.builder().id("user-1").userId("user-1").items(new ArrayList<>()).build();
        when(cartRepository.findById("user-1")).thenReturn(Optional.of(cart));
        
        Optional<Cart> result = cartService.getCart("user-1");
        
        assertThat(result).isPresent();
    }
    
    @Test
    void getCart_returnsEmpty_whenNotExists() {
        when(cartRepository.findById("user-1")).thenReturn(Optional.empty());
        
        Optional<Cart> result = cartService.getCart("user-1");
        
        assertThat(result).isEmpty();
    }
    
    // -------- updateQuantity --------
    
    @Test
    void updateQuantity_updatesExistingItem() {
        setTaxRate();
        CartItem item = CartItem.builder()
                .productId("prod-1").sellerId("seller-1")
                .productName("Laptop").price(BigDecimal.valueOf(500))
                .quantity(1).build();
        Cart cart = Cart.builder().id("user-1").userId("user-1")
                .items(new ArrayList<>(List.of(item))).build();
        
        when(cartRepository.findById("user-1")).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));
        
        Cart result = cartService.updateQuantity("user-1", "prod-1", 5);
        
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(5);
    }
    
    @Test
    void updateQuantity_throwsBadRequest_whenProductNotInCart() {
        Cart cart = Cart.builder().id("user-1").userId("user-1")
                .items(new ArrayList<>()).build();
        
        when(cartRepository.findById("user-1")).thenReturn(Optional.of(cart));
        
        assertThatThrownBy(() -> cartService.updateQuantity("user-1", "prod-999", 5))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Product not found in cart");
    }
    
    @Test
    void updateQuantity_removesItem_whenQuantitySetToZero() {
        setTaxRate();
        CartItem item = CartItem.builder()
                .productId("prod-1").sellerId("seller-1")
                .productName("Laptop").price(BigDecimal.valueOf(500))
                .quantity(1).build();
        Cart cart = Cart.builder().id("user-1").userId("user-1")
                .items(new ArrayList<>(List.of(item))).build();
        
        when(cartRepository.findById("user-1")).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));
        
        Cart result = cartService.updateQuantity("user-1", "prod-1", 0);
        
        assertThat(result.getItems()).isEmpty();
    }
    
    // -------- removeItem --------
    
    @Test
    void removeItem_removesExistingItem() {
        setTaxRate();
        CartItem item = CartItem.builder()
                .productId("prod-1").sellerId("seller-1")
                .productName("Laptop").price(BigDecimal.valueOf(500))
                .quantity(1).build();
        Cart cart = Cart.builder().id("user-1").userId("user-1")
                .items(new ArrayList<>(List.of(item))).build();
        
        when(cartRepository.findById("user-1")).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));
        
        Cart result = cartService.removeItem("user-1", "prod-1");
        
        assertThat(result.getItems()).isEmpty();
    }
    
    @Test
    void removeItem_doesNotThrow_whenItemNotInCart() {
        setTaxRate();
        Cart cart = Cart.builder().id("user-1").userId("user-1")
                .items(new ArrayList<>()).build();
        
        when(cartRepository.findById("user-1")).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));
        
        Cart result = cartService.removeItem("user-1", "prod-999");
        
        assertThat(result.getItems()).isEmpty();
    }
    
    // -------- clearCart --------
    
    @Test
    void clearCart_savesEmptyCart() {
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));
        
        Cart result = cartService.clearCart("user-1");
        
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getSubtotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getTax()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(cartRepository).save(any(Cart.class));
    }
}
