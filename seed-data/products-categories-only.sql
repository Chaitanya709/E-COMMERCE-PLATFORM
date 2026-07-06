
SET FOREIGN_KEY_CHECKS = 0;

-- Make sure categories exist with known IDs
REPLACE INTO category (id, name, description, deleted) VALUES
(1, 'Electronics', 'Electronic devices and gadgets', 0),
(2, 'Clothing', 'Apparel and fashion accessories', 0),
(3, 'Books', 'Physical and digital books', 0),
(4, 'Home & Kitchen', 'Furniture, appliances and kitchen essentials', 0),
(5, 'Sports', 'Sports equipment and fitness gear', 0),
(6, 'Beauty', 'Skincare, makeup and beauty products', 0),
(7, 'Toys & Games', 'Toys, board games and puzzles', 0),
(8, 'Automotive', 'Car accessories and maintenance', 0),
(9, 'Garden', 'Gardening tools and outdoor supplies', 0),
(10, 'Music', 'Musical instruments and audio gear', 0);

-- Products (50)
INSERT INTO product (name, description, price, stock_quantity, image_url, category_id, created_at, updated_at, deleted) VALUES
-- Electronics (5)
('Laptop Pro 15', 'High-performance laptop with 16GB RAM and 512GB SSD', 1299.99, 25, 'https://placehold.co/400x300?text=Laptop', 1, NOW(), NOW(), 0),
('Wireless Mouse', 'Ergonomic wireless mouse with USB receiver', 29.99, 150, 'https://placehold.co/400x300?text=Mouse', 1, NOW(), NOW(), 0),
('USB-C Hub', '7-in-1 USB-C hub with HDMI and SD card reader', 49.99, 80, 'https://placehold.co/400x300?text=Hub', 1, NOW(), NOW(), 0),
('Mechanical Keyboard', 'RGB mechanical keyboard with Cherry MX switches', 89.99, 60, 'https://placehold.co/400x300?text=Keyboard', 1, NOW(), NOW(), 0),
('Wireless Earbuds', 'Bluetooth earbuds with noise cancellation', 59.99, 95, 'https://placehold.co/400x300?text=Earbuds', 1, NOW(), NOW(), 0),

-- Clothing (5)
('T-Shirt Classic', 'Comfortable cotton t-shirt in multiple colors', 19.99, 200, 'https://placehold.co/400x300?text=TShirt', 2, NOW(), NOW(), 0),
('Denim Jeans', 'Slim-fit stretch denim jeans', 49.99, 100, 'https://placehold.co/400x300?text=Jeans', 2, NOW(), NOW(), 0),
('Running Shoes', 'Lightweight running shoes with cushioning', 79.99, 75, 'https://placehold.co/400x300?text=Shoes', 2, NOW(), NOW(), 0),
('Winter Jacket', 'Waterproof winter jacket with fleece lining', 89.99, 45, 'https://placehold.co/400x300?text=Jacket', 2, NOW(), NOW(), 0),
('Cotton Hoodie', 'Soft cotton hoodie with kangaroo pocket', 39.99, 120, 'https://placehold.co/400x300?text=Hoodie', 2, NOW(), NOW(), 0),

-- Books (5)
('Java Programming', 'Complete guide to Java programming', 39.99, 120, 'https://placehold.co/400x300?text=Java+Book', 3, NOW(), NOW(), 0),
('Spring Boot in Action', 'Practical guide to Spring Boot development', 44.99, 90, 'https://placehold.co/400x300?text=Spring+Book', 3, NOW(), NOW(), 0),
('Clean Code', 'A handbook of agile software craftsmanship', 34.99, 80, 'https://placehold.co/400x300?text=Clean+Code', 3, NOW(), NOW(), 0),
('Design Patterns', 'Elements of reusable object-oriented software', 29.99, 65, 'https://placehold.co/400x300?text=Patterns', 3, NOW(), NOW(), 0),
('System Design', 'An insider guide to building large-scale systems', 49.99, 50, 'https://placehold.co/400x300?text=System+Design', 3, NOW(), NOW(), 0),

-- Home & Kitchen (5)
('Coffee Maker', 'Programmable drip coffee maker 12-cup', 59.99, 40, 'https://placehold.co/400x300?text=Coffee+Maker', 4, NOW(), NOW(), 0),
('Blender Pro', 'High-speed blender for smoothies and soups', 69.99, 35, 'https://placehold.co/400x300?text=Blender', 4, NOW(), NOW(), 0),
('Non-Stick Pan Set', 'Set of 3 non-stick frying pans', 45.99, 55, 'https://placehold.co/400x300?text=Pan+Set', 4, NOW(), NOW(), 0),
('Air Fryer', 'Digital air fryer with 8 presets', 79.99, 30, 'https://placehold.co/400x300?text=Air+Fryer', 4, NOW(), NOW(), 0),
('Vacuum Cleaner', 'Bagless upright vacuum with HEPA filter', 119.99, 25, 'https://placehold.co/400x300?text=Vacuum', 4, NOW(), NOW(), 0),

-- Sports (5)
('Yoga Mat', 'Non-slip exercise yoga mat 6mm thick', 24.99, 110, 'https://placehold.co/400x300?text=Yoga+Mat', 5, NOW(), NOW(), 0),
('Dumbbells Set', 'Adjustable dumbbells set 5-25 lbs', 149.99, 30, 'https://placehold.co/400x300?text=Dumbbells', 5, NOW(), NOW(), 0),
('Resistance Bands', 'Set of 5 resistance bands with handles', 19.99, 200, 'https://placehold.co/400x300?text=Bands', 5, NOW(), NOW(), 0),
('Jump Rope', 'Speed jump rope with adjustable length', 12.99, 150, 'https://placehold.co/400x300?text=Jump+Rope', 5, NOW(), NOW(), 0),
('Water Bottle', 'Insulated stainless steel water bottle 32oz', 24.99, 180, 'https://placehold.co/400x300?text=Bottle', 5, NOW(), NOW(), 0),

-- Beauty (5)
('Face Moisturizer', 'Hydrating face moisturizer SPF 30', 24.99, 90, 'https://placehold.co/400x300?text=Moisturizer', 6, NOW(), NOW(), 0),
('Shampoo Set', 'Volumizing shampoo and conditioner set', 19.99, 120, 'https://placehold.co/400x300?text=Shampoo', 6, NOW(), NOW(), 0),
('Lipstick Collection', 'Set of 6 long-lasting matte lipsticks', 29.99, 70, 'https://placehold.co/400x300?text=Lipstick', 6, NOW(), NOW(), 0),
('Sunscreen Spray', 'Broad spectrum SPF 50 sunscreen spray', 14.99, 100, 'https://placehold.co/400x300?text=Sunscreen', 6, NOW(), NOW(), 0),
('Nail Polish Set', '8-piece gel nail polish kit', 22.99, 85, 'https://placehold.co/400x300?text=Nail+Polish', 6, NOW(), NOW(), 0),

-- Toys & Games (5)
('Board Game Set', 'Classic board game collection 10 games', 34.99, 45, 'https://placehold.co/400x300?text=Board+Game', 7, NOW(), NOW(), 0),
('Building Blocks', '500-piece building blocks set', 39.99, 60, 'https://placehold.co/400x300?text=Blocks', 7, NOW(), NOW(), 0),
('Remote Car', 'RC racing car with rechargeable battery', 44.99, 40, 'https://placehold.co/400x300?text=RC+Car', 7, NOW(), NOW(), 0),
('Puzzle 1000pc', '1000-piece landscape jigsaw puzzle', 18.99, 75, 'https://placehold.co/400x300?text=Puzzle', 7, NOW(), NOW(), 0),
('Card Game', 'Popular family card game', 14.99, 200, 'https://placehold.co/400x300?text=Card+Game', 7, NOW(), NOW(), 0),

-- Automotive (5)
('Car Phone Mount', 'Universal dashboard phone mount', 15.99, 150, 'https://placehold.co/400x300?text=Phone+Mount', 8, NOW(), NOW(), 0),
('USB Car Charger', 'Fast charge 3-port USB car charger', 19.99, 120, 'https://placehold.co/400x300?text=Car+Charger', 8, NOW(), NOW(), 0),
('Seat Cover Set', 'Leather seat covers for front and back', 59.99, 35, 'https://placehold.co/400x300?text=Seat+Covers', 8, NOW(), NOW(), 0),
('Car Vacuum', 'Portable handheld car vacuum cleaner', 34.99, 50, 'https://placehold.co/400x300?text=Car+Vacuum', 8, NOW(), NOW(), 0),
('Dash Cam', '1080p dashboard camera with night vision', 79.99, 40, 'https://placehold.co/400x300?text=Dash+Cam', 8, NOW(), NOW(), 0),

-- Garden (5)
('Garden Hose', '50ft expandable garden hose with nozzle', 29.99, 60, 'https://placehold.co/400x300?text=Hose', 9, NOW(), NOW(), 0),
('Plant Pot Set', 'Set of 5 ceramic plant pots', 34.99, 80, 'https://placehold.co/400x300?text=Plant+Pots', 9, NOW(), NOW(), 0),
('Pruning Shears', 'Professional garden pruning shears', 22.99, 70, 'https://placehold.co/400x300?text=Shears', 9, NOW(), NOW(), 0),
('Outdoor Lights', 'Solar powered outdoor string lights 20ft', 25.99, 100, 'https://placehold.co/400x300?text=Outdoor+Lights', 9, NOW(), NOW(), 0),
('Bird Feeder', 'Wooden bird feeder for backyard', 18.99, 55, 'https://placehold.co/400x300?text=Bird+Feeder', 9, NOW(), NOW(), 0),

-- Music (5)
('Acoustic Guitar', 'Full-size acoustic guitar with case', 149.99, 20, 'https://placehold.co/400x300?text=Guitar', 10, NOW(), NOW(), 0),
('Bluetooth Speaker', 'Portable waterproof Bluetooth speaker', 44.99, 80, 'https://placehold.co/400x300?text=Speaker', 10, NOW(), NOW(), 0),
('Headphones', 'Over-ear noise cancelling headphones', 69.99, 55, 'https://placehold.co/400x300?text=Headphones', 10, NOW(), NOW(), 0),
('Keyboard Piano', '61-key electric keyboard piano', 129.99, 15, 'https://placehold.co/400x300?text=Piano', 10, NOW(), NOW(), 0),
('Microphone Set', 'USB condenser microphone with stand', 54.99, 35, 'https://placehold.co/400x300?text=Microphone', 10, NOW(), NOW(), 0);

SET FOREIGN_KEY_CHECKS = 1;
