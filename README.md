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
- `/health`

Quick deployment route checklist:

```text
/
/login
/dashboard
/admin/dashboard
/customer/dashboard
/products
/cart
/checkout
/orders
/profile
/legacy-console
/health
```

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

## Deployment: Render + Neon

Target architecture:

```text
GitHub repository -> Render Docker Web Service -> Spring Boot app -> Neon PostgreSQL
```

This deployment path uses no paid service dependency. Render Free web services can sleep after inactivity, so the first request after a quiet period may take about one minute while the service wakes up.

### Create Neon PostgreSQL

1. Create a free Neon project.
2. Create or select a database.
3. Copy the host, database name, username, and password.
4. Convert the Neon connection string to a JDBC URL:

```text
jdbc:postgresql://YOUR_NEON_HOST/YOUR_DATABASE?sslmode=require
```

`sslmode=require` is important for Neon.

### Create Render Web Service

1. Push this repository to GitHub.
2. In Render, create a new Web Service.
3. Connect the GitHub repository.
4. Choose Docker as the runtime.
5. Keep the root-level `Dockerfile`.
6. Add the environment variables below.
7. Deploy.

Required Render environment variables:

```env
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://YOUR_NEON_HOST/YOUR_DATABASE?sslmode=require
SPRING_DATASOURCE_USERNAME=YOUR_NEON_USERNAME
SPRING_DATASOURCE_PASSWORD=YOUR_NEON_PASSWORD
JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75.0
```

Optional environment variables:

```env
APP_SEED_DEMO_DATA=true
APP_BASE_URL=https://YOUR-RENDER-SERVICE.onrender.com
```

Render provides `PORT` automatically. The production profile reads it with `${PORT:8080}`.

### Verify Deployment

1. Open the Render URL.
2. Visit `/health`; it should return `OK`.
3. Visit `/login`.
4. Login as admin: `admin@onlineshop.local` / `admin123`.
5. Confirm `/dashboard` redirects to `/admin/dashboard`.
6. Logout.
7. Login as customer: `customer@onlineshop.local` / `customer123`.
8. Confirm `/dashboard` redirects to `/customer/dashboard`.
9. Browse `/products`, add to cart, checkout, and track the order.
10. Open `/legacy-console`, run `help`, `clear`, `reset`, and menu-number commands.

### Docker Commands

Build locally:

```bash
docker build -t online-shop-java-platform .
```

Run locally against Neon:

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://YOUR_NEON_HOST/YOUR_DATABASE?sslmode=require" \
  -e SPRING_DATASOURCE_USERNAME="YOUR_NEON_USERNAME" \
  -e SPRING_DATASOURCE_PASSWORD="YOUR_NEON_PASSWORD" \
  online-shop-java-platform
```

Render uses the same Dockerfile.

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

## Deployment Troubleshooting

### Build fails with `release version 21 not supported`

Cause: Java/JDK mismatch, or a runtime-only Java install without a compatible compiler.

Fix:

- Use the provided Dockerfile, which builds with `maven:3.9.9-eclipse-temurin-21`.
- Locally, install a full JDK 21+ and confirm `javac -version` works.
- Run `mvn clean test` again after fixing the JDK.

### App crashes with database connection errors

Check:

- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- Neon JDBC URL includes `?sslmode=require`

No Neon credentials should be committed to Git.

### App starts but login fails

Check:

- Demo seeding is enabled with `APP_SEED_DEMO_DATA=true`.
- Demo credentials are exactly:
  - `admin@onlineshop.local` / `admin123`
  - `customer@onlineshop.local` / `customer123`
- The database has roles `ADMIN` and `CUSTOMER`.
- Password values in the database are BCrypt hashes, not plain text.

### `/dashboard` returns 403

Expected behavior:

- Unauthenticated `/dashboard` redirects to `/login`.
- Admin `/dashboard` redirects to `/admin/dashboard`.
- Customer `/dashboard` redirects to `/customer/dashboard`.

If this fails, check:

- The user has role value `ADMIN` or `CUSTOMER`.
- Spring Security maps roles to `ROLE_ADMIN` and `ROLE_CUSTOMER`.
- `SecurityConfig` keeps `/dashboard` as authenticated-only and role-routes in the controller.

### Static assets are not loading

Check:

- CSS is under `/css/**`.
- JavaScript is under `/js/**`.
- Templates use app-relative paths, not `localhost`.
- Render deployed the Docker image from the repository root.

### Original CLI Mode output grows too long

The terminal has fixed height and internal scrolling. Use:

- `clear` or `cls` to clear visible output.
- `reset` to restart the session.
- `help` for command guidance.

The browser terminal history is capped to avoid unbounded client memory growth.

### Render first load is slow

Render Free services can sleep after inactivity. A slow first load is normal for a no-cost portfolio deployment.

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
