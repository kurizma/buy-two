package com.buyone.orderservice.service;

import com.buyone.orderservice.client.ProductClient;
import com.buyone.orderservice.dto.request.ReleaseStockRequest;
import com.buyone.orderservice.dto.request.ReserveStockRequest;
import com.buyone.orderservice.dto.request.order.OrderSearchRequest;
import com.buyone.orderservice.dto.response.ApiResponse;
import com.buyone.orderservice.dto.response.ProductResponse;
import com.buyone.orderservice.exception.BadRequestException;
import com.buyone.orderservice.exception.ResourceNotFoundException;
import com.buyone.orderservice.model.Address;
import com.buyone.orderservice.model.cart.Cart;
import com.buyone.orderservice.model.cart.CartItem;
import com.buyone.orderservice.model.order.Order;
import com.buyone.orderservice.model.order.OrderItem;
import com.buyone.orderservice.model.order.OrderStatus;
import com.buyone.orderservice.repository.OrderRepository;
import com.buyone.orderservice.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceImplTests {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private CartService cartService;
    
    @Mock
    private ProductClient productClient;
    
    @InjectMocks
    private OrderServiceImpl orderService;
    
    // -------- createOrderFromCart --------
    
    @Test
    void createOrderFromCart_createsOrder_whenCartHasItems() {
        String userId = "user-1";
        Address address = Address.builder()
                .street("123 Main St").city("NYC").zipCode("10001").country("US")
                .build();
        
        CartItem cartItem = CartItem.builder()
                .productId("prod-1").sellerId("seller-1")
                .productName("Laptop").price(BigDecimal.valueOf(1000))
                .quantity(2).build();
        Cart cart = Cart.builder().userId(userId)
                .items(new ArrayList<>(List.of(cartItem))).build();
        
        when(cartService.getCart(userId)).thenReturn(Optional.of(cart));
        
        ProductResponse product = new ProductResponse();
        product.setId("prod-1");
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setUserId("seller-1");
        product.setQuantity(10);
        product.setImages(List.of("img1.jpg"));
        
        when(productClient.getById("prod-1"))
                .thenReturn(ApiResponse.<ProductResponse>builder().success(true).data(product).build());
        when(productClient.reserveStock(any(ReserveStockRequest.class)))
                .thenReturn(ApiResponse.<Void>builder().success(true).build());
        
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId("order-id-1");
            return o;
        });
        
        when(cartService.clearCart(userId)).thenReturn(Cart.builder().userId(userId).items(new ArrayList<>()).build());
        
        Order result = orderService.createOrderFromCart(userId, address);
        
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getProductName()).isEqualTo("Laptop");
        // Auto-confirmed for PAY_ON_DELIVERY
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        // Subtotal = 2000 / 1.24 = 1612.90 (reverse VAT calculation)
        assertThat(result.getSubtotal()).isEqualByComparingTo(BigDecimal.valueOf(1612.90));
        verify(cartService).clearCart(userId);
    }
    
    @Test
    void createOrderFromCart_throwsResourceNotFound_whenCartNotFound() {
        when(cartService.getCart("user-1")).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> orderService.createOrderFromCart("user-1",
                Address.builder().street("s").city("c").zipCode("12345").country("US").build()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cart not found");
    }
    
    @Test
    void createOrderFromCart_throwsIllegalState_whenCartEmpty() {
        Cart emptyCart = Cart.builder().userId("user-1").items(new ArrayList<>()).build();
        when(cartService.getCart("user-1")).thenReturn(Optional.of(emptyCart));
        
        assertThatThrownBy(() -> orderService.createOrderFromCart("user-1",
                Address.builder().street("s").city("c").zipCode("12345").country("US").build()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Empty cart");
    }
    
    // -------- getBuyerOrders --------
    
    @Test
    void getBuyerOrders_returnsList() {
        Order o1 = Order.builder().id("o1").userId("user-1").orderNumber("ORD-001").build();
        when(orderRepository.findByUserId("user-1")).thenReturn(List.of(o1));
        
        List<Order> result = orderService.getBuyerOrders("user-1");
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrderNumber()).isEqualTo("ORD-001");
    }
    
    // -------- getOrder --------
    
    @Test
    void getOrder_returnsOptional_whenFound() {
        Order order = Order.builder().orderNumber("ORD-001").userId("user-1").build();
        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(order));
        
        Optional<Order> result = orderService.getOrder("ORD-001");
        
        assertThat(result).isPresent();
        assertThat(result.get().getOrderNumber()).isEqualTo("ORD-001");
    }
    
    @Test
    void getOrder_returnsEmpty_whenNotFound() {
        when(orderRepository.findByOrderNumber("ORD-999")).thenReturn(Optional.empty());
        
        Optional<Order> result = orderService.getOrder("ORD-999");
        
        assertThat(result).isEmpty();
    }
    
    // -------- updateStatus --------
    
    @Test
    void updateStatus_updatesSuccessfully_whenSellerOwnsAllItems() {
        String sellerId = "seller-1";
        OrderItem item = OrderItem.builder().productId("p1").sellerId(sellerId).build();
        Order order = Order.builder()
                .orderNumber("ORD-001").userId("user-1")
                .status(OrderStatus.PENDING)
                .items(List.of(item))
                .build();
        
        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(productClient.commitStock("ORD-001"))
                .thenReturn(ApiResponse.<Void>builder().success(true).build());
        
        Optional<Order> result = orderService.updateStatus("ORD-001", sellerId, OrderStatus.CONFIRMED);
        
        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(productClient).commitStock("ORD-001");
    }
    
    @Test
    void updateStatus_throwsBadRequest_whenSellerDoesNotOwnItems() {
        OrderItem item = OrderItem.builder().productId("p1").sellerId("other-seller").build();
        Order order = Order.builder()
                .orderNumber("ORD-001").userId("user-1")
                .items(List.of(item))
                .build();
        
        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(order));
        
        assertThatThrownBy(() -> orderService.updateStatus("ORD-001", "seller-1", OrderStatus.SHIPPED))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Seller not authorized");
    }
    
    @Test
    void updateStatus_throwsResourceNotFound_whenOrderMissing() {
        when(orderRepository.findByOrderNumber("ORD-999")).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> orderService.updateStatus("ORD-999", "seller-1", OrderStatus.SHIPPED))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found");
    }
    
    // -------- cancelOrder --------
    
    @Test
    void cancelOrder_cancelsSuccessfully_whenPendingAndOwner() {
        OrderItem item = OrderItem.builder().productId("p1").quantity(2).build();
        Order order = Order.builder()
                .orderNumber("ORD-001").userId("user-1")
                .status(OrderStatus.PENDING)
                .items(List.of(item))
                .build();
        
        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(productClient.releaseStock(any(ReleaseStockRequest.class)))
                .thenReturn(ApiResponse.<Void>builder().success(true).build());
        
        orderService.cancelOrder("ORD-001", "user-1");
        
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(productClient).releaseStock(any(ReleaseStockRequest.class));
    }
    
    @Test
    void cancelOrder_throwsBadRequest_whenNotOwner() {
        Order order = Order.builder()
                .orderNumber("ORD-001").userId("user-1")
                .status(OrderStatus.PENDING)
                .build();
        
        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(order));
        
        assertThatThrownBy(() -> orderService.cancelOrder("ORD-001", "user-2"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Not your order");
    }
    
    @Test
    void cancelOrder_throwsIllegalState_whenNotPendingOrConfirmed() {
        Order order = Order.builder()
                .orderNumber("ORD-001").userId("user-1")
                .status(OrderStatus.SHIPPED)
                .build();
        
        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(order));
        
        assertThatThrownBy(() -> orderService.cancelOrder("ORD-001", "user-1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only PENDING or CONFIRMED orders can be cancelled");
    }
    
    @Test
    void cancelOrder_throwsResourceNotFound_whenOrderMissing() {
        when(orderRepository.findByOrderNumber("ORD-999")).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> orderService.cancelOrder("ORD-999", "user-1"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found");
    }
    
    // -------- confirmOrder --------
    
    @Test
    void confirmOrder_confirmsPendingOrder_whenBuyerOwns() {
        Order order = Order.builder()
                .orderNumber("ORD-001").userId("user-1")
                .status(OrderStatus.PENDING)
                .build();
        
        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(productClient.commitStock("ORD-001"))
                .thenReturn(ApiResponse.<Void>builder().success(true).build());
        
        Optional<Order> result = orderService.confirmOrder("ORD-001", "user-1");
        
        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(productClient).commitStock("ORD-001");
    }
    
    @Test
    void confirmOrder_returnsEmpty_whenNotOwner() {
        Order order = Order.builder()
                .orderNumber("ORD-001").userId("user-1")
                .status(OrderStatus.PENDING)
                .build();
        
        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(order));
        
        Optional<Order> result = orderService.confirmOrder("ORD-001", "user-2");
        
        assertThat(result).isEmpty();
    }
    
    @Test
    void confirmOrder_returnsEmpty_whenNotPending() {
        Order order = Order.builder()
                .orderNumber("ORD-001").userId("user-1")
                .status(OrderStatus.CONFIRMED)
                .build();
        
        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(order));
        
        Optional<Order> result = orderService.confirmOrder("ORD-001", "user-1");
        
        assertThat(result).isEmpty();
    }
    
    // -------- searchBuyerOrders --------
    
    @Test
    void searchBuyerOrders_withKeywordOnly_callsRepository() {
        Order order = Order.builder().orderNumber("ORD-001").userId("user-1").build();
        Page<Order> page = new PageImpl<>(List.of(order));
        
        when(orderRepository.findBuyerOrdersSearch(
                eq("user-1"), eq("laptop"),
                eq(null), any(Pageable.class)))
                .thenReturn(page);
        
        OrderSearchRequest req = OrderSearchRequest.builder()
                .keyword("laptop").page(0).size(10)
                .build();
        
        Page<Order> result = orderService.searchBuyerOrders("user-1", req);
        
        assertThat(result.getContent()).hasSize(1);
        verify(orderRepository).findBuyerOrdersSearch(
                eq("user-1"), eq("laptop"),
                eq(null), any(Pageable.class));
    }
    
    @Test
    void searchBuyerOrders_withStatus_passesEnumToRepository() {
        Page<Order> page = new PageImpl<>(List.of());
        
        when(orderRepository.findBuyerOrdersSearch(
                eq("user-1"), isNull(),
                eq(OrderStatus.PENDING), any(Pageable.class)))
                .thenReturn(page);
        
        OrderSearchRequest req = OrderSearchRequest.builder()
                .status("PENDING").page(0).size(10)
                .build();
        
        Page<Order> result = orderService.searchBuyerOrders("user-1", req);
        
        assertThat(result.getContent()).isEmpty();
        verify(orderRepository).findBuyerOrdersSearch(
                eq("user-1"), isNull(),
                eq(OrderStatus.PENDING), any(Pageable.class));
    }
    
    @Test
    void searchBuyerOrders_withKeywordAndStatus_callsRepositoryWithBoth() {
        Page<Order> page = new PageImpl<>(List.of());
        
        when(orderRepository.findBuyerOrdersSearch(
                eq("user-1"), eq("laptop"),
                eq(OrderStatus.DELIVERED), any(Pageable.class)))
                .thenReturn(page);
        
        OrderSearchRequest req = OrderSearchRequest.builder()
                .keyword("laptop")
                .status("DELIVERED")
                .page(0).size(10)
                .build();
        
        Page<Order> result = orderService.searchBuyerOrders("user-1", req);
        
        assertThat(result.getContent()).isEmpty();
        verify(orderRepository).findBuyerOrdersSearch(
                eq("user-1"), eq("laptop"),
                eq(OrderStatus.DELIVERED), any(Pageable.class));
    }
    
    // -------- getSellerOrders --------
    
    @Test
    void getSellerOrders_returnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Order order = Order.builder().orderNumber("ORD-001")
                .items(List.of(OrderItem.builder().sellerId("seller-1").build()))
                .build();
        
        when(orderRepository.findSellerOrders("seller-1", pageable))
                .thenReturn(new PageImpl<>(List.of(order)));
        
        Page<Order> result = orderService.getSellerOrders("seller-1", pageable);
        
        assertThat(result.getContent()).hasSize(1);
    }
    
    // -------- redoOrder --------
    
    @Test
    void redoOrder_returnsEmpty_whenNotCancelled() {
        Order order = Order.builder()
                .orderNumber("ORD-001").userId("user-1")
                .status(OrderStatus.PENDING)
                .build();
        
        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(order));
        
        Optional<Order> result = orderService.redoOrder("ORD-001", "user-1");
        
        assertThat(result).isEmpty();
    }
    
    @Test
    void redoOrder_returnsEmpty_whenNotOwner() {
        Order order = Order.builder()
                .orderNumber("ORD-001").userId("user-1")
                .status(OrderStatus.CANCELLED)
                .build();
        
        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(order));
        
        Optional<Order> result = orderService.redoOrder("ORD-001", "user-2");
        
        assertThat(result).isEmpty();
    }
    
    @Test
    void redoOrder_recreatesOrder_whenCancelledAndOwner() {
        // Old cancelled order
        OrderItem oldItem = OrderItem.builder()
                .productId("p1").productName("Widget").sellerId("seller-1")
                .price(BigDecimal.TEN).quantity(2).build();
        Order oldOrder = Order.builder()
                .orderNumber("ORD-001").userId("user-1")
                .status(OrderStatus.CANCELLED)
                .shippingAddress(Address.builder()
                        .street("123 Main").city("NYC")
                        .zipCode("10001").country("US").build())
                .items(List.of(oldItem))
                .build();
        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(oldOrder));
        
        // Cart will be fetched by createOrderFromCart
        CartItem cartItem = CartItem.builder()
                .productId("p1").productName("Widget").sellerId("seller-1")
                .price(BigDecimal.TEN).quantity(2).build();
        Cart savedCart = Cart.builder()
                .userId("user-1").items(new ArrayList<>(List.of(cartItem)))
                .subtotal(BigDecimal.valueOf(20)).build();
        when(cartService.getCart("user-1")).thenReturn(Optional.of(savedCart));
        
        // Product client for fresh snap
        ProductResponse pr = new ProductResponse();
        pr.setId("p1");
        pr.setName("Widget");
        pr.setPrice(BigDecimal.TEN);
        pr.setQuantity(100);
        pr.setUserId("seller-1");
        pr.setImages(List.of("img.jpg"));
        when(productClient.getById("p1")).thenReturn(
                ApiResponse.<ProductResponse>builder().success(true).data(pr).build());
        when(productClient.reserveStock(any(ReserveStockRequest.class))).thenReturn(
                ApiResponse.<Void>builder().success(true).build());
        
        Order newOrder = Order.builder()
                .orderNumber("ORD-NEW").userId("user-1")
                .status(OrderStatus.PENDING).build();
        when(orderRepository.save(any(Order.class))).thenReturn(newOrder);
        
        Optional<Order> result = orderService.redoOrder("ORD-001", "user-1");
        
        assertThat(result).isPresent();
        verify(cartService).saveCart(any(Cart.class));
    }
    
    @Test
    void redoOrder_returnsEmpty_whenOrderNotFound() {
        when(orderRepository.findByOrderNumber("MISSING")).thenReturn(Optional.empty());
        
        Optional<Order> result = orderService.redoOrder("MISSING", "user-1");
        
        assertThat(result).isEmpty();
    }
}
