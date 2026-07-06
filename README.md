# E-Commerce Platform

Full-stack e-commerce platform built with Spring Boot, React, MySQL, Redis, JWT authentication, Docker, and Docker Compose.

## Tech Stack

| Layer            | Technology                                             |
| ---------------- | ------------------------------------------------------ |
| Backend          | Java 17, Spring Boot 3, Spring Security, JPA/Hibernate |
| Frontend         | React, Vite, Axios, React Router                       |
| Database         | MySQL 8.4                                              |
| Cache            | Redis                                                  |
| Containerization | Docker, Docker Compose                                 |

## Project Structure

```
e-commerce-platform/
├── ecommerce/                  # Spring Boot backend
├── ecommerce-UI/
│   └── ecommerce-ui/           # React/Vite frontend
├── seed-data/                  # SQL seed files
├── docker-compose.yml          # Local compose
├── docker-compose.prod.yml     # Production/VPS compose
├── .env                        # Environment variables (do not commit)
└── README.md
```

## Quick Start (Local)

### Prerequisites

- Docker and Docker Compose installed

### Run

```powershell
# From the e-commerce-platform folder
docker compose down
docker compose build --no-cache
docker compose up
```

Wait for all containers to be healthy, then:

```powershell
# Seed categories and products (50 products, 10 categories)
Get-Content .\seed-data\products-categories-only.sql | docker exec -i ecommerce-mysql mysql -u root -proot ecommerce
```

### Access

| Service     | URL                     |
| ----------- | ----------------------- |
| Frontend    | http://localhost:5173   |
| Backend API | http://localhost:8080   |
| MySQL       | localhost:3307 (mapped) |
| Redis       | localhost:6379          |

## Environment Variables

Copy `.env` and update values:

| Variable              | Description                               |
| --------------------- | ----------------------------------------- |
| `MYSQL_ROOT_PASSWORD` | MySQL root password                       |
| `MYSQL_USER`          | MySQL application user                    |
| `MYSQL_PASSWORD`      | MySQL application user password           |
| `JWT_SECRET`          | Secret key for JWT signing (min 32 chars) |
| `VITE_API_BASE_URL`   | API base URL for frontend                 |

## API Endpoints

### Auth

- `POST /api/auth/register` — Register new user
- `POST /api/auth/login` — Login, returns JWT
- `GET /api/auth/me` — Current user info

### Products

- `GET /api/products` — List all products
- `GET /api/products/{id}` — Get product by ID
- `POST /api/products` — Create (ADMIN)
- `PUT /api/products/{id}` — Update (ADMIN)
- `DELETE /api/products/{id}` — Soft delete (ADMIN)

### Categories

- `GET /api/categories` — List all categories
- `GET /api/categories/{id}` — Get category by ID
- `POST /api/categories` — Create (ADMIN)
- `PUT /api/categories/{id}` — Update (ADMIN)
- `DELETE /api/categories/{id}` — Delete (ADMIN)

### Cart

- `GET /api/cart` — Get current user's cart
- `POST /api/cart/items` — Add item to cart
- `PUT /api/cart/items/{id}` — Update cart item
- `DELETE /api/cart/items/{id}` — Remove cart item

### Orders

- `POST /api/orders` — Place order
- `GET /api/orders` — Get user's orders
- `GET /api/orders/{id}` — Get order detail
- `PUT /api/orders/{id}/status` — Update status (ADMIN)
- `DELETE /api/orders/{id}` — Cancel order

### Payments

- `POST /api/payments` — Process payment
- `GET /api/payments/order/{orderId}` — Get payment by order

### Users

- `GET /api/users` — List all users (ADMIN)
- `PUT /api/users/{id}` — Update user (ADMIN)
- `DELETE /api/users/{id}` — Delete user (ADMIN)

## Deployment (VPS)

```bash
# Install Docker
sudo apt update && sudo apt install -y docker.io docker-compose-plugin

# Clone and deploy
git clone <repo-url> e-commerce-platform
cd e-commerce-platform
nano .env                          # Set production values
docker compose -f docker-compose.prod.yml build --no-cache
docker compose -f docker-compose.prod.yml up -d
```

## License

MIT
