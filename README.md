# grit:Tee E-Commerce Marketplace (buy-two)

## Table of Contents
- [Overview](#overview)
- [Architecture & Tech Stack](#architecture--tech-stack)
- [Core Features](#core-features)
- [API Documentation](#api-documentation)
- [Error Handling](#error-handling)
- [CI/CD & Code Quality](#cicd--code-quality)
- [Installation & Setup](#installation--setup)
- [Creators](#creators)

***

## Overview
**grit:Tee** is a full e-commerce marketplace designed for T-shirt creators and fans.  
This project (**buy-two phase**) finalizes the platform by integrating a microservice architecture with core features such as a shopping cart, order management, user/seller analytics, and faceted product search.  

The system emphasizes **security**, **performance**, and **user experience**, supported by a robust CI/CD pipeline.

[Back to top](#table-of-contents)

***

## Architecture & Tech Stack
- **Architecture**: Microservice-oriented with a central API Gateway & Service Discovery  
- **Frontend**: 
  - Angular 17+ (Angular Material, Ng2-charts)  
- **Backend**: Spring Boot 3.x Microservices  
  - **Discovery Service**: Eureka-based service registry  
  - **Gateway Service**: Spring Cloud Gateway (JWT validation, CORS, header injection)  
  - **User Service**: Authentication & profile management  
  - **Product Service**: Catalog management & faceted search  
  - **Order Service**: Shopping cart, order lifecycle, analytics pipelines  
  - **Media Service**: Image storage using Cloudflare R2/S3  
- **Infrastructure**:
    - Host Platform: Hetzner Virtual Machine (Serves as the Single Source of Truth for Jenkins automation)
    - Database: MongoDB Atlas (Cloud)
    - Object Storage: Cloudflare R2 Object Storage
    - Quality Assurance: SonarCloud.io integration for centralized code metrics
    - Messaging: Apache Kafka for async service events


[Back to top](#table-of-contents)

***

## Core Features

### 1. Faceted Product Search & Pagination
- Regex-based keyword search on product names/descriptions  
- Price and category facets  
- Paginated results to maintain performance at scale  

### 2. Two-Step Checkout & Reservation Logic
- **Step 1 (Pending):** Reserve stock at checkout initiation  
- **Step 2 (Commit):** Confirm order → convert reservation to permanent stock deduction  
- **Cleanup:** Scheduled task runs every 60s to release expired reservations  

### 3. Advanced Analytics Pipelines
- **Client View:** Total spend, top categories, and most purchased products  
- **Seller View:** Total revenue, best-selling products, and units sold  

[Back to top](#table-of-contents)

***

## API Documentation

<details>
<summary><strong>Click to expand API Endpoints</strong></summary>

### Security
**Base URL:** `https://localhost:8080`  
**Auth:** Bearer `<JWT>` in the `Authorization` header  
**Gateway Headers:** Injected — `X-USER-ID` and `X-USER-ROLE`

***

### 1. Authentication (User Service)

| Method | Endpoint             | Access | Description                          |
|--------|----------------------|---------|--------------------------------------|
| POST   | /auth/register       | Public  | Register as CLIENT or SELLER         |
| POST   | /auth/login          | Public  | Returns JWT and user profile         |
| GET    | /api/users/me        | Auth    | Retrieve current authenticated user  |

***

### 2. Product Catalog (Product Service)

| Method | Endpoint              | Access | Description                      |
|--------|------------------------|---------|----------------------------------|
| GET    | /products/search       | Public  | Faceted search                   |
| POST   | /products              | Seller  | Create new product listing       |
| PUT    | /products/{id}         | Seller  | Update owned product             |
| DELETE | /products/{id}         | Seller  | Delete product & associated media|

***

### 3. Shopping Cart & Orders (Order Service)

| Method | Endpoint                   | Access | Description                             |
|--------|-----------------------------|---------|-----------------------------------------|
| POST   | /api/cart/items             | Client  | Add item to cart (valid stock check)    |
| POST   | /api/orders/checkout        | Client  | Initiate order & reserve stock          |
| POST   | /api/orders/{num}/confirm   | Client  | Confirm order to commit stock           |
| POST   | /api/orders/{num}/redo      | Client  | Re-add cancelled order items to cart    |
| PUT    | /api/orders/{num}/status    | Seller  | Update status (e.g. SHIPPED, DELIVERED) |

***

### 4. Analytics (Order Service)

| Method | Endpoint                    | Access | Description                          |
|--------|------------------------------|---------|--------------------------------------|
| GET    | /api/analytics/client/{id}   | Client  | Aggregate spend & category metrics   |
| GET    | /api/analytics/seller/{id}   | Seller  | Aggregate revenue & unit metrics     |

</details>
</br>

[Back to top](#table-of-contents)

***

## Error Handling
All services use a standardized JSON error schema:

```json
{
  "code": "400",
  "message": "Validation failed",
  "details": {
    "price": "Must be greater than 0",
    "quantity": "Required field"
  }
}
```

**Common status codes:**  
400 (Validation), 401/403 (Auth), 404 (Missing), 409 (Conflict)

[Back to top](#table-of-contents)

***

## CI/CD & Code Quality
The project utilizes a centralized **Green Pipeline Strategy** hosted on a remote VM. This environment serves as the Single Source of Truth, ensuring that all builds, tests, and quality checks are executed in a consistent, production-like environment—no longer fragmented across developer machines.

- **Jenkins Pipeline**: Orchestrates the full lifecycle from Checkout to Deploy & Verify.
- **Cloud Quality Gates**: 
  - Every Pull Request is automatically analyzed by SonarCloud.io. 
    - The pipeline enforces sonar.qualitygate.wait=true, ensuring that no code with security vulnerabilities or maintainability issues is merged into the main branch.
- **Automated Staging**: Verified builds are automatically containerized and deployed to a staging environment for final validation.
- **Build & Test:** Maven builds, JUnit/Mockito tests  
- **Quality Gate:** SonarQube with `sonar.qualitygate.wait=true`  
- **Dockerization:** Auto build and push with versioned tags  
- **Deployment:** Automated deploy + verify with rollback support  




[Back to top](#table-of-contents)


***

## Installation & Setup

### Prerequisites
- Docker & Docker Compose  
- MongoDB Atlas URI and Kafka environment variables  
- Cloudflare R2 credentials (for media storage)  

### Startup Steps

<details> <summary><strong>Click to expand Startup Steps</strong></summary>

#### 1. Start Infrastructure Services
Navigate to each infra directory and start services:

**SonarQube:**
```bash
cd root/infra/sonarqube
docker-compose up -d
```

**Jenkins:**
```bash
cd root/infra/jenkins
docker-compose up -d
```

#### 2. Start Marketplace
```bash
# From project root
docker-compose up -d
```

#### 3. Access Services
- **Frontend:** `https://localhost:4200`  
- **Jenkins:** `http://localhost:8080` (default port)  
- **SonarQube:** `http://localhost:9000` (default port)  

</details>
</br>

[Back to top](#table-of-contents)

***


## Creators
- **Mayuree Reunsati** - [GitHub](https://github.com/mareerray)
- **Joon Kim** - [GitHub](https://github.com/kurizma)
- **Toft Diederichs** - [GitHub](https://github.com/Toft08)
- **Chan Myint** - [GitHub](https://github.com/cmbigk)

[Back to top](#table-of-contents)

***

