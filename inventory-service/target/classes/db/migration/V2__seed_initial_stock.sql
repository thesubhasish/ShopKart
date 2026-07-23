-- product_id values match the sample products seeded in product-service's V3 migration
INSERT INTO inventory_items (product_id, available_quantity) VALUES
    (1, 50),   -- Wireless Bluetooth Headphones
    (2, 30),   -- Smartphone 128GB
    (3, 100),  -- Men's Cotton T-Shirt
    (4, 40),   -- Women's Running Shoes
    (5, 25),   -- Non-Stick Frying Pan
    (6, 60),   -- Clean Code
    (7, 15);   -- Yoga Mat (kept low to make it easy to test the "insufficient stock" path)
