package com.buyone.orderservice.service.impl;

import com.buyone.orderservice.client.ProductClient;
import com.buyone.orderservice.dto.request.order.OrderSearchRequest;
import com.buyone.orderservice.dto.request.ReserveStockRequest;
import com.buyone.orderservice.dto.request.ReleaseStockRequest;
import com.buyone.orderservice.exception.BadRequestException;
import com.buyone.orderservice.exception.ResourceNotFoundException;
import com.buyone.orderservice.model.*;
import com.buyone.orderservice.model.cart.Cart;
import com.buyone.orderservice.model.cart.CartItem;
import com.buyone.orderservice.model.order.Order;
import com.buyone.orderservice.model.order.OrderItem;
import com.buyone.orderservice.model.order.OrderStatus;
import com.buyone.orderservice.model.order.PaymentMethod;
import static com.buyone.orderservice.model.order.OrderStatus.*;
import static com.buyone.orderservice.model.order.PaymentMethod.PAY_ON_DELIVERY;
import com.buyone.orderservice.repository.OrderRepository;
import com.buyone.orderservice.service.CartService;
import com.buyone.orderservice.service.OrderService;
import com.buyone.orderservice.dto.response.ProductResponse;
import com.buyone.orderservice.dto.response.ApiResponse;
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
import java.util.Objects;
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
        BigDecimal totalInclVat = calculateSubtotal(orderItems); // €135.00 (incl VAT)
        BigDecimal tax = calculateTax(totalInclVat);             // €13.50
        BigDecimal subtotal = totalInclVat.subtract(tax);        // €121.50 (excl VAT)
        
         BigDecimal shippingCost = totalInclVat.compareTo(BigDecimal.valueOf(50)) >= 0 
            ? BigDecimal.ZERO 
            : BigDecimal.valueOf(4.9);
  
        BigDecimal grandTotal = totalInclVat.add(shippingCost);
        
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
                .shippingCost(shippingCost)
                .total(grandTotal)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Order saved = Objects.requireNonNull(orderRepository.save(order), "Failed to save order");
        
        // Post-checkout actions
        reserveInventory(saved.getItems(), saved.getOrderNumber());
        
//        // 2. NEW: auto-confirm Pay on Delivery
       if (saved.getPaymentMethod() == PaymentMethod.PAY_ON_DELIVERY) {
           saved.setStatus(OrderStatus.CONFIRMED);
           saved.setUpdatedAt(LocalDateTime.now());
           saved = orderRepository.save(saved);  // Save CONFIRMED
            
            // Commit: delete reservations, qty stays deducted ✅
           productClient.commitStock(saved.getOrderNumber());
           log.info("Auto-confirmed Pay on Delivery order {}", saved.getOrderNumber());
       }
        
        cartService.clearCart(userId);
        
        log.info("Order {} created for {} (subtotal: {})", orderNumber, userId, subtotal);
        return saved;
    }
    
    @Override
    public Optional<Order> confirmOrder(String orderNumber, String userId) {
        return getOrder(orderNumber)
                .filter(order -> userId.equals(order.getUserId()))      // Buyer owns order
                .filter(order -> order.getStatus() == OrderStatus.PENDING)  // Only PENDING
                .map(order -> {
                    OrderStatus oldStatus = order.getStatus();
                    order.setStatus(OrderStatus.CONFIRMED);
                    order.setUpdatedAt(LocalDateTime.now());
                    Order saved = orderRepository.save(order);
                    
                    // Commit stock (same as seller updateStatus)
                    productClient.commitStock(orderNumber);
                    log.info("Buyer {} confirmed order {}", userId, orderNumber);
                    
                    return saved;
                });
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
        ApiResponse<ProductResponse> response = productClient.getById(cartItem.getProductId());
        
        if (!response.isSuccess() || response.getData() == null) {
            log.warn("Product not found for order snapshot: {}", cartItem.getProductId());
            throw new ResourceNotFoundException("Product not found: " + cartItem.getProductId());
        }
        
        ProductResponse product = response.getData();
        
        return OrderItem.builder()
                .productId(product.getId())
                .productName(product.getName())
                .sellerId(product.getUserId())  // Maps userId → sellerId
                .price(product.getPrice())
                .quantity(cartItem.getQuantity())
                .imageUrl(safeFirstImage(product.getImages()))
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
    // Reverse VAT: totalInclVat = subtotal * 1.24
        BigDecimal subtotalExclVat = subtotal.divide(BigDecimal.valueOf(1.24), 2, RoundingMode.HALF_UP);
        return subtotal.subtract(subtotalExclVat);  // VAT amount
    }
    
    /**
     * Queues inventory reservation (TODO: @Async + RabbitMQ).
     */
    private void reserveInventory(List<OrderItem> items, String orderNumber) {
        for (OrderItem item : items) {
            ReserveStockRequest req = new ReserveStockRequest(
                    item.getProductId(),
                    item.getQuantity(),
                    orderNumber
            );
            
            ApiResponse<Void> response = productClient.reserveStock(req);
            if (!response.isSuccess()) {
                throw new BadRequestException(
                        "Failed to reserve stock for product: " + item.getProductId() +
                                ". Error: " + response.getMessage());
            }
            log.info("Reserved {} units of {} for order {}",
                    item.getQuantity(), item.getProductId(), orderNumber);
        }
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
        
        OrderStatus oldStatus = order.getStatus();
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        Order saved = orderRepository.save(order);
        
        // NEW: commit stock once when leaving PENDING
        if (oldStatus == OrderStatus.PENDING && status == OrderStatus.CONFIRMED) {
            // We only need orderNumber to commit all reservations
            productClient.commitStock(orderNumber);
            log.info("Committed stock reservations for order {}", orderNumber);
        }
        
        return Optional.of(saved);
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
        order.getItems().forEach(item -> {
            ReleaseStockRequest req = new ReleaseStockRequest(
                    item.getProductId(),
                    item.getQuantity()
            );
            productClient.releaseStock(req);
            log.info("Released {} units of {} for cancelled order {}",
                    item.getQuantity(), item.getProductId(), orderNumber);
        });
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
                    return createOrderFromCart(userId, oldOrder.getShippingAddress());  // Reuse original
                });
    }
    
    @Override
    public Page<Order> searchBuyerOrders(String userId, OrderSearchRequest req) {
        OrderStatus status = req.getStatus() != null
                ? OrderStatus.valueOf(req.getStatus().toUpperCase())
                : null;
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
