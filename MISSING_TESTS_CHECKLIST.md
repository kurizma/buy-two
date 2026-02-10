# Missing Tests Checklist - Quick Reference

This file provides a quick checklist of all missing test files organized by service and category.

---

## DISCOVERY-SERVICE (2 missing)

### Exceptions
- [ ] `src/test/java/com/buyone/discovery_service/exception/GlobalExceptionHandlerTests.java`

### DTOs/Models/Entities
- [ ] `src/test/java/com/buyone/discovery_service/response/ErrorResponseTests.java`

---

## GATEWAY-SERVICE (5 missing)

### Config Classes
- [ ] `src/test/java/com/buyone/gatewayservice/config/GatewayDebugConfigTests.java`
- [ ] `src/test/java/com/buyone/gatewayservice/config/JwtDecoderConfigTests.java`
- [ ] `src/test/java/com/buyone/gatewayservice/config/SecurityConfigTests.java`

### Exceptions
- [ ] `src/test/java/com/buyone/gatewayservice/exception/GlobalExceptionHandlerTests.java`

### Filters/Interceptors
- [ ] `src/test/java/com/buyone/gatewayservice/filter/JwtHeaderGatewayFilterFactoryTests.java`

### DTOs/Models/Entities
- [ ] `src/test/java/com/buyone/gatewayservice/response/ErrorResponseTests.java`

---

## MEDIA-SERVICE (23 missing)

### Config Classes
- [ ] `src/test/java/com/buyone/mediaservice/config/CloudflareR2ConfigTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/config/CloudflareR2PropertiesTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/config/JwtConfigTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/config/S3ConfigTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/config/SecurityConfigTests.java`

### Exceptions
- [ ] `src/test/java/com/buyone/mediaservice/exception/BadRequestExceptionTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/exception/ConflictExceptionTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/exception/ForbiddenExceptionTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/exception/GlobalExceptionHandlerTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/exception/InvalidFileExceptionTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/exception/MediaNotFoundExceptionTests.java`

### DTOs/Models/Entities
- [ ] `src/test/java/com/buyone/mediaservice/model/MediaTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/model/MediaOwnerTypeTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/request/MediaUploadRequestTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/response/ApiResponseTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/response/DeleteMediaResponseTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/response/ErrorResponseTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/response/MediaListResponseTests.java`
- [ ] `src/test/java/com/buyone/mediaservice/response/MediaResponseTests.java`
- [ ] `src/test/java/com/buyone/productservice/event/ProductDeletedEventTests.java`

### Repositories
- [ ] `src/test/java/com/buyone/mediaservice/repository/MediaRepositoryTests.java`

---

## ORDER-SERVICE (42 missing)

### Config Classes
- [ ] `src/test/java/com/buyone/orderservice/config/KafkaConfigTests.java`
- [ ] `src/test/java/com/buyone/orderservice/config/SecurityConfigTests.java`

### Exceptions
- [ ] `src/test/java/com/buyone/orderservice/exception/BadRequestExceptionTests.java`
- [ ] `src/test/java/com/buyone/orderservice/exception/ConflictExceptionTests.java`
- [ ] `src/test/java/com/buyone/orderservice/exception/ForbiddenExceptionTests.java`
- [ ] `src/test/java/com/buyone/orderservice/exception/GlobalExceptionHandlerTests.java`
- [ ] `src/test/java/com/buyone/orderservice/exception/ResourceNotFoundExceptionTests.java`

### DTOs/Models/Entities - Requests
- [ ] `src/test/java/com/buyone/orderservice/dto/request/ReleaseStockRequestTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/request/ReserveStockRequestTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/request/cart/AddCartItemRequestTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/request/cart/UpdateCartQuantityRequestTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/request/order/CreateOrderRequestTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/request/order/OrderSearchRequestTests.java`

### DTOs/Models/Entities - Responses
- [ ] `src/test/java/com/buyone/orderservice/dto/response/ApiResponseTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/response/ErrorResponseTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/response/ProductResponseTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/response/analytics/ClientAnalyticsResponseTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/response/analytics/ClientMostBoughtTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/response/analytics/ClientTopCategoryTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/response/analytics/ClientTotalSpentTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/response/analytics/SellerAnalyticsResponseTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/response/analytics/SellerBestProductTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/response/analytics/SellerTotalRevenueTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/response/analytics/SellerTotalUnitsTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/response/cart/CartItemResponseTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/response/cart/CartResponseTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/response/order/OrderItemResponseTests.java`
- [ ] `src/test/java/com/buyone/orderservice/dto/response/order/OrderResponseTests.java`

### DTOs/Models/Entities - Models
- [ ] `src/test/java/com/buyone/orderservice/model/AddressTests.java`
- [ ] `src/test/java/com/buyone/orderservice/model/ProductTests.java`
- [ ] `src/test/java/com/buyone/orderservice/model/cart/CartTests.java`
- [ ] `src/test/java/com/buyone/orderservice/model/cart/CartItemTests.java`
- [ ] `src/test/java/com/buyone/orderservice/model/order/OrderTests.java`
- [ ] `src/test/java/com/buyone/orderservice/model/order/OrderItemTests.java`
- [ ] `src/test/java/com/buyone/orderservice/model/order/OrderStatusTests.java`
- [ ] `src/test/java/com/buyone/orderservice/model/order/PaymentMethodTests.java`

### Repositories
- [ ] `src/test/java/com/buyone/orderservice/repository/CartRepositoryTests.java`
- [ ] `src/test/java/com/buyone/orderservice/repository/OrderRepositoryTests.java`

### Clients
- [ ] `src/test/java/com/buyone/orderservice/client/ProductClientTests.java`

---

## PRODUCT-SERVICE (30 missing)

### Controllers
- [ ] `src/test/java/com/buyone/productservice/controller/CategoryControllerTests.java`

### Config Classes
- [ ] `src/test/java/com/buyone/productservice/config/KafkaConfigTests.java`
- [ ] `src/test/java/com/buyone/productservice/config/SecurityConfigTests.java`

### Exceptions
- [ ] `src/test/java/com/buyone/productservice/exception/BadRequestExceptionTests.java`
- [ ] `src/test/java/com/buyone/productservice/exception/ConflictExceptionTests.java`
- [ ] `src/test/java/com/buyone/productservice/exception/ForbiddenExceptionTests.java`
- [ ] `src/test/java/com/buyone/productservice/exception/GlobalExceptionHandlerTests.java`
- [ ] `src/test/java/com/buyone/productservice/exception/ProductNotFoundExceptionTests.java`
- [ ] `src/test/java/com/buyone/productservice/exception/ResourceNotFoundExceptionTests.java`

### DTOs/Models/Entities - Events
- [ ] `src/test/java/com/buyone/productservice/event/ProductCreatedEventTests.java`
- [ ] `src/test/java/com/buyone/productservice/event/ProductDeletedEventTests.java`
- [ ] `src/test/java/com/buyone/productservice/event/ProductUpdatedEventTests.java`

### DTOs/Models/Entities - Models
- [ ] `src/test/java/com/buyone/productservice/model/CategoryTests.java`
- [ ] `src/test/java/com/buyone/productservice/model/ProductTests.java`
- [ ] `src/test/java/com/buyone/productservice/model/ReservationTests.java`

### DTOs/Models/Entities - Requests
- [ ] `src/test/java/com/buyone/productservice/request/CreateProductRequestTests.java`
- [ ] `src/test/java/com/buyone/productservice/request/ReleaseStockRequestTests.java`
- [ ] `src/test/java/com/buyone/productservice/request/ReserveStockRequestTests.java`
- [ ] `src/test/java/com/buyone/productservice/request/UpdateCategoryRequestTests.java`
- [ ] `src/test/java/com/buyone/productservice/request/UpdateProductRequestTests.java`

### DTOs/Models/Entities - Responses
- [ ] `src/test/java/com/buyone/productservice/response/ApiResponseTests.java`
- [ ] `src/test/java/com/buyone/productservice/response/CategoryResponseTests.java`
- [ ] `src/test/java/com/buyone/productservice/response/ErrorResponseTests.java`
- [ ] `src/test/java/com/buyone/productservice/response/ProductResponseTests.java`

### Repositories
- [ ] `src/test/java/com/buyone/productservice/repository/CategoryRepositoryTests.java`
- [ ] `src/test/java/com/buyone/productservice/repository/ProductRepositoryTests.java`
- [ ] `src/test/java/com/buyone/productservice/repository/ReservationRepositoryTests.java`

### Listeners
- [ ] `src/test/java/com/buyone/productservice/listener/ReservationCleanupListenerTests.java`

---

## USER-SERVICE (18 missing)

### Config Classes
- [ ] `src/test/java/com/buyone/userservice/config/SecurityConfigTests.java`

### Exceptions
- [ ] `src/test/java/com/buyone/userservice/exception/AuthExceptionTests.java`
- [ ] `src/test/java/com/buyone/userservice/exception/BadRequestExceptionTests.java`
- [ ] `src/test/java/com/buyone/userservice/exception/ConflictExceptionTests.java`
- [ ] `src/test/java/com/buyone/userservice/exception/ForbiddenExceptionTests.java`
- [ ] `src/test/java/com/buyone/userservice/exception/GlobalExceptionHandlerTests.java`
- [ ] `src/test/java/com/buyone/userservice/exception/ResourceNotFoundExceptionTests.java`

### DTOs/Models/Entities - Models
- [ ] `src/test/java/com/buyone/userservice/model/RoleTests.java`
- [ ] `src/test/java/com/buyone/userservice/model/UserTests.java`

### DTOs/Models/Entities - Requests
- [ ] `src/test/java/com/buyone/userservice/request/LoginRequestTests.java`
- [ ] `src/test/java/com/buyone/userservice/request/RegisterUserRequestTests.java`
- [ ] `src/test/java/com/buyone/userservice/request/UpdateUserRequestTests.java`

### DTOs/Models/Entities - Responses
- [ ] `src/test/java/com/buyone/userservice/response/ErrorResponseTests.java`
- [ ] `src/test/java/com/buyone/userservice/response/LoginResponseTests.java`
- [ ] `src/test/java/com/buyone/userservice/response/UserResponseTests.java`

### Repositories
- [ ] `src/test/java/com/buyone/userservice/repository/UserRepositoryTests.java`

---

## SUMMARY

**Total Missing Tests: 120**

### By Category:
- Controllers: 1
- Config Classes: 18
- Exceptions: 26
- DTOs/Models/Entities: 68
- Repositories: 7
- Filters/Interceptors: 1
- Listeners: 1
- Clients: 1

### By Service:
- discovery-service: 2
- gateway-service: 5
- media-service: 23
- order-service: 42
- product-service: 30
- user-service: 18

---

## IMPLEMENTATION TIPS

1. **Batch Creation**: Group similar test types (e.g., all exception tests, all DTO tests) for efficient creation
2. **Template Reuse**: Create test templates for common patterns (DTOs, Entities, Exceptions)
3. **Priority Order**: Follow the Priority recommendations in TEST_COVERAGE_ANALYSIS.md
4. **Test Naming**: Use consistent naming pattern: `{ClassName}Tests.java`
5. **Package Structure**: Mirror the main/java package structure in test/java

