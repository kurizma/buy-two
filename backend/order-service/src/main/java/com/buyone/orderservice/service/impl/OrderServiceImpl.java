package com.buyone.orderservice.service.impl;

import com.buyone.orderservice.client.ProductClient;
import com.buyone.orderservice.dto.request.order.OrderSearchRequest;
import com.buyone.orderservice.exception.BadRequestException;
import com.buyone.orderservice.exception.ResourceNotFoundException;
import com.buyone.orderservice.model.*;
import com.buyone.orderservice.model.cart.Cart;
import com.buyone.orderservice.model.cart.CartItem;
import com.buyone.orderservice.model.order.Order;
import com.buyone.orderservice.model.order.OrderItem;
import com.buyone.orderservice.model.order.OrderStatus;
import com.buyone.orderservice.model.order.PaymentMethod;
import com.buyone.orderservice.repository.OrderRepository;
import com.buyone.orderservice.service.CartService;
import com.buyone.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * OrderService implementation with re-fetch snapshot strategy for production accuracy.
 * Creates immutable order records with fresh product data at checkout time.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductClient productClient;  // Re-fetches live product data
    
    /**
     * Creates order from cart with FRESH product snapshots (price/name/seller).
     * Validates cart → re-fetches products → calculates totals → saves → clears cart.
     */
    @Override
    public Order createOrderFromCart(String userId, Address shippingAddress) {
        var cartItems = getValidatedCartItems(userId);
        
        // Re-fetch LIVE products → Fresh, legally-accurate snapshots
        List<OrderItem> orderItems = cartItems.stream()
                .map(this::fetchFreshProductSnapshot)
                .toList();
        
        // Precise money calculations
        BigDecimal subtotal = calculateSubtotal(orderItems);
        BigDecimal tax = calculateTax(subtotal);
        
        // Build order
        String orderNumber = generateOrderNumber();
        Order order = Order.builder()
                .userId(userId)
                .orderNumber(orderNumber)
                .items(orderItems)
                .status(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.PAY_ON_DELIVERY)
                .shippingAddress(shippingAddress)
                .subtotal(subtotal)
                .tax(tax)
                .total(subtotal.add(tax))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Order saved = orderRepository.save(order);
        
        // Post-checkout actions
        cartService.clearCart(userId);
        reserveInventoryAsync(orderItems, orderNumber);
        
        log.info("Order {} created for {} (subtotal: {})", orderNumber, userId, subtotal);
        return saved;
    }
    
    /**
     * Gets cart items with validation - quantities only (don't trust stale cart data).
     */
    private List<CartItem> getValidatedCartItems(String userId) {
        var cartOpt = cartService.getCart(userId);
        var cartItems = cartOpt.map(Cart::getItems)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found: " + userId));
        if (cartItems.isEmpty()) throw new IllegalStateException("Empty cart");
        return cartItems;
    }
    
    /**
     * Fetches LIVE product data → Creates immutable snapshot for order.
     * Guarantees accuracy even if seller changed price/name since cart add.
     */
    private OrderItem fetchFreshProductSnapshot(CartItem cartItem) {
        Product product = productClient.getById(cartItem.getProductId());
        
        return OrderItem.builder()
                .productId(product.getId())
                .productName(product.getName())     // Fresh for receipts
                .sellerId(product.getUserId())      // Fresh for seller analytics
                .price((product.getPrice())) // Fresh for billing
                .quantity(cartItem.getQuantity())   // User choice from cart
                .imageUrl(safeFirstImage(product.getImages()))  // Fresh for UI
                .build();
    }
    
    /**
     * Safely extracts first image URL - null-safe.
     */
    private String safeFirstImage(List<String> images) {
        return images != null && !images.isEmpty() ? images.get(0) : null;
    }
    
    /**
     * Generates unique order number: ORD-ABC12345
     */
    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Calculates subtotal: Σ(price × quantity) exactly.
     */
    private BigDecimal calculateSubtotal(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 10% tax, rounded to 2 decimals (business rule).
     */
    private BigDecimal calculateTax(BigDecimal subtotal) {
        return subtotal.multiply(BigDecimal.valueOf(0.1)).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Queues inventory reservation (TODO: @Async + RabbitMQ).
     */
    private void reserveInventoryAsync(List<OrderItem> items, String orderNumber) {
        log.debug("Inventory reservation queued for order: {}", orderNumber);
        // TODO: productClient.reserveStock(items, orderNumber);
    }
    
    // ========== EXISTING METHODS (PERFECT - MINOR ENUM FIXES) ==========
    
    @Override
    public List<Order> getBuyerOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }
    
    @Override
    public Optional<Order> getOrder(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }
    
    /**
     * Seller/admin updates status (CONFIRMED→SHIPPED→DELIVERED).
     */
    @Override
    public Optional<Order> updateStatus(String orderNumber, String sellerId, OrderStatus status) {
        Order order = getOrder(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));
        
        // CRITICAL CHECK
        // Seller owns all items
        boolean ownsAllItems = order.getItems().stream()
                .allMatch(item -> sellerId.equals(item.getSellerId()));
        if (!ownsAllItems) {
            throw new BadRequestException("Seller not authorized for this order");
        }
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        return Optional.of(orderRepository.save(order));
    }
    
    /**
     * Buyer cancels PENDING order → CANCELLED.
     */
    @Override
    public void cancelOrder(String orderNumber, String userId) {
        Order order = getOrder(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));
        // ownership check
        if (!userId.equals(order.getUserId())) {
            throw new BadRequestException("Not your order");
        }
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
    
    /**
     * Buyer redoes CANCELLED order → clones to new cart → new order.
     */
    @Override
    public Optional<Order> redoOrder(String orderNumber, String userId) {
        return getOrder(orderNumber)  // Optional chain
                .filter(order -> userId.equals(order.getUserId()))  // Ownership
                .filter(order -> order.getStatus() == OrderStatus.CANCELLED)
                .map(oldOrder -> {
                    List<CartItem> newItems = oldOrder.getItems().stream()
                            .map(this::orderItemToCartItem)
                            .collect(Collectors.toList());
                    Cart newCart = Cart.builder()
                            .userId(userId)
                            .items(newItems)
                            .build();
                    cartService.saveCart(newCart);
                    return createOrderFromCart(userId, null);  // refresh prices if changed
                });
    }
    
    @Override
    public Page<Order> searchBuyerOrders(String userId, OrderSearchRequest req) {
        OrderStatus status = req.getStatus() != null ? OrderStatus.valueOf(req.getStatus()) : null;
        Pageable pageable = PageRequest.of(req.getPage(), req.getSize());
        return orderRepository.findBuyerOrdersSearch(userId, req.getKeyword(), status, pageable);
    }
    
    @Override
    public Page<Order> getSellerOrders(String sellerId, Pageable pageable) {
        return orderRepository.findSellerOrders(sellerId, pageable);
    }
    
    /**
     * Converts OrderItem → CartItem for redo (double back-convert).
     */
    private CartItem orderItemToCartItem(OrderItem item) {
        return CartItem.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .sellerId(item.getSellerId())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .imageUrl(item.getImageUrl())
                .build();
    }
}
