-- Seed data for ecommerce application

-- Users (passwords are BCrypt encoded for "password123")
INSERT INTO users (name, email, password, role, created_at) VALUES
('Admin User', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', NOW()),
('John Doe', 'john@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', NOW()),
('Jane Smith', 'jane@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', NOW());

-- Categories
INSERT INTO category (name, description, deleted) VALUES
('Electronics', 'Electronic devices and gadgets', 0),
('Clothing', 'Apparel and fashion accessories', 0),
('Books', 'Physical and digital books', 0),
('Home & Kitchen', 'Furniture, appliances and kitchen essentials', 0),
('Sports', 'Sports equipment and fitness gear', false);

-- Products
INSERT INTO product (name, description, price, stock_quantity, image_url, category_id, created_at, updated_at, deleted) VALUES
('Laptop Pro 15', 'High-performance laptop with 16GB RAM and 512GB SSD', 1299.99, 25, 'https://placehold.co/400x300?text=Laptop', 1, NOW(), NOW(), 0),
('Wireless Mouse', 'Ergonomic wireless mouse with USB receiver', 29.99, 150, 'https://placehold.co/400x300?text=Mouse', 1, NOW(), NOW(), 0),
('USB-C Hub', '7-in-1 USB-C hub with HDMI and SD card reader', 49.99, 80, 'https://placehold.co/400x300?text=Hub', 1, NOW(), NOW(), 0),
('Mechanical Keyboard', 'RGB mechanical keyboard with Cherry MX switches', 89.99, 60, 'https://placehold.co/400x300?text=Keyboard', 1, NOW(), NOW(), 0),
('T-Shirt Classic', 'Comfortable cotton t-shirt in multiple colors', 19.99, 200, 'https://placehold.co/400x300?text=T-Shirt', 2, NOW(), NOW(), 0),
('Denim Jeans', 'Slim-fit stretch denim jeans', 49.99, 100, 'https://placehold.co/400x300?text=Jeans', 2, NOW(), NOW(), 0),
('Running Shoes', 'Lightweight running shoes with cushioning', 79.99, 75, 'https://placehold.co/400x300?text=Shoes', 2, NOW(), NOW(), 0),
('Java Programming', 'Complete guide to Java programming', 39.99, 120, 'https://placehold.co/400x300?text=Java+Book', 3, NOW(), NOW(), 0),
('Spring Boot in Action', 'Practical guide to Spring Boot development', 44.99, 90, 'https://placehold.co/400x300?text=Spring+Book', 3, NOW(), NOW(), 0),
('Coffee Maker', ' programmable drip coffee maker 12-cup', 59.99, 40, 'https://placehold.co/400x300?text=Coffee+Maker', 4, NOW(), NOW(), 0),
('Blender Pro', 'High-speed blender for smoothies and soups', 69.99, 35, 'https://placehold.co/400x300?text=Blender', 4, NOW(), NOW(), 0),
('Yoga Mat', 'Non-slip exercise yoga mat 6mm thick', 24.99, 110, 'https://placehold.co/400x300?text=Yoga+Mat', 5, NOW(), NOW(), 0),
('Dumbbells Set', 'Adjustable dumbbells set 5-25 lbs', 149.99, 30, 'https://placehold.co/400x300?text=Dumbbells', 5, NOW(), NOW(), 0),
('Wireless Earbuds', 'Bluetooth earbuds with noise cancellation', 59.99, 95, 'https://placehold.co/400x300?text=Earbuds', 1, NOW(), NOW(), 0),
('Winter Jacket', 'Waterproof winter jacket with fleece lining', 89.99, 45, 'https://placehold.co/400x300?text=Jacket', 2, NOW(), NOW(), false);
