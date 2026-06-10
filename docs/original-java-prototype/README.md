# Original Java OOP Console Prototype

This folder documents the source identity of the project before modernization.

The original project was an individual Java OOP console case study with these classes:

- `Main.java` initialized demo users and products, then ran the console menu loop.
- `ShoppingSystem.java` owned login/signup, product menus, admin stock management, cart, checkout, order history, and tracking screens.
- `User.java` represented the base user abstraction.
- `Customer.java` extended `User` and stored address, cart, order list, total cart price, and simulated delivery distance.
- `Product.java` was the abstract product parent with product ID, name, price, stock, and availability behavior.
- `Clothing.java` extended `Product` with size-specific behavior.
- `Electronics.java` extended `Product` with brand and warranty behavior.
- `Order.java` extended `Thread` and simulated accepted, packing, shipping, and delivered states.
- `Orderable.java` and `MultiToolable.java` provided order and console utility behavior.

The modern application keeps these concepts but moves production behavior into Spring services, JPA entities, DTOs, controllers, and a safe web-based `Original CLI Mode`.

Important modernization choices:

- `Order extends Thread` is not used in production. The same asynchronous idea is represented by `OrderTrackingService` and Spring `@Async`.
- Cart additions no longer permanently reduce stock. Stock is deducted transactionally during checkout.
- Admin identity no longer depends on an `AD` string prefix. Spring Security roles now enforce authorization.
- The legacy console is not a shell. It is a controlled Java state machine under `com.mcdimas.onlineshop.legacy`.

Original seed data carried forward:

- Admin demo user: `AD000`, Tsukishima Alan, `GreatGenshin@mihoyo.com`.
- Customer demo user: `CU001`, Jeanne Fortes, `loveVanitas@carte.com`.
- Clothing: `P001`, `P002`, `P005`.
- Electronics: `P003`, `P004`, `P006`.
