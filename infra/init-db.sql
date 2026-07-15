-- Runs automatically on first MySQL container startup
-- Creates one database per microservice so each owns its own schema

CREATE DATABASE IF NOT EXISTS user_service_db;
CREATE DATABASE IF NOT EXISTS product_service_db;
CREATE DATABASE IF NOT EXISTS order_service_db;
CREATE DATABASE IF NOT EXISTS inventory_service_db;

GRANT ALL PRIVILEGES ON user_service_db.* TO 'shopkart'@'%';
GRANT ALL PRIVILEGES ON product_service_db.* TO 'shopkart'@'%';
GRANT ALL PRIVILEGES ON order_service_db.* TO 'shopkart'@'%';
GRANT ALL PRIVILEGES ON inventory_service_db.* TO 'shopkart'@'%';

FLUSH PRIVILEGES;

