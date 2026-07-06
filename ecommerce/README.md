# E-Commerce Backend

## Current Version
v1.0-monolith

## Tech Stack
- Java
- Spring Boot
- MySQL
- Hibernate / JPA
- Spring Security (JWT-based authentication)

## Modules
- Category
- Product
- User
- Cart
- Order

## Features
- CRUD APIs for Category, Product, User, Cart, and Order
- Cart management (add, update, remove items)
- Order placement and order history
- DTOs and Mappers for clean request/response separation
- Bean validation (request payload validation)
- Global exception handling (custom error responses)
- Spring Security with JWT authentication
- Role-based access control (USER, ADMIN)
- Soft delete support for Product (prevents FK constraint issues with existing orders)

## How to Run
1. Create a MySQL database
2. Update `application.properties` with your DB credentials and JWT secret
3. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

## API Modules
- Category APIs
- Product APIs
- User APIs
- Cart APIs
- Order APIs
- Auth APIs (login, register, JWT token generation)

## Authentication & Authorization
- JWT-based stateless authentication
- Role-based access:
  - `USER` — manage own cart, place orders, view own order history
  - `ADMIN` — manage categories and products, view all orders

## Next Phase
- Refresh token support
- Refactor toward microservices (Product, Order, User as separate services)
- API rate limiting and request logging
- Caching for Product/Category APIs
