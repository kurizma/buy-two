# Postman API Testing Guide - Error Response Validation

## Base Configuration

**Base URL (Gateway)**: `https://localhost:8080`  
**Note**: Gateway uses SSL, you may need to disable SSL verification in Postman

---

## 1. Authentication Flow (Get JWT Token)

### ✅ Register New User (Successful)
```
POST https://localhost:8080/auth/register

Headers:
Content-Type: application/json

Body (JSON):
{
  "name": "John Seller",
  "email": "john.seller@example.com",
  "password": "password123",
  "role": "SELLER"
}

Expected Success Response (201):
{
  "id": "...",
  "name": "John Seller",
  "email": "john.seller@example.com",
  "role": "SELLER",
  ...
}
```

### ✅ Login (Get JWT Token)
```
POST https://localhost:8080/auth/login

Headers:
Content-Type: application/json

Body (JSON):
{
  "email": "john.seller@example.com",
  "password": "password123"
}

Expected Success Response (200):
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful",
  "user": {
    "id": "...",
    "name": "John Seller",
    "email": "john.seller@example.com",
    "role": "SELLER"
  }
}

⚠️ SAVE THE TOKEN - You'll need it for authenticated requests!
```

---

## 2. Error Responses Testing

### 🔴 400 Bad Request - Validation Errors

#### Test 1: Missing Required Fields (Product Creation)
```
POST https://localhost:8080/products

Headers:
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

Body (JSON) - INVALID:
{
  "description": "A product without name or price"
}

Expected Error Response (400):
{
  "code": "400",
  "message": "Validation failed",
  "details": {
    "name": "Product name is required",
    "price": "Price is required",
    "quantity": "Quantity is required"
  }
}
```

#### Test 2: Invalid Field Values (User Registration)
```
POST https://localhost:8080/auth/register

Headers:
Content-Type: application/json

Body (JSON) - INVALID:
{
  "name": "A",
  "email": "not-an-email",
  "password": "short",
  "role": "SELLER"
}

Expected Error Response (400):
{
  "code": "400",
  "message": "Validation failed",
  "details": {
    "name": "Name must be between 2 and 40 characters",
    "email": "Invalid email format",
    "password": "Password must be between 8 and 24 characters"
  }
}
```

#### Test 3: Invalid Price/Quantity (Product Creation)
```
POST https://localhost:8080/products

Headers:
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

Body (JSON) - INVALID:
{
  "name": "Test Product",
  "price": -10.50,
  "quantity": -5
}

Expected Error Response (400):
{
  "code": "400",
  "message": "Validation failed",
  "details": {
    "price": "Price must be at least 0.01",
    "quantity": "Quantity must be at least 1"
  }
}
```

#### Test 3b: Zero or Decimal Quantity (Product Creation)
```
POST https://localhost:8080/products

Headers:
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

Body (JSON) - INVALID:
{
  "name": "Test Product",
  "price": 29.99,
  "quantity": 0
}

Expected Error Response (400):
{
  "code": "400",
  "message": "Validation failed",
  "details": {
    "quantity": "Quantity must be at least 1"
  }
}

Note: Decimal values like 0.11 will be rejected by type validation since quantity is Integer.
```

### 🔴 401 Unauthorized - Missing/Invalid Token

#### Test 4: Access Protected Route Without Token
```
POST https://localhost:8080/products

Headers:
Content-Type: application/json
(NO Authorization header)

Body (JSON):
{
  "name": "Test Product",
  "price": 100,
  "quantity": 10
}

Expected Error Response (401):
{
  "code": "401",
  "message": "Unauthorized: JWT token is missing or invalid",
  "details": null
}
```

#### Test 5: Invalid/Expired Token
```
POST https://localhost:8080/products

Headers:
Content-Type: application/json
Authorization: Bearer invalid.token.here

Body (JSON):
{
  "name": "Test Product",
  "price": 100,
  "quantity": 10
}

Expected Error Response (401):
{
  "code": "401",
  "message": "Unauthorized: Invalid JWT token",
  "details": null
}
```

### 🔴 403 Forbidden - Insufficient Permissions

#### Test 6: CLIENT Role Trying to Create Product (SELLER only)
```
POST https://localhost:8080/products

Headers:
Content-Type: application/json
Authorization: Bearer <client-jwt-token>

Body (JSON):
{
  "name": "Test Product",
  "price": 100,
  "quantity": 10
}

Expected Error Response (403):
{
  "code": "403",
  "message": "Only sellers can create products.",
  "details": null
}

Note: You need to register/login as CLIENT role first to test this
```

#### Test 7: Update Another Seller's Product
```
PUT https://localhost:8080/products/{another-sellers-product-id}

Headers:
Content-Type: application/json
Authorization: Bearer <your-seller-jwt-token>

Body (JSON):
{
  "name": "Updated Name",
  "price": 200
}

Expected Error Response (403):
{
  "code": "403",
  "message": "You do not have permission to update this product",
  "details": null
}
```

### 🔴 404 Not Found - Resource Doesn't Exist

#### Test 8: Get Non-existent Product
```
GET https://localhost:8080/products/nonexistent-id-12345

Expected Error Response (404):
{
  "code": "404",
  "message": "Product not found with id: nonexistent-id-12345",
  "details": null
}
```

#### Test 9: Get Non-existent User
```
GET https://localhost:8080/api/users/nonexistent-user-id

Expected Error Response (404):
{
  "code": "404",
  "message": "User not found",
  "details": null
}
```

### 🔴 405 Method Not Allowed

#### Test 10: Wrong HTTP Method
```
PATCH https://localhost:8080/products/{product-id}

Headers:
Authorization: Bearer <your-jwt-token>

Expected Error Response (405):
{
  "code": "405",
  "message": "Method not allowed",
  "details": null
}
```

### 🔴 413 Payload Too Large (Media Service)

#### Test 11: Upload File > 2MB
```
POST https://localhost:8080/media/upload

Headers:
Authorization: Bearer <your-jwt-token>
Content-Type: multipart/form-data

Body:
file: [Select a file larger than 2MB]

Expected Error Response (413):
{
  "code": "413",
  "message": "File exceeds 2MB size limit",
  "details": null
}
```

### 🔴 409 Conflict - Duplicate Resource

#### Test 12: Register with Existing Email
```
POST https://localhost:8080/auth/register

Headers:
Content-Type: application/json

Body (JSON):
{
  "name": "Another User",
  "email": "john.seller@example.com",  // Already registered
  "password": "password123",
  "role": "CLIENT"
}

Expected Error Response (409):
{
  "code": "409",
  "message": "Email already exists",
  "details": null
}
```

---

## 3. Successful API Calls (✅ Correct Format)

### Create Product (Successful)
```
POST https://localhost:8080/products

Headers:
Content-Type: application/json
Authorization: Bearer <seller-jwt-token>

Body (JSON):
{
  "name": "Wireless Mouse",
  "description": "Ergonomic wireless mouse with USB receiver",
  "price": 29.99,
  "quantity": 100,
  "categoryId": "electronics-123",
  "images": ["https://example.com/image1.jpg"]
}

Expected Success Response (201):
{
  "success": true,
  "message": "Product created successfully",
  "data": {
    "id": "...",
    "name": "Wireless Mouse",
    "price": 29.99,
    "quantity": 100,
    ...
  }
}
```

### Get All Products (Public - No Auth Required)
```
GET https://localhost:8080/products

Expected Success Response (200):
{
  "success": true,
  "message": "Products fetched successfully",
  "data": [
    {
      "id": "...",
      "name": "Product 1",
      "price": 100,
      ...
    },
    ...
  ]
}
```

### Search Products with Filters (Public - Faceted Search)
```
GET https://localhost:8080/products/search

Query Parameters (all optional):
- keyword: Search in name/description (e.g., "laptop", "wireless")
- minPrice: Minimum price filter (e.g., 100)
- maxPrice: Maximum price filter (e.g., 500)
- categoryId: Filter by category ID
- page: Page number (default: 0)
- size: Items per page (default: 10)

Example 1 - Keyword Search:
GET https://localhost:8080/products/search?keyword=laptop

Example 2 - Price Range:
GET https://localhost:8080/products/search?minPrice=100&maxPrice=500

Example 3 - Category Filter:
GET https://localhost:8080/products/search?categoryId=electronics-123

Example 4 - Combined Filters with Pagination:
GET https://localhost:8080/products/search?keyword=mouse&minPrice=20&maxPrice=50&page=0&size=20

Example 5 - Only Min Price (max defaults to 9999999):
GET https://localhost:8080/products/search?minPrice=50

Expected Success Response (200):
{
  "success": true,
  "message": "Search results fetched successfully",
  "data": {
    "content": [
      {
        "id": "...",
        "name": "Wireless Mouse",
        "price": 29.99,
        "description": "Ergonomic wireless mouse",
        ...
      },
      ...
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {...},
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 5,
    "totalElements": 47,
    "last": false,
    "size": 10,
    "number": 0,
    "numberOfElements": 10,
    "first": true,
    "empty": false
  }
}
```

### Get Product by ID (Public)
```
GET https://localhost:8080/products/{product-id}

Expected Success Response (200):
{
  "success": true,
  "message": "Product fetched successfully",
  "data": {
    "id": "...",
    "name": "Wireless Mouse",
    "price": 29.99,
    ...
  }
}
```

### Update Product (Successful)
```
PUT https://localhost:8080/products/{your-product-id}

Headers:
Content-Type: application/json
Authorization: Bearer <seller-jwt-token>

Body (JSON):
{
  "name": "Updated Product Name",
  "description": "Updated description",
  "price": 39.99,
  "quantity": 150
}

Expected Success Response (200):
{
  "success": true,
  "message": "Product updated successfully",
  "data": {
    "id": "...",
    "name": "Updated Product Name",
    ...
  }
}
```

### Delete Product (Successful)
```
DELETE https://localhost:8080/products/{your-product-id}

Headers:
Authorization: Bearer <seller-jwt-token>

Expected Success Response (200):
{
  "success": true,
  "message": "Product deleted successfully",
  "data": null
}
```

### Get Categories (Public)
```
GET https://localhost:8080/categories

Expected Success Response (200):
{
  "success": true,
  "message": "Categories fetched successfully",
  "data": [
    {
      "id": "...",
      "name": "Electronics",
      ...
    },
    ...
  ]
}
```

### Add Item to Cart (CLIENT only)
```
POST https://localhost:8080/api/cart/items

Headers:
Content-Type: application/json
Authorization: Bearer <client-jwt-token>

Body (JSON):
{
  "productId": "product-id-123",
  "sellerId": "seller-id-456",
  "quantity": 2,
  "price": 29.99
}

Expected Success Response (200):
{
  "success": true,
  "message": "Item added to cart successfully",
  "data": {
    "userId": "...",
    "items": [...],
    "subtotal": 59.98,
    ...
  }
}
```

### Get Cart (CLIENT only)
```
GET https://localhost:8080/api/cart

Headers:
Authorization: Bearer <client-jwt-token>

Expected Success Response (200):
{
  "success": true,
  "message": "Cart fetched successfully",
  "data": {
    "userId": "...",
    "items": [...],
    "subtotal": 59.98,
    ...
  }
}
```

---

## 4. Testing Workflow

1. **Start all services** (discovery, gateway, all business services)
2. **Register users** with different roles:
   - One as SELLER
   - One as CLIENT
3. **Login** and save both JWT tokens
4. **Test validation errors** (400) - use invalid data
5. **Test auth errors** (401) - remove/invalidate token
6. **Test permission errors** (403) - use wrong role token
7. **Test not found errors** (404) - use non-existent IDs
8. **Test successful operations** - verify correct responses

---

## 5. Postman Environment Variables (Recommended)

Create environment variables in Postman:

```
base_url = https://localhost:8080
seller_token = <paste-seller-jwt-token>
client_token = <paste-client-jwt-token>
product_id = <paste-created-product-id>
```

Then use: `{{base_url}}/products` and `Authorization: Bearer {{seller_token}}`

---

## 6. Error Response Schema Reference

All errors follow this format:
```json
{
  "code": "HTTP status code as string",
  "message": "Human-readable error message",
  "details": "null or object with field-level errors"
}
```

**Validation Errors Include Field Details:**
```json
{
  "code": "400",
  "message": "Validation failed",
  "details": {
    "fieldName1": "error message 1",
    "fieldName2": "error message 2"
  }
}
```

**Other Errors Have No Details:**
```json
{
  "code": "404",
  "message": "Resource not found",
  "details": null
}
```
