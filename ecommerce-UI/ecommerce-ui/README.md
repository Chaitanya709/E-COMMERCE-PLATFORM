# Ecommerce UI Starter

This is a clean React + Vite starter setup for your Spring Boot ecommerce backend.

## What is included

- React app setup using Vite
- React Router page routing
- Axios instance with JWT bearer token interceptor
- Auth context
- Protected user routes
- Admin-only routes
- Public, user, and admin page placeholders
- Basic CSS
- API files for auth, products, cart, and orders

## Folder structure

```txt
src
 ├── api
 │    ├── authApi.js
 │    ├── axiosInstance.js
 │    ├── cartApi.js
 │    ├── orderApi.js
 │    └── productApi.js
 │
 ├── assets
 │
 ├── components
 │    ├── AdminRoute.jsx
 │    ├── Footer.jsx
 │    ├── Navbar.jsx
 │    ├── ProductCard.jsx
 │    └── ProtectedRoute.jsx
 │
 ├── context
 │    └── AuthContext.jsx
 │
 ├── pages
 │    ├── public
 │    │    ├── Home.jsx
 │    │    ├── Login.jsx
 │    │    ├── NotFound.jsx
 │    │    ├── ProductDetails.jsx
 │    │    ├── Products.jsx
 │    │    ├── ProductsByCategory.jsx
 │    │    └── Register.jsx
 │    │
 │    ├── user
 │    │    ├── Cart.jsx
 │    │    ├── Checkout.jsx
 │    │    ├── OrderDetails.jsx
 │    │    ├── Orders.jsx
 │    │    └── Profile.jsx
 │    │
 │    └── admin
 │         ├── AddProduct.jsx
 │         ├── AdminCategories.jsx
 │         ├── AdminDashboard.jsx
 │         ├── AdminOrders.jsx
 │         ├── AdminProducts.jsx
 │         ├── AdminUsers.jsx
 │         └── EditProduct.jsx
 │
 ├── routes
 │    └── AppRoutes.jsx
 │
 ├── styles
 │    └── global.css
 │
 ├── App.jsx
 └── main.jsx
```

## Setup instructions

### 1. Unzip the project

```bash
unzip ecommerce-ui-starter.zip
cd ecommerce-ui-starter
```

### 2. Install dependencies

```bash
npm install
```

### 3. Create environment file

Copy `.env.example` to `.env`:

```bash
cp .env.example .env
```

For Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

### 4. Start React app

```bash
npm run dev
```

Open:

```txt
http://localhost:5173
```

## Backend connection

The frontend expects your backend API base URL here:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

If your Spring Boot backend route is different, update `.env`.

## Important backend CORS reminder

Your Spring Boot backend must allow requests from:

```txt
http://localhost:5173
```

Without CORS configuration, your React app may open but API calls will fail.

## Build order you should follow

Do not randomly build admin screens first. Follow this order:

1. Navbar and routing
2. Products page
3. Product details page
4. Login and register
5. JWT storage and protected routes
6. Cart page
7. Checkout page
8. Orders page
9. Admin product and category pages

Build the boring working flow first. Styling can improve later.
