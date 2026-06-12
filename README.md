# Online Shop Java Platform

[![Java 21](https://img.shields.io/badge/Java-21-blue)](#tech-stack)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.x-brightgreen)](#tech-stack)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-336791)](#deployment-azure-container-apps--neon)
[![Docker](https://img.shields.io/badge/Docker-GHCR-2496ED)](#container-image)
[![Azure Container Apps](https://img.shields.io/badge/Azure-Container%20Apps-0078D4)](#deployment-azure-container-apps--neon)

Online Shop Java Platform is a professional modernization of a Java OOP console-based online shop prototype. It shows the evolution from a menu-driven Java assignment into a Spring Boot web application with persistence, authentication, role-based dashboards, transactional checkout, live order tracking, and a preserved Original CLI Mode.

## Live Demo

The project is currently deployed on Azure Container Apps:

[Open Online Shop Java Platform](https://online-shop-java-platform.proudflower-f2031a1c.southeastasia.azurecontainerapps.io)

Health check:

`/health` -> `OK`

> Note: The app uses Azure Container Apps with minimum replicas set to `0`, so the first request after inactivity may take longer while the container starts.

## Portfolio Positioning

This project is intentionally not a generic e-commerce clone. It is a Java/OOP modernization story:

- Original Java OOP console prototype.
- Refactored Spring Boot web architecture.
- PostgreSQL persistence.
- Role-based admin/customer dashboards.
- Transactional checkout.
- Async/live order tracking.
- Safe browser-based Original CLI Mode that preserves the old console experience.
- Practical no-credit-card student deployment using Azure for Students, GHCR, and Neon.

## Current Deployment Status

Current live architecture:

```text
GitHub / GHCR public image -> Azure Container Apps -> Spring Boot container -> Neon PostgreSQL
```

Current live URL:

```text
https://online-shop-java-platform.proudflower-f2031a1c.southeastasia.azurecontainerapps.io
```

Repository:

```text
https://github.com/McDimas2005/Online-Shop-Java-Platform
```

Container image:

```text
ghcr.io/mcdimas2005/online-shop-java-platform:latest
```

Render Web Service with Docker was initially planned, but Render required a credit card even for the Free instance, so it was abandoned. Heroku through the GitHub Student Developer Pack was also considered, but it required a credit card. Azure for Students was selected because it supports a no-credit-card student deployment path with student credit.

Neon PostgreSQL remains the database to avoid extra Azure database cost and keep the deployment lightweight.

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
- Idempotent demo seed data migrated from the original prototype.
- Dockerized local and cloud deployment.
- JUnit 5/Spring Boot tests for core flows.

## Tech Stack

- Java 21
- Spring Boot 3.3.x
- Spring Web MVC
- Thymeleaf
- Spring Data JPA / Hibernate
- PostgreSQL / Neon PostgreSQL
- Spring Security
- Spring Validation
- Server-Sent Events
- JUnit 5, AssertJ, Spring Boot Test, Spring Security Test
- Docker
- GitHub Container Registry
- Azure Container Apps

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

The original project demonstrated inheritance, abstraction, polymorphism, encapsulation, and multithreading. The web version preserves that educational value while using production-style Spring patterns:

- `User` becomes `UserAccount` plus `CustomerProfile`.
- Abstract `Product` remains the base for `ClothingProduct` and `ElectronicsProduct`.
- Cart and checkout behavior move from in-memory lists into services and PostgreSQL-backed entities.
- The old `Order extends Thread` tracking idea becomes a Spring-managed asynchronous workflow.
- Admin authorization no longer depends on the old `AD` user ID prefix; it uses Spring Security roles.
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

Terminal commands:

- `help`: show available commands and menu hints.
- `clear` or `cls`: clear visible terminal output.
- `reset`: restart the simulated session.

Security notes:

- It never executes operating system commands.
- It never exposes server paths, environment variables, credentials, or shell output.
- Each browser session uses isolated in-memory simulator state.
- Inputs are interpreted only as shop menu choices or prototype values.
- Terminal output is fixed-height, scrolls internally, and is bounded to avoid unbounded browser memory growth.

## Main Routes

Live verification routes:

```text
/health
/
/login
/products
/dashboard
/admin/dashboard
/customer/dashboard
/legacy-console
```

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

## Demo Accounts

Demo accounts are seeded by the app when `APP_SEED_DEMO_DATA=true`.

```text
Admin:
admin@onlineshop.local / admin123

Customer:
customer@onlineshop.local / customer123
```

> These are public demo credentials for portfolio testing only. Do not use real personal data in this deployment.

Passwords are stored as BCrypt hashes.

The seeded accounts preserve the original prototype identity:

- `AD000` Tsukishima Alan is the admin demo identity.
- `CU001` Jeanne Fortes is the customer demo identity.

## Local Setup

Prerequisites:

- Java 21
- Maven 3.9+
- Docker Desktop with WSL integration if using Docker Compose

Copy environment defaults:

```bash
cp .env.example .env
```

For local Docker Compose development, edit `.env` and use local values like:

```env
DB_NAME=online_shop
DB_USERNAME=online_shop
DB_PASSWORD=online_shop
DB_URL=jdbc:postgresql://postgres:5432/online_shop
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
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

## Docker Setup

Run the full local stack:

```bash
docker compose up --build
```

The app will be available at:

```text
http://localhost:8080
```

Build the deployment image locally:

```bash
docker build -t online-shop-java-platform .
```

## Container Image

The current deployment image is hosted publicly on GitHub Container Registry:

```text
ghcr.io/mcdimas2005/online-shop-java-platform:latest
```

Azure Container Apps pulls this public GHCR image.

Manual build and push:

```bash
docker build -t ghcr.io/mcdimas2005/online-shop-java-platform:latest .
docker push ghcr.io/mcdimas2005/online-shop-java-platform:latest
```

You must be logged in to GHCR before pushing.

## Deployment: Azure Container Apps + Neon

Primary deployment architecture:

```text
GitHub / GHCR public image -> Azure Container Apps -> Spring Boot container -> Neon PostgreSQL
```

Azure Container Apps was used instead of Azure App Service, Azure VM, AKS, Azure Container Registry, or Azure PostgreSQL to keep the deployment lightweight and free-friendly.

Neon PostgreSQL is used instead of Azure PostgreSQL to avoid extra Azure database cost and keep the architecture simple. Authentication is handled by the Java/Spring Boot app, not Neon Auth.

### Create Neon PostgreSQL

1. Create a Neon project.
2. Create or select a database.
3. Copy the host, database name, username, and password.
4. Convert the Neon connection string to a JDBC URL:

```text
jdbc:postgresql://<NEON_HOST>/<DATABASE>?sslmode=require
```

`sslmode=require` is important for Neon.

### Build and Publish the Container Image

1. Build the Docker image.
2. Tag it as `ghcr.io/mcdimas2005/online-shop-java-platform:latest`.
3. Push it to GitHub Container Registry.
4. Make sure the GHCR image is public so Azure Container Apps can pull it without registry credentials.

### Create Azure Container App

1. Use the `Azure for Students` subscription.
2. Create or select resource group `rg-online-shop-free`.
3. Create a Container App named `online-shop-java-platform`.
4. Select region `Southeast Asia`.
5. Use workload profile `Consumption`.
6. Select image source as a public container registry.
7. Registry: `ghcr.io`.
8. Image: `ghcr.io/mcdimas2005/online-shop-java-platform:latest`.
9. Enable ingress.
10. Enable external HTTP traffic.
11. Set target port to `8080`.
12. Set CPU to `0.25`.
13. Set memory to `0.5 Gi`.
14. Set minimum replicas to `0`.
15. Set maximum replicas to `1`.
16. Add the production environment variables.
17. Deploy.

### Production Environment Variables

Configure these in Azure Container Apps environment variables/secrets. Do not commit real Neon credentials.

```env
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://<NEON_HOST>/<DATABASE>?sslmode=require
SPRING_DATASOURCE_USERNAME=<NEON_USERNAME>
SPRING_DATASOURCE_PASSWORD=<NEON_PASSWORD>
JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75.0
APP_SEED_DEMO_DATA=true
PORT=8080
APP_BASE_URL=https://online-shop-java-platform.proudflower-f2031a1c.southeastasia.azurecontainerapps.io
```

`SPRING_DATASOURCE_PASSWORD` should be stored as a secret or protected environment value in Azure Container Apps.

### Azure Deployment Configuration

Current deployed configuration:

```text
Subscription: Azure for Students
Resource group: rg-online-shop-free
Container App name: online-shop-java-platform
Region: Southeast Asia
Workload profile: Consumption
CPU: 0.25
Memory: 0.5 Gi
Ingress: enabled
External HTTP traffic: enabled
Target port: 8080
Min replicas: 0
Max replicas: 1
Registry: ghcr.io
Image: ghcr.io/mcdimas2005/online-shop-java-platform:latest
Database: Neon PostgreSQL
Authentication: Spring Security inside the Java app
```

### Verify Azure Deployment

1. Open the live URL.
2. Visit `/health`; it should return `OK`.
3. Visit `/login`.
4. Login as admin: `admin@onlineshop.local` / `admin123`.
5. Confirm `/dashboard` redirects to `/admin/dashboard`.
6. Logout.
7. Login as customer: `customer@onlineshop.local` / `customer123`.
8. Confirm `/dashboard` redirects to `/customer/dashboard`.
9. Browse `/products`, add to cart, checkout, and track the order.
10. Open `/legacy-console`, run `help`, `clear`, `cls`, `reset`, and menu-number commands.

### Alternative Deployment Paths Considered But Not Used

Render Web Service with Docker was originally planned, but it required a credit card even on the Free instance. Heroku through the GitHub Student Developer Pack was also considered, but it required a credit card. Both were abandoned for this no-credit-card student deployment.

## Cost Safety / Free-Friendly Deployment Notes

This deployment is designed to stay lightweight and student-friendly. It is not a guarantee of permanent zero cost; usage and cloud billing should still be monitored.

- Azure Container Apps Consumption plan is used.
- Min replicas are set to `0`.
- Max replicas are set to `1`.
- CPU is limited to `0.25`.
- Memory is limited to `0.5 Gi`.
- An Azure budget was created for resource group `rg-online-shop-free`.
- Monthly budget amount: `$1`.
- Neon PostgreSQL is used instead of Azure PostgreSQL to reduce Azure resource cost.
- Log Analytics was created automatically by Azure during deployment; monitor costs and reduce or disable stored logs if possible.

> Note: The `$1` Azure budget is used for monitoring and alerts. It should not be treated as a guaranteed hard spending cap. Azure budgets can notify when spending reaches a threshold, but they do not automatically stop resources unless additional automation is configured. Cost Management should still be checked regularly.

Avoid adding these unless truly needed:

- Azure PostgreSQL
- Azure Container Registry
- Virtual Machines
- AKS
- Always-on replicas
- Paid custom domains
- Paid add-ons
- Private endpoints
- Extra Azure services

## Custom Domain Status

The Azure-generated URL is currently used.

Custom domains were explored. Name.com, `.TECH`, and GitHub Student Developer Pack domain options were considered, but they required a credit card. No custom domain was purchased.

This is acceptable because the app can be embedded or linked inside a prototype or portfolio. A possible future clean-link option is a GitHub Pages redirect or landing page.

## Verification Checklist

Routes:

```text
/health
/
/login
/products
/dashboard
/admin/dashboard
/customer/dashboard
/legacy-console
```

Admin flow:

```text
Login admin -> dashboard -> product management -> stock -> orders
```

Customer flow:

```text
Login customer -> products -> cart -> checkout -> orders -> tracking
```

CLI flow:

```text
Open /legacy-console -> help -> menu numbers -> clear/cls -> reset
```

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
- role-based dashboard redirects
- seed data idempotency
- CLI simulation login, invalid input, clear, help, and reset behavior

## Troubleshooting

### `/health` fails

Check:

- Container App revision logs.
- Ingress is enabled.
- External HTTP traffic is enabled.
- Target port is `8080`.
- Required environment variables are present.
- The latest GHCR image was deployed.

### App starts but database connection fails

Check:

- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- Neon JDBC URL includes `?sslmode=require`

No Neon credentials should be committed to Git.

### Login fails

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
- The user is logging in with the correct demo account.
- `SecurityConfig` keeps `/dashboard` as authenticated-only and role-routes in the controller.

### Container does not wake quickly

Min replicas are set to `0`. The first request after inactivity may take longer while Azure starts the container.

### Cost concerns

Check:

- Azure Cost Management.
- Budget alerts for `rg-online-shop-free`.
- Container App max replicas remain `1`.
- Container App min replicas remain `0`.
- Log Analytics ingestion and retention.

Avoid Azure PostgreSQL, Azure Container Registry, AKS, VMs, paid domains, and always-on replicas unless the project requirements change.

### GHCR image does not pull

Check:

- The image is public.
- Image name is exactly `ghcr.io/mcdimas2005/online-shop-java-platform:latest`.
- The Container App revision points to the latest pushed image.
- The image was built for Linux and contains the Spring Boot jar.

### Static assets are not loading

Check:

- CSS is under `/css/**`.
- JavaScript is under `/js/**`.
- Templates use app-relative paths, not `localhost`.
- The deployed image was built from the repository root.

### Original CLI Mode issues

Open `/legacy-console` and use:

- `help` for command guidance.
- `clear` or `cls` to clear visible output.
- `reset` to restart the simulated session.

The terminal has fixed height, internal scrolling, and capped browser history.

### Build fails with `release version 21 not supported`

Cause: Java/JDK mismatch, or a runtime-only Java install without a compatible compiler.

Fix:

- Use the provided Dockerfile, which builds with `maven:3.9.9-eclipse-temurin-21`.
- Locally, install a full JDK 21+ and confirm `javac -version` works.
- Run `mvn test` again after fixing the JDK.

## Screenshots

Add screenshots here after running locally or from the live deployment:

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
- Add GitHub Actions workflow for automatic GHCR image publishing.
- Add a GitHub Pages portfolio redirect or landing page for a cleaner public link.

## Source Notes

The original prototype source was provided as the project foundation in the prompt and documented under `docs/original-java-prototype/`. The implementation keeps that identity visible while replacing fragile console and thread patterns with Spring Boot web architecture.
