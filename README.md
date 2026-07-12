# ShopKart

Event-driven e-commerce microservices platform built with Java 21 and Spring Boot 3.

## Architecture

| Service | Responsibility | Database | Port |
|---|---|---|---|
| user-service | Auth, registration, JWT issuance | user_service_db (Postgres) | 8081 |
| product-service | Product catalog, search | product_service_db (Postgres) | 8082 |
| order-service | Order placement, orchestration | order_service_db (Postgres) | 8083 |
| inventory-service | Stock management, Kafka consumer | inventory_service_db (Postgres) | 8084 |

**Event flow:** Order Service publishes `OrderPlacedEvent` to Kafka → Inventory Service consumes it, updates stock, and publishes `StockUpdatedEvent` / `StockFailedEvent` → Order Service consumes the result and updates order status.

## Prerequisites

- Java 21 (`java -version`)
- Maven 3.9+
- Docker + Docker Compose

## Local Setup

1. Start infrastructure (Postgres, Kafka, Redis, Kafka UI):
   ```bash
   docker-compose up -d
   ```
2. Verify containers are healthy:
   ```bash
   docker-compose ps
   ```
3. Kafka UI available at [http://localhost:8085](http://localhost:8085)
4. Build all services:
   ```bash
   mvn clean install
   ```
5. Run a service (from its module folder):
   ```bash
   cd user-service
   mvn spring-boot:run
   ```

## Project Status

- [x] Day 1 — Project scaffold, Docker Compose infra, parent POM
- [ ] Day 2 — User Service (auth/JWT)
- [ ] Day 3 — Product Service (catalog)
- [ ] Day 4 — Order Service (REST flow, no Kafka yet)
- [ ] Day 5 — Kafka producer in Order Service
- [ ] Day 6 — Inventory Service (Kafka consumer)
- [ ] Day 7 — Close the loop + Resilience4j
- [ ] Day 8 — API Gateway (optional)
- [ ] Day 9 — Dockerize all services
- [ ] Day 10 — Polish, README, demo
