# Online Shop Java Platform

Online Shop Java Platform is a professional modernization of a Java OOP console-based online shop prototype. It shows the evolution from a menu-driven Java assignment into a Spring Boot web application with persistence, authentication, role-based dashboards, transactional checkout, live order tracking, and a preserved Original CLI Mode.

## Portfolio Positioning

This project is intentionally not a generic e-commerce clone. It keeps the original Java OOP foundation visible:

- `User` becomes `UserAccount` plus `CustomerProfile`.
- Abstract `Product` remains the base for `ClothingProduct` and `ElectronicsProduct`.
- Cart and checkout behavior move from in-memory lists into services and PostgreSQL-backed entities.
- The old `Order extends Thread` tracking idea becomes a Spring-managed asynchronous workflow.
- The original console experience is preserved through a safe browser-based simulator.

## Features

- Customer registration and login.
- Admin login with role-based access.
- Product catalog with category, availability, brand, and size filtering.
- Admin product management for clothing and electronics.
- Low-stock and out-of-stock admin views.
- Cart add, update, remove, clear, and totals.
- Transactional checkout with exact stock deduction.
- Order history and order detail pages.
- Order item snapshots so historical orders remain accurate after product edits.
- Live order tracking using Server-Sent Events.
- Original CLI Mode with terminal-style menu simulation.
- Seed data migrated from the original prototype.
- Docker Compose for PostgreSQL and the app.
- JUnit 5/Spring Boot tests for core flows.

## Tech Stack

- Java 21
- Spring Boot 3.3.x
- Spring Web MVC
- Thymeleaf
- Spring Data JPA / Hibernate
- PostgreSQL
- Spring Security
- Spring Validation
- Server-Sent Events
- JUnit 5, AssertJ, Spring Boot Test, Spring Security Test
- Docker Compose

## Architecture

The application uses a layered Spring structure:

- `entity`: JPA model and enums.
- `repository`: Spring Data repositories.
- `service`: business logic, transactions, checkout, tracking, account handling.
- `controller`: Thymeleaf pages, JSON APIs, SSE endpoints.
- `dto` and `mapper`: request/response shapes.
- `security`: Spring Security user loading.
- `seed`: idempotent demo data.
- `legacy`: safe Original CLI Mode simulator.
- `sse`: shared SSE emitter hub.

## Domain Model

Core entities:

- `UserAccount`
- `CustomerProfile`
- `Product`
- `ClothingProduct`
- `ElectronicsProduct`
- `Cart`
- `CartItem`
- `CustomerOrder`
- `OrderItem`
- `OrderTrackingEvent`

Enums:

- `Role`: `ADMIN`, `CUSTOMER`
- `ProductCategory`: `CLOTHING`, `ELECTRONICS`
- `OrderStatus`: `ACCEPTED`, `PACKING`, `SHIPPING`, `DELIVERED`, `CANCELLED`

`OrderItem` stores product snapshots: product code, product name, category, unit price, quantity, and subtotal.

## OOP Preservation

The original project demonstrated inheritance, abstraction, polymorphism, encapsulation, and multithreading. The web version preserves that educational value while using production-appropriate Spring patterns:

- Product inheritance remains explicit.
- User/customer concepts remain separate.
- Business rules are encapsulated in services and domain methods.
- Asynchronous tracking is handled by Spring `@Async`, not by directly extending `Thread`.
- The CLI simulator shows the old menu flow without exposing a server shell.

## Original CLI Mode

Open `/legacy-console` to use the preserved prototype interface.

It supports:

- login and signup
- main menu
- product browsing
- add clothing/electronics to cart
- view cart
- checkout
- order history
- order tracking refresh
- customer info
- admin product menu
- add demo stock
- restock
- reset session

Security notes:

- It never executes operating system commands.
- It never exposes server paths, environment variables, credentials, or shell output.
- Each browser session uses isolated in-memory simulator state.
- Inputs are interpreted only as shop menu choices or prototype values.

## Main Routes

Pages:

- `/`
- `/login`
- `/register`
- `/dashboard` role router
- `/customer/dashboard`
- `/products`
- `/products/{id}`
- `/cart`
- `/checkout/success`
- `/orders`
- `/orders/{id}`
- `/orders/{id}/tracking`
- `/profile`
- `/admin` redirects to `/admin/dashboard`
- `/admin/dashboard`
- `/admin/products`
- `/admin/stock`
- `/admin/orders`
- `/legacy-console`

APIs:

- `GET /api/auth/me`
- `GET /api/products`
- `GET /api/products/{id}`
- `GET /api/cart`
- `POST /api/cart/items`
- `PATCH /api/cart/items/{id}`
- `DELETE /api/cart/items/{id}`
- `POST /api/checkout`
- `GET /api/orders`
- `GET /api/orders/{id}`
- `GET /api/orders/{id}/tracking`
- `GET /api/admin/orders`
- `POST /api/admin/products/clothing`
- `POST /api/admin/products/electronics`
- `PUT /api/admin/products/{id}`
- `PATCH /api/admin/products/{id}/restock`
- `PATCH /api/admin/products/{id}/deactivate`
- `POST /api/legacy-console/session`
- `POST /api/legacy-console/session/{sessionId}/input`
- `GET /api/legacy-console/session/{sessionId}`
- `POST /api/legacy-console/session/{sessionId}/reset`
- `GET /sse/orders/{orderId}`
- `GET /sse/legacy-console/{sessionId}`

## Local Setup

Prerequisites:

- Java 21
- Maven 3.9+
- Docker Desktop with WSL integration if using Docker Compose

Copy environment defaults:

```bash
cp .env.example .env
```

Run PostgreSQL:

```bash
docker compose up postgres
```

Run the application:

```bash
mvn spring-boot:run
```

Then open:

```text
http://localhost:8080
```

## Docker Compose

Run the full stack:

```bash
docker compose up --build
```

The app will be available at `http://localhost:8080`.

## Demo Accounts

For local development only:

- Admin: `admin@onlineshop.local` / `admin123`
- Customer: `customer@onlineshop.local` / `customer123`

The seeded accounts preserve the original prototype identity:

- `AD000` Tsukishima Alan is the admin demo account.
- `CU001` Jeanne Fortes is the customer demo account.

Passwords are hashed with BCrypt in the database.

## Testing

Run:

```bash
mvn test
```

The test profile uses H2 in PostgreSQL compatibility mode.

Covered scenarios include:

- product creation and availability
- cart add, update, remove, and total calculation
- checkout success
- empty cart failure
- insufficient stock failure
- transactional stock deduction
- order item snapshots
- order tracking events
- customer/admin page access
- CLI simulation login, invalid input, and reset

## Working Flow Checklist

Customer:

- Open `/`, register, log in, and land on `/customer/dashboard`.
- Browse `/products`, open a product detail page, and add an item to the cart.
- Update and remove cart items from `/cart`.
- Checkout from `/cart`; stock is deducted and the cart clears.
- View `/orders`, open an order detail page, and track it live.
- Update contact details from `/profile`.
- Logout from the customer dashboard.

Admin:

- Log in with the admin demo account and land on `/admin/dashboard`.
- Open `/admin/products`, add clothing/electronics, edit products, and deactivate products.
- Open `/admin/stock`, view low/out-of-stock products, and restock them.
- Open `/admin/orders` to monitor order status.
- Logout from the admin dashboard.

Original CLI Mode:

- Open `/legacy-console`.
- Use menu numbers for the original Java console flow.
- Use `help` to show terminal commands.
- Use `clear` or `cls` to clear visible output.
- Use `reset` or the Reset button to restart the simulated session.

## Troubleshooting 403 Forbidden

A 403 usually means the user is authenticated but does not have the role required by the route, or the route matcher is wrong.

Current expected behavior:

- Unauthenticated `/dashboard` redirects to `/login`.
- Admin `/dashboard` redirects to `/admin/dashboard`.
- Customer `/dashboard` redirects to `/customer/dashboard`.
- Admin-only pages require `ROLE_ADMIN`.
- Customer-only pages require `ROLE_CUSTOMER`.

Correct local demo credentials:

- Admin: `admin@onlineshop.local` / `admin123`
- Customer: `customer@onlineshop.local` / `customer123`

If old seed data is still present, restart the app once. The seeder repairs the demo users by `AD000` and `CU001`. If the database has conflicting manually-created demo emails, reset the local database volume:

```bash
docker compose down -v
docker compose up -d postgres
mvn spring-boot:run
```

## Screenshots

Add screenshots here after running locally:

- Landing page
- Product catalog
- Customer cart
- Live tracking page
- Admin dashboard
- Original CLI Mode

## Future Improvements

- Add Flyway migrations for stricter production database lifecycle.
- Add Testcontainers for PostgreSQL-backed integration tests.
- Add product images managed through admin forms.
- Add richer order cancellation rules.
- Add audit logs for admin stock changes.

## Source Notes

The original prototype source was provided as the project foundation in the prompt and documented under `docs/original-java-prototype/`. The implementation keeps that identity visible while replacing fragile console and thread patterns with Spring Boot web architecture.
