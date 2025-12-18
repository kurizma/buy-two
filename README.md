<h1 id="top">buy-one</h1>
buy-one is an end-to-end e-commerce platform built with Spring Boot microservices and Angular, allowing clients to browse products and sellers to manage their own catalog and product media.

---

![App screenshot](./frontend/src/assets/images/projectOverview.jpg)

## Features

- User registration as CLIENT or SELLER with JWT-based authentication and authorization.
- Seller-only product CRUD with ownership checks enforced at the API layer.
- Media service for product images with `image/*` validation and a 2MB size limit.
- API gateway and discovery service for routing, security, and service registration.
- Angular SPA for product browsing, seller dashboard, and profile management.

## Architecture

The backend is composed of separate microservices for users, products, media, an API gateway, and a discovery service, with an Angular SPA as the frontend.
Services communicate over REST via the gateway, use MongoDB for persistence, and store images in object storage such as S3 or Cloudflare R2.

<details>
<summary>Project structure</summary>

```text
buy-one/
├── docker-compose.yml
├── start-app.sh
├── start-backend.sh
├── start-frontend.sh
├── stop-app.sh
├── README.md
├── backend/
│   ├── discovery-service/
│   ├── gateway-service/
│   ├── user-service/
│   ├── product-service/
│   └── media-service/
└── frontend/
    ├── Dockerfile
    ├── angular.json
    ├── src/
    │   ├── app/        # components, guards, models, services
    │   ├── assets/
    │   └── environments/
    └── Doc/
```

</details>

## Tech stack

- **Backend:** Java, Spring Boot, Spring Security, Spring Cloud (Gateway, Eureka/Discovery).
- **Frontend:** Angular SPA.
- **Database:** MongoDB per service.
- **Messaging:** Kafka for product events (created/updated/deleted).
- **Infrastructure:** Docker / Docker Compose.
- **Auth:** JWT tokens issued by the User Service and validated at the gateway and services.

## Getting started

### Prerequisites

- Java (e.g., 17+).
- Node.js and npm.
- Docker and Docker Compose installed.
- Storage - Using Cloud, No need for local install
  - MongoDB: Atlas Cluster
  - Cloudflare R2
- Apache/kafka using Docker Compose.

### Installation

- Clone the repository.
- Build backend services: `cd backend/<service> && ./mvnw clean package`.
- Install frontend dependencies: `cd frontend && npm install`.

### Running the services

- Preferred: `docker compose up` from the project root to start all services and the frontend.
- Manual: start discovery, then gateway, then user/product/media services, then run `npm start` (or `ng serve`) in `frontend`.

## Usage

### Authentication and roles

- Register as CLIENT or SELLER via `POST /auth/register`.
- Log in via `POST /auth/login` to obtain a JWT and pass it in the `Authorization: Bearer <token>` header.
- SELLER users can create, update, and delete only their own products and related media; CLIENT users can browse products.

### Core flows

- CLIENT: register → login → browse products via `/products` and view details and images.
- SELLER: register as seller → login → create products (`POST /products`) → upload images (`POST /media/images`) → manage own catalog.

## API overview

- **User Service**
    - `POST /auth/register` – Register as client or seller.
    - `POST /auth/login` – Obtain JWT.
    - `GET /me` – Get current user profile.

- **Product Service**
    - `GET /products` – List products (public).
    - `GET /products/{id}` – Get product by id (public).
    - `POST /products` – Create product (SELLER only).
    - `PUT /products/{id}` – Update own product (SELLER only).
    - `DELETE /products/{id}` – Delete own product (SELLER only).

- **Media Service**
    - `POST /media/images` – Upload image for a product (SELLER only, `image/*`, ≤2MB).
    - `GET /media/images/{id}` – Download image (public).
    - `DELETE /media/images/{id}` – Delete image (owner optional).

## Security

The platform uses JWT authentication with tokens issued by the User Service and validated at the gateway and downstream services.
Passwords are hashed with BCrypt, access is controlled by roles (CLIENT, SELLER), product and media ownership is enforced, and CORS/HTTPS are configured at the gateway level.

## Configuration

Key configuration includes MongoDB connection strings, JWT secret/keys, Kafka broker settings, and object storage credentials exposed as environment variables.
Service-specific settings are defined in `application.yml` / `application-docker.yml`, and secrets should be provided via environment variables or secret management rather than committed files.

## Testing

Backend tests can be run with `./mvnw test` inside each service module.
Frontend tests can be executed from the `frontend` folder with `npm test` or `ng test`, and additional integration tests can be wired to run against the dockerized stack.

## Roadmap

- Shopping cart, orders, and payment integration.
- Product search and filtering improvements.
- User reviews and ratings.

## Created by

[Mayuree Reunsati](https://github.com/mareerray) <br>
[Joon Kim](https://github.com/kurizma)

[Back to top](#top)