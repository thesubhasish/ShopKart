-- Runs automatically on first Postgres container startup
-- Creates one database per microservice so each owns its own schema

CREATE DATABASE user_service_db;
CREATE DATABASE product_service_db;
CREATE DATABASE order_service_db;
CREATE DATABASE inventory_service_db;

GRANT ALL PRIVILEGES ON DATABASE user_service_db TO shopkart;
GRANT ALL PRIVILEGES ON DATABASE product_service_db TO shopkart;
GRANT ALL PRIVILEGES ON DATABASE order_service_db TO shopkart;
GRANT ALL PRIVILEGES ON DATABASE inventory_service_db TO shopkart;
