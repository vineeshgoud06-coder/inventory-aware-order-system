# Inventory-Aware Order Processing System (StockFlow)

A full-stack demo for product catalogue, stock-aware checkout, server-side price calculation, simulated payment, and duplicate checkout protection. Built with Java 17, Spring Boot, Spring Data JPA, React, Vite, and H2 by default. PostgreSQL is included as an optional database.

## Features
- Product listing, product creation, and stock restocking.
- Checkout calculates totals using database prices; client-submitted prices are never accepted.
- Stock is locked with a pessimistic database write lock during checkout, and all reservation/order writes are transactional.
- Products are locked in ascending ID order to reduce deadlock risk. Duplicate product lines are merged.
- Idempotency key is unique in the database. Replaying the same request key returns the existing order instead of creating a new one.
- Simulated payment failure restores stock in the same transaction and records `PAYMENT_FAILED`.
- Bean validation, consistent JSON error responses, sample seed products, and a responsive React UI.

> Educational demo: no real payment gateway, authentication, user roles, tax, shipping, or production observability is included. Do not expose the demo API publicly without adding authentication and rate limiting.

## Requirements
- Java 17+
- Maven 3.8+
- Node.js 20+ and npm

## Run locally

### 1. Start backend
```bash
cd backend
mvn spring-boot:run
```
The API runs at `http://localhost:8080`. H2 file storage is used by default, with data in `backend/data/` (depending on the directory from which the app is launched). The H2 console is at `http://localhost:8080/h2-console`; JDBC URL `jdbc:h2:file:./data/orders`, user `sa`, blank password.

### 2. Start frontend in another terminal
```bash
cd frontend
npm install
npm run dev
```
Open the Vite URL printed in the terminal (usually `http://localhost:5173`). The UI expects the backend at `http://localhost:8080/api`. To use another API URL, copy `frontend/.env.example` to `frontend/.env` and set `VITE_API_URL`.

## Optional PostgreSQL
Start the database from the repository root:
```bash
docker compose up -d db
```
Then start the backend with environment variables (PowerShell example):
```powershell
$env:DATABASE_URL='jdbc:postgresql://localhost:5432/ordersdb'
$env:DATABASE_USERNAME='orders'
$env:DATABASE_PASSWORD='orders_local_password'
$env:DATABASE_DRIVER='org.postgresql.Driver'
cd backend
mvn spring-boot:run
```
For Bash, use `export` for each variable before `mvn spring-boot:run`.

## API reference
Base URL: `http://localhost:8080/api`

### Products
- `GET /products` — list products.
- `GET /products/{id}` — product detail.
- `POST /products` — create product. JSON: `{"name":"USB Cable","sku":"CAB-1","price":299.00,"stock":20}`
- `POST /products/{id}/restock?quantity=10` — add stock.

### Orders
- `POST /orders` — checkout. Example:
```json
{
  "customerName": "Alex",
  "idempotencyKey": "checkout-demo-001",
  "simulatePaymentFailure": false,
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3, "quantity": 1 }
  ]
}
```
- `GET /orders` — list orders.
- `GET /orders/{id}` — order detail.

A repeated POST with the same `idempotencyKey` returns the original order. In this demo, that key identifies the first request; clients should reuse it only for retries of that same logical checkout. Payment failure can be tested by setting `simulatePaymentFailure` to `true`; the response is a recorded failed order and stock is restored.

### Quick cURL examples
```bash
curl http://localhost:8080/api/products
curl -X POST http://localhost:8080/api/orders -H 'Content-Type: application/json' -d '{"customerName":"Alex","idempotencyKey":"try-001","simulatePaymentFailure":false,"items":[{"productId":1,"quantity":2}]}'
```

## Design notes
1. **Server-authoritative prices:** order totals are derived from product records in the database, not from browser input.
2. **Concurrency safety:** `PESSIMISTIC_WRITE` locks the relevant product rows while stock is checked and decremented. The transaction commits all changes together. Concurrent requests for the final unit serialize; the later request sees updated stock and is rejected.
3. **Atomic recovery:** simulated failure restores reserved stock before the transaction commits, while retaining a `PAYMENT_FAILED` order for demonstration.
4. **Idempotency:** a unique DB constraint protects against duplicate order creation. The service first checks the key; database uniqueness is the final guard. For high-throughput production systems, add a dedicated idempotency record and deterministic handling for simultaneous identical keys.
5. **Validation:** request DTOs validate required fields and quantities. API errors return an `error` field.

## Suggested manual test plan
1. `GET /api/products`; note the stock for product 1.
2. Buy 2 units; confirm total uses the server price and stock decreases by 2.
3. Submit the same logical request again with the same idempotency key; confirm the order count does not increase.
4. Request more than available stock; expect HTTP 400 and no stock change.
5. Submit with `simulatePaymentFailure: true`; expect `PAYMENT_FAILED` and unchanged stock.
6. Create two concurrent requests for the same last unit using different idempotency keys; only one should succeed.
7. Try duplicate SKU and invalid quantity; expect a validation/conflict error.

## Build and test
```bash
cd backend
mvn test
mvn package
cd ../frontend
npm install
npm run build
```

## Deployment notes
- Deploy backend as a Java 17 Spring Boot service and configure `PORT`, `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, and `DATABASE_DRIVER` for a managed PostgreSQL database. For PostgreSQL, `DATABASE_URL` must be a JDBC URL such as `jdbc:postgresql://HOST:5432/DBNAME`.
- Deploy `frontend` as a static Vite site and set `VITE_API_URL` to the public backend API URL plus `/api` (for example `https://your-api.example.com/api`) before building.
- Configure CORS to the exact frontend origin before production. Current permissive CORS is for local demo only.
- H2 file storage is convenient locally but ephemeral deployment filesystems may reset. Use PostgreSQL for deployment.

## Repository structure
```text
backend/  Spring Boot API, entities, repositories, services, controllers, tests
frontend/ React + Vite UI
README.md
 docker-compose.yml
```
