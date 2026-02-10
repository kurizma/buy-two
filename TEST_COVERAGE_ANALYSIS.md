# Backend Services Test Coverage Analysis

## Executive Summary

This report provides a comprehensive mapping of existing and missing test files across all backend services in the buy-two project. The analysis excludes Application.java files from source counts.

---

## 1. DISCOVERY-SERVICE

### Statistics
- **Total Source Files**: 2 (excluding Application.java)
- **Existing Test Files**: 0 (excluding ApplicationTests.java)  
- **Missing Test Files**: 2
- **Coverage**: 0% (0/2)

### All Source Files (src/main/java)
1. `exception/GlobalExceptionHandler.java`
2. `response/ErrorResponse.java`

### Existing Test Files (src/test/java)
- None (only DiscoveryServiceApplicationTests.java exists)

### Missing Tests by Category

#### Exceptions (2 missing)
- `exception/GlobalExceptionHandler.java` → Missing: `GlobalExceptionHandlerTests.java`

#### DTOs/Models/Entities (1 missing)
- `response/ErrorResponse.java` → Missing: `ErrorResponseTests.java`

---

## 2. GATEWAY-SERVICE

### Statistics
- **Total Source Files**: 6 (excluding Application.java)
- **Existing Test Files**: 1 (excluding ApplicationTests.java)
- **Missing Test Files**: 5
- **Coverage**: 16.7% (1/6)

### All Source Files (src/main/java)
1. `config/GatewayDebugConfig.java`
2. `config/JwtDecoderConfig.java`
3. `config/SecurityConfig.java`
4. `exception/GlobalExceptionHandler.java`
5. `filter/JwtHeaderGatewayFilterFactory.java`
6. `response/ErrorResponse.java`

### Existing Test Files (src/test/java)
1. `TestJwtConfig.java` (test helper)

### Missing Tests by Category

#### Config Classes (3 missing)
- `config/GatewayDebugConfig.java` → Missing: `GatewayDebugConfigTests.java`
- `config/JwtDecoderConfig.java` → Missing: `JwtDecoderConfigTests.java`
- `config/SecurityConfig.java` → Missing: `SecurityConfigTests.java`

#### Exceptions (1 missing)
- `exception/GlobalExceptionHandler.java` → Missing: `GlobalExceptionHandlerTests.java`

#### Filters/Interceptors (1 missing)
- `filter/JwtHeaderGatewayFilterFactory.java` → Missing: `JwtHeaderGatewayFilterFactoryTests.java`

#### DTOs/Models/Entities (1 missing)
- `response/ErrorResponse.java` → Missing: `ErrorResponseTests.java`

---

## 3. MEDIA-SERVICE

### Statistics
- **Total Source Files**: 28 (excluding Application.java)
- **Existing Test Files**: 5 (excluding ApplicationTests.java)
- **Missing Test Files**: 23
- **Coverage**: 17.9% (5/28)

### All Source Files (src/main/java)
1. `config/CloudflareR2Config.java`
2. `config/CloudflareR2Properties.java`
3. `config/JwtConfig.java`
4. `config/S3Config.java`
5. `config/SecurityConfig.java`
6. `controller/MediaController.java`
7. `exception/BadRequestException.java`
8. `exception/ConflictException.java`
9. `exception/ForbiddenException.java`
10. `exception/GlobalExceptionHandler.java`
11. `exception/InvalidFileException.java`
12. `exception/MediaNotFoundException.java`
13. `listener/ProductEventListener.java`
14. `model/Media.java`
15. `model/MediaOwnerType.java`
16. `repository/MediaRepository.java`
17. `request/MediaUploadRequest.java`
18. `response/ApiResponse.java`
19. `response/DeleteMediaResponse.java`
20. `response/ErrorResponse.java`
21. `response/MediaListResponse.java`
22. `response/MediaResponse.java`
23. `security/SecurityUtils.java`
24. `service.impl/MediaServiceImpl.java`
25. `service.impl/com/buyone/mediaservice/service/impl/StorageServiceImpl.java`
26. `service/MediaService.java`
27. `service/StorageService.java`
28. `productservice/event/ProductDeletedEvent.java`

### Existing Test Files (src/test/java)
1. `controller/MediaControllerTests.java`
2. `listener/ProductEventListenerTests.java`
3. `security/SecurityUtilsTests.java`
4. `service/MediaServiceImplTests.java`
5. `service/StorageServiceImplTests.java`

### Missing Tests by Category

#### Config Classes (5 missing)
- `config/CloudflareR2Config.java` → Missing: `CloudflareR2ConfigTests.java`
- `config/CloudflareR2Properties.java` → Missing: `CloudflareR2PropertiesTests.java`
- `config/JwtConfig.java` → Missing: `JwtConfigTests.java`
- `config/S3Config.java` → Missing: `S3ConfigTests.java`
- `config/SecurityConfig.java` → Missing: `SecurityConfigTests.java`

#### Exceptions (6 missing)
- `exception/BadRequestException.java` → Missing: `BadRequestExceptionTests.java`
- `exception/ConflictException.java` → Missing: `ConflictExceptionTests.java`
- `exception/ForbiddenException.java` → Missing: `ForbiddenExceptionTests.java`
- `exception/GlobalExceptionHandler.java` → Missing: `GlobalExceptionHandlerTests.java`
- `exception/InvalidFileException.java` → Missing: `InvalidFileExceptionTests.java`
- `exception/MediaNotFoundException.java` → Missing: `MediaNotFoundExceptionTests.java`

#### DTOs/Models/Entities (9 missing)
- `model/Media.java` → Missing: `MediaTests.java`
- `model/MediaOwnerType.java` → Missing: `MediaOwnerTypeTests.java`
- `request/MediaUploadRequest.java` → Missing: `MediaUploadRequestTests.java`
- `response/ApiResponse.java` → Missing: `ApiResponseTests.java`
- `response/DeleteMediaResponse.java` → Missing: `DeleteMediaResponseTests.java`
- `response/ErrorResponse.java` → Missing: `ErrorResponseTests.java`
- `response/MediaListResponse.java` → Missing: `MediaListResponseTests.java`
- `response/MediaResponse.java` → Missing: `MediaResponseTests.java`
- `productservice/event/ProductDeletedEvent.java` → Missing: `ProductDeletedEventTests.java`

#### Repositories (1 missing)
- `repository/MediaRepository.java` → Missing: `MediaRepositoryTests.java`

#### Services/Service implementations (2 missing - interfaces only)
- `service/MediaService.java` → Already tested via: `MediaServiceImplTests.java`
- `service/StorageService.java` → Already tested via: `StorageServiceImplTests.java`

---

## 4. ORDER-SERVICE

### Statistics
- **Total Source Files**: 48 (excluding Application.java)
- **Existing Test Files**: 6 (excluding ApplicationTests.java)
- **Missing Test Files**: 42
- **Coverage**: 12.5% (6/48)

### All Source Files (src/main/java)
1. `client/ProductClient.java`
2. `config/KafkaConfig.java`
3. `config/SecurityConfig.java`
4. `controller/AnalyticsController.java`
5. `controller/CartController.java`
6. `controller/OrderController.java`
7. `dto/request/ReleaseStockRequest.java`
8. `dto/request/ReserveStockRequest.java`
9. `dto/request/cart/AddCartItemRequest.java`
10. `dto/request/cart/UpdateCartQuantityRequest.java`
11. `dto/request/order/CreateOrderRequest.java`
12. `dto/request/order/OrderSearchRequest.java`
13. `dto/response/ApiResponse.java`
14. `dto/response/ErrorResponse.java`
15. `dto/response/ProductResponse.java`
16. `dto/response/analytics/ClientAnalyticsResponse.java`
17. `dto/response/analytics/ClientMostBought.java`
18. `dto/response/analytics/ClientTopCategory.java`
19. `dto/response/analytics/ClientTotalSpent.java`
20. `dto/response/analytics/SellerAnalyticsResponse.java`
21. `dto/response/analytics/SellerBestProduct.java`
22. `dto/response/analytics/SellerTotalRevenue.java`
23. `dto/response/analytics/SellerTotalUnits.java`
24. `dto/response/cart/CartItemResponse.java`
25. `dto/response/cart/CartResponse.java`
26. `dto/response/order/OrderItemResponse.java`
27. `dto/response/order/OrderResponse.java`
28. `exception/BadRequestException.java`
29. `exception/ConflictException.java`
30. `exception/ForbiddenException.java`
31. `exception/GlobalExceptionHandler.java`
32. `exception/ResourceNotFoundException.java`
33. `model/Address.java`
34. `model/Product.java`
35. `model/cart/Cart.java`
36. `model/cart/CartItem.java`
37. `model/order/Order.java`
38. `model/order/OrderItem.java`
39. `model/order/OrderStatus.java`
40. `model/order/PaymentMethod.java`
41. `repository/CartRepository.java`
42. `repository/OrderRepository.java`
43. `service/CartService.java`
44. `service/OrderService.java`
45. `service/ProfileAnalyticsService.java`
46. `service/impl/CartServiceImpl.java`
47. `service/impl/OrderServiceImpl.java`
48. `service/impl/ProfileAnalyticsServiceImpl.java`

### Existing Test Files (src/test/java)
1. `controller/AnalyticsControllerTests.java`
2. `controller/CartControllerTests.java`
3. `controller/OrderControllerTests.java`
4. `service/CartServiceImplTests.java`
5. `service/OrderServiceImplTests.java`
6. `service/ProfileAnalyticsServiceImplTests.java`

### Missing Tests by Category

#### Controllers (0 missing)
- ✅ All controllers have tests

#### Services/Service implementations (3 missing - interfaces only)
- `service/CartService.java` → Already tested via: `CartServiceImplTests.java`
- `service/OrderService.java` → Already tested via: `OrderServiceImplTests.java`
- `service/ProfileAnalyticsService.java` → Already tested via: `ProfileAnalyticsServiceImplTests.java`

#### Config Classes (2 missing)
- `config/KafkaConfig.java` → Missing: `KafkaConfigTests.java`
- `config/SecurityConfig.java` → Missing: `SecurityConfigTests.java`

#### DTOs/Models/Entities (27 missing)
- `dto/request/ReleaseStockRequest.java` → Missing: `ReleaseStockRequestTests.java`
- `dto/request/ReserveStockRequest.java` → Missing: `ReserveStockRequestTests.java`
- `dto/request/cart/AddCartItemRequest.java` → Missing: `AddCartItemRequestTests.java`
- `dto/request/cart/UpdateCartQuantityRequest.java` → Missing: `UpdateCartQuantityRequestTests.java`
- `dto/request/order/CreateOrderRequest.java` → Missing: `CreateOrderRequestTests.java`
- `dto/request/order/OrderSearchRequest.java` → Missing: `OrderSearchRequestTests.java`
- `dto/response/ApiResponse.java` → Missing: `ApiResponseTests.java`
- `dto/response/ErrorResponse.java` → Missing: `ErrorResponseTests.java`
- `dto/response/ProductResponse.java` → Missing: `ProductResponseTests.java`
- `dto/response/analytics/ClientAnalyticsResponse.java` → Missing: `ClientAnalyticsResponseTests.java`
- `dto/response/analytics/ClientMostBought.java` → Missing: `ClientMostBoughtTests.java`
- `dto/response/analytics/ClientTopCategory.java` → Missing: `ClientTopCategoryTests.java`
- `dto/response/analytics/ClientTotalSpent.java` → Missing: `ClientTotalSpentTests.java`
- `dto/response/analytics/SellerAnalyticsResponse.java` → Missing: `SellerAnalyticsResponseTests.java`
- `dto/response/analytics/SellerBestProduct.java` → Missing: `SellerBestProductTests.java`
- `dto/response/analytics/SellerTotalRevenue.java` → Missing: `SellerTotalRevenueTests.java`
- `dto/response/analytics/SellerTotalUnits.java` → Missing: `SellerTotalUnitsTests.java`
- `dto/response/cart/CartItemResponse.java` → Missing: `CartItemResponseTests.java`
- `dto/response/cart/CartResponse.java` → Missing: `CartResponseTests.java`
- `dto/response/order/OrderItemResponse.java` → Missing: `OrderItemResponseTests.java`
- `dto/response/order/OrderResponse.java` → Missing: `OrderResponseTests.java`
- `model/Address.java` → Missing: `AddressTests.java`
- `model/Product.java` → Missing: `ProductTests.java`
- `model/cart/Cart.java` → Missing: `CartTests.java`
- `model/cart/CartItem.java` → Missing: `CartItemTests.java`
- `model/order/Order.java` → Missing: `OrderTests.java`
- `model/order/OrderItem.java` → Missing: `OrderItemTests.java`
- `model/order/OrderStatus.java` → Missing: `OrderStatusTests.java`
- `model/order/PaymentMethod.java` → Missing: `PaymentMethodTests.java`

#### Repositories (2 missing)
- `repository/CartRepository.java` → Missing: `CartRepositoryTests.java`
- `repository/OrderRepository.java` → Missing: `OrderRepositoryTests.java`

#### Exceptions (5 missing)
- `exception/BadRequestException.java` → Missing: `BadRequestExceptionTests.java`
- `exception/ConflictException.java` → Missing: `ConflictExceptionTests.java`
- `exception/ForbiddenException.java` → Missing: `ForbiddenExceptionTests.java`
- `exception/GlobalExceptionHandler.java` → Missing: `GlobalExceptionHandlerTests.java`
- `exception/ResourceNotFoundException.java` → Missing: `ResourceNotFoundExceptionTests.java`

#### Clients (1 missing)
- `client/ProductClient.java` → Missing: `ProductClientTests.java`

---

## 5. PRODUCT-SERVICE

### Statistics
- **Total Source Files**: 33 (excluding Application.java)
- **Existing Test Files**: 3 (excluding ApplicationTests.java)
- **Missing Test Files**: 30
- **Coverage**: 9.1% (3/33)

### All Source Files (src/main/java)
1. `config/KafkaConfig.java`
2. `config/SecurityConfig.java`
3. `controller/CategoryController.java`
4. `controller/ProductController.java`
5. `event/ProductCreatedEvent.java`
6. `event/ProductDeletedEvent.java`
7. `event/ProductUpdatedEvent.java`
8. `exception/BadRequestException.java`
9. `exception/ConflictException.java`
10. `exception/ForbiddenException.java`
11. `exception/GlobalExceptionHandler.java`
12. `exception/ProductNotFoundException.java`
13. `exception/ResourceNotFoundException.java`
14. `listener/ReservationCleanupListener.java`
15. `model/Category.java`
16. `model/Product.java`
17. `model/Reservation.java`
18. `repository/CategoryRepository.java`
19. `repository/ProductRepository.java`
20. `repository/ReservationRepository.java`
21. `request/CreateProductRequest.java`
22. `request/ReleaseStockRequest.java`
23. `request/ReserveStockRequest.java`
24. `request/UpdateCategoryRequest.java`
25. `request/UpdateProductRequest.java`
26. `response/ApiResponse.java`
27. `response/CategoryResponse.java`
28. `response/ErrorResponse.java`
29. `response/ProductResponse.java`
30. `service/CategoryService.java`
31. `service/CategoryServiceImpl.java`
32. `service/ProductService.java`
33. `service/ProductServiceImpl.java`

### Existing Test Files (src/test/java)
1. `controller/ProductControllerTests.java`
2. `service/CategoryServiceImplTests.java`
3. `service/ProductServiceImplTests.java`

### Missing Tests by Category

#### Controllers (1 missing)
- `controller/CategoryController.java` → Missing: `CategoryControllerTests.java`

#### Services/Service implementations (2 missing - interfaces only)
- `service/CategoryService.java` → Already tested via: `CategoryServiceImplTests.java`
- `service/ProductService.java` → Already tested via: `ProductServiceImplTests.java`

#### Config Classes (2 missing)
- `config/KafkaConfig.java` → Missing: `KafkaConfigTests.java`
- `config/SecurityConfig.java` → Missing: `SecurityConfigTests.java`

#### Exceptions (6 missing)
- `exception/BadRequestException.java` → Missing: `BadRequestExceptionTests.java`
- `exception/ConflictException.java` → Missing: `ConflictExceptionTests.java`
- `exception/ForbiddenException.java` → Missing: `ForbiddenExceptionTests.java`
- `exception/GlobalExceptionHandler.java` → Missing: `GlobalExceptionHandlerTests.java`
- `exception/ProductNotFoundException.java` → Missing: `ProductNotFoundExceptionTests.java`
- `exception/ResourceNotFoundException.java` → Missing: `ResourceNotFoundExceptionTests.java`

#### DTOs/Models/Entities (14 missing)
- `event/ProductCreatedEvent.java` → Missing: `ProductCreatedEventTests.java`
- `event/ProductDeletedEvent.java` → Missing: `ProductDeletedEventTests.java`
- `event/ProductUpdatedEvent.java` → Missing: `ProductUpdatedEventTests.java`
- `model/Category.java` → Missing: `CategoryTests.java`
- `model/Product.java` → Missing: `ProductTests.java`
- `model/Reservation.java` → Missing: `ReservationTests.java`
- `request/CreateProductRequest.java` → Missing: `CreateProductRequestTests.java`
- `request/ReleaseStockRequest.java` → Missing: `ReleaseStockRequestTests.java`
- `request/ReserveStockRequest.java` → Missing: `ReserveStockRequestTests.java`
- `request/UpdateCategoryRequest.java` → Missing: `UpdateCategoryRequestTests.java`
- `request/UpdateProductRequest.java` → Missing: `UpdateProductRequestTests.java`
- `response/ApiResponse.java` → Missing: `ApiResponseTests.java`
- `response/CategoryResponse.java` → Missing: `CategoryResponseTests.java`
- `response/ErrorResponse.java` → Missing: `ErrorResponseTests.java`
- `response/ProductResponse.java` → Missing: `ProductResponseTests.java`

#### Repositories (3 missing)
- `repository/CategoryRepository.java` → Missing: `CategoryRepositoryTests.java`
- `repository/ProductRepository.java` → Missing: `ProductRepositoryTests.java`
- `repository/ReservationRepository.java` → Missing: `ReservationRepositoryTests.java`

#### Listeners (1 missing)
- `listener/ReservationCleanupListener.java` → Missing: `ReservationCleanupListenerTests.java`

---

## 6. USER-SERVICE

### Statistics
- **Total Source Files**: 24 (excluding Application.java)
- **Existing Test Files**: 6 (excluding ApplicationTests.java)
- **Missing Test Files**: 18
- **Coverage**: 25% (6/24)

### All Source Files (src/main/java)
1. `auth/AuthController.java`
2. `auth/AuthService.java`
3. `auth/AuthServiceImpl.java`
4. `auth/JwtFilter.java`
5. `auth/JwtUtil.java`
6. `config/SecurityConfig.java`
7. `controller/UserController.java`
8. `exception/AuthException.java`
9. `exception/BadRequestException.java`
10. `exception/ConflictException.java`
11. `exception/ForbiddenException.java`
12. `exception/GlobalExceptionHandler.java`
13. `exception/ResourceNotFoundException.java`
14. `model/Role.java`
15. `model/User.java`
16. `repository/UserRepository.java`
17. `request/LoginRequest.java`
18. `request/RegisterUserRequest.java`
19. `request/UpdateUserRequest.java`
20. `response/ErrorResponse.java`
21. `response/LoginResponse.java`
22. `response/UserResponse.java`
23. `service/UserService.java`
24. `service/UserServiceImpl.java`

### Existing Test Files (src/test/java)
1. `auth/AuthControllerTests.java`
2. `auth/AuthServiceImplTests.java`
3. `auth/JwtFilterTests.java`
4. `auth/JwtUtilTests.java`
5. `controller/UserControllerTests.java`
6. `service/UserServiceImplTests.java`

### Missing Tests by Category

#### Controllers (0 missing)
- ✅ All controllers have tests

#### Services/Service implementations (2 missing - interfaces only)
- `auth/AuthService.java` → Already tested via: `AuthServiceImplTests.java`
- `service/UserService.java` → Already tested via: `UserServiceImplTests.java`

#### Config Classes (1 missing)
- `config/SecurityConfig.java` → Missing: `SecurityConfigTests.java`

#### Exceptions (6 missing)
- `exception/AuthException.java` → Missing: `AuthExceptionTests.java`
- `exception/BadRequestException.java` → Missing: `BadRequestExceptionTests.java`
- `exception/ConflictException.java` → Missing: `ConflictExceptionTests.java`
- `exception/ForbiddenException.java` → Missing: `ForbiddenExceptionTests.java`
- `exception/GlobalExceptionHandler.java` → Missing: `GlobalExceptionHandlerTests.java`
- `exception/ResourceNotFoundException.java` → Missing: `ResourceNotFoundExceptionTests.java`

#### DTOs/Models/Entities (8 missing)
- `model/Role.java` → Missing: `RoleTests.java`
- `model/User.java` → Missing: `UserTests.java`
- `request/LoginRequest.java` → Missing: `LoginRequestTests.java`
- `request/RegisterUserRequest.java` → Missing: `RegisterUserRequestTests.java`
- `request/UpdateUserRequest.java` → Missing: `UpdateUserRequestTests.java`
- `response/ErrorResponse.java` → Missing: `ErrorResponseTests.java`
- `response/LoginResponse.java` → Missing: `LoginResponseTests.java`
- `response/UserResponse.java` → Missing: `UserResponseTests.java`

#### Repositories (1 missing)
- `repository/UserRepository.java` → Missing: `UserRepositoryTests.java`

---

## OVERALL SUMMARY

### Total Statistics Across All Services
- **Total Source Files**: 141 (excluding Application.java files)
- **Total Existing Test Files**: 21 (excluding ApplicationTests.java files)
- **Total Missing Test Files**: 120
- **Overall Coverage**: 14.9% (21/141)

### Missing Tests by Category (All Services Combined)

#### Controllers: 2 missing
- product-service: `CategoryController.java`
- gateway-service: N/A (no controllers)

#### Services/Service Implementations: 0 missing
- All service implementations are tested
- Service interfaces don't need separate tests as their implementations are tested

#### Config Classes: 18 missing
- discovery-service: 0
- gateway-service: 3 (GatewayDebugConfig, JwtDecoderConfig, SecurityConfig)
- media-service: 5 (CloudflareR2Config, CloudflareR2Properties, JwtConfig, S3Config, SecurityConfig)
- order-service: 2 (KafkaConfig, SecurityConfig)
- product-service: 2 (KafkaConfig, SecurityConfig)
- user-service: 1 (SecurityConfig)

#### DTOs/Models/Entities: 68 missing
- discovery-service: 1 (ErrorResponse)
- gateway-service: 1 (ErrorResponse)
- media-service: 9 (various models, requests, responses)
- order-service: 27 (various DTOs, models)
- product-service: 14 (various events, models, requests, responses)
- user-service: 8 (various models, requests, responses)

#### Repositories: 7 missing
- media-service: 1 (MediaRepository)
- order-service: 2 (CartRepository, OrderRepository)
- product-service: 3 (CategoryRepository, ProductRepository, ReservationRepository)
- user-service: 1 (UserRepository)

#### Exceptions: 26 missing
- discovery-service: 1 (GlobalExceptionHandler)
- gateway-service: 1 (GlobalExceptionHandler)
- media-service: 6 (various exceptions)
- order-service: 5 (various exceptions)
- product-service: 6 (various exceptions)
- user-service: 6 (various exceptions)

#### Filters/Interceptors: 1 missing
- gateway-service: 1 (JwtHeaderGatewayFilterFactory)

#### Listeners: 1 missing
- product-service: 1 (ReservationCleanupListener)

#### Clients: 1 missing
- order-service: 1 (ProductClient)

---

## PRIORITIZATION RECOMMENDATIONS

### Priority 1: High Business Value (Controllers & Services)
1. **CategoryController** (product-service) - Has business logic

### Priority 2: Critical Infrastructure (Repositories)
All 7 repository tests (data access layer):
- MediaRepository (media-service)
- CartRepository, OrderRepository (order-service)
- CategoryRepository, ProductRepository, ReservationRepository (product-service)
- UserRepository (user-service)

### Priority 3: Error Handling (Exceptions & Handlers)
- All GlobalExceptionHandler tests (6 services)
- Custom exception tests (26 total)

### Priority 4: Security & Infrastructure
- SecurityConfig tests (5 services)
- JwtHeaderGatewayFilterFactory (gateway-service)
- JwtConfig, JwtDecoderConfig tests

### Priority 5: Listeners & Clients
- ReservationCleanupListener (product-service)
- ProductClient (order-service)

### Priority 6: Configuration Classes
- Kafka, S3, Cloudflare R2 config tests (lower priority as usually configuration)

### Priority 7: DTOs/Models/Entities
- Entity validation tests
- DTO serialization/deserialization tests
- Response/Request object tests (68 total - can be batch generated)

---

## NOTES

1. **Service Interface Tests**: Service interfaces (like `UserService`, `CartService`, etc.) are marked as "interface only" because they are adequately tested through their implementation tests. No separate tests are needed for interfaces.

2. **Application Tests**: All services have `*ApplicationTests.java` which test Spring Boot application context loading. These are excluded from the missing test counts.

3. **Test Helper Classes**: Files like `TestJwtConfig.java` in gateway-service are test utilities, not actual test files.

4. **Coverage Calculation**: Coverage is calculated as (Existing Tests / Total Source Files) excluding Application.java files.

5. **Best Practices**: 
   - DTOs/Models should have validation tests
   - Repositories should have data access tests
   - Controllers already have good coverage in most services
   - Config classes may need integration tests rather than unit tests

