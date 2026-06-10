package com.mcdimas.onlineshop.legacy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LegacyConsoleSession {
    private final String id;
    private final List<LegacyUser> users = new ArrayList<>();
    private final List<LegacyProduct> products = new ArrayList<>();
    private LegacyConsoleState state = LegacyConsoleState.LOGIN_MENU;
    private LegacyUser currentUser;
    private LegacyOrder currentTrackingOrder;
    private String signupName;
    private String signupEmail;
    private String pendingRestockId;
    private Instant lastTouched = Instant.now();

    public LegacyConsoleSession(String id) {
        this.id = id;
        seed();
    }

    public String id() {
        return id;
    }

    public LegacyConsoleState state() {
        return state;
    }

    public Instant lastTouched() {
        return lastTouched;
    }

    public LegacyCommandResult reset() {
        users.clear();
        products.clear();
        currentUser = null;
        currentTrackingOrder = null;
        signupName = null;
        signupEmail = null;
        pendingRestockId = null;
        state = LegacyConsoleState.LOGIN_MENU;
        seed();
        return new LegacyCommandResult(id, welcome(), state);
    }

    public LegacyCommandResult submit(String rawInput) {
        lastTouched = Instant.now();
        String input = rawInput == null ? "" : rawInput.trim();
        String output = switch (state) {
            case LOGIN_MENU -> handleLoginMenu(input);
            case LOGIN_ID -> handleLogin(input);
            case SIGNUP_NAME -> {
                signupName = input;
                state = LegacyConsoleState.SIGNUP_EMAIL;
                yield "Your Email: ";
            }
            case SIGNUP_EMAIL -> {
                signupEmail = input;
                state = LegacyConsoleState.SIGNUP_ADDRESS;
                yield "Your Address: ";
            }
            case SIGNUP_ADDRESS -> handleSignupAddress(input);
            case MAIN_MENU -> handleMainMenu(input);
            case USER_PRODUCTS_MENU -> handleUserProductsMenu(input);
            case ADMIN_PRODUCTS_MENU -> handleAdminProductsMenu(input);
            case CART_MENU -> handleCartMenu(input);
            case ORDERS_MENU -> handleOrdersMenu(input);
            case ADD_PRODUCT_ID -> handleAddProduct(input);
            case ADD_STOCK_KIND -> handleAddStockKind(input);
            case RESTOCK_ID -> {
                pendingRestockId = input;
                state = LegacyConsoleState.RESTOCK_QUANTITY;
                yield "Number of stock: ";
            }
            case RESTOCK_QUANTITY -> handleRestockQuantity(input);
            case CHECKOUT_CONFIRM -> handleCheckoutConfirm(input);
            case TRACK_ORDER_ID -> handleTrackOrderId(input);
            case TRACK_REFRESH -> handleTrackRefresh(input);
        };
        return new LegacyCommandResult(id, output, state);
    }

    public String welcome() {
        return title() + """
                1. Log In (If you already have an account)
                2. Sign Up (Create New Account)
                3. Exit
                Your Choice: """;
    }

    private String handleLoginMenu(String input) {
        return switch (input) {
            case "1" -> {
                state = LegacyConsoleState.LOGIN_ID;
                yield "LOGIN\n=====\nYour CustomerID: ";
            }
            case "2" -> {
                state = LegacyConsoleState.SIGNUP_NAME;
                yield "SIGN UP\n=======\nYour Name: ";
            }
            case "3", "exit" -> {
                currentUser = null;
                state = LegacyConsoleState.LOGIN_MENU;
                yield "Prototype session ended. Type any key to restart.\n\n" + welcome();
            }
            default -> invalid();
        };
    }

    private String handleLogin(String input) {
        Optional<LegacyUser> found = users.stream().filter(user -> user.id.equalsIgnoreCase(input)).findFirst();
        if (found.isEmpty()) {
            state = LegacyConsoleState.LOGIN_MENU;
            return "The ID doesn't exist !!\n\n" + welcome();
        }
        currentUser = found.get();
        state = LegacyConsoleState.MAIN_MENU;
        return "Login Success\n\n" + mainMenu();
    }

    private String handleSignupAddress(String address) {
        String idCode = "CU" + String.format("%03d", users.size() + 1);
        currentUser = new LegacyUser(idCode, signupName, signupEmail, address, 5);
        users.add(currentUser);
        state = LegacyConsoleState.MAIN_MENU;
        return "Successfully Created\nCustomer ID: " + idCode + "\n\n" + mainMenu();
    }

    private String handleMainMenu(String input) {
        return switch (input) {
            case "1" -> {
                state = currentUser.admin() ? LegacyConsoleState.ADMIN_PRODUCTS_MENU : LegacyConsoleState.USER_PRODUCTS_MENU;
                yield currentUser.admin() ? adminProductsMenu() : userProductsMenu();
            }
            case "2" -> {
                state = LegacyConsoleState.CART_MENU;
                yield cartMenu();
            }
            case "3" -> {
                state = LegacyConsoleState.ORDERS_MENU;
                yield ordersMenu();
            }
            case "4" -> customerInfo() + "\n\n" + mainMenu();
            case "5" -> {
                state = LegacyConsoleState.LOGIN_MENU;
                currentUser = null;
                yield welcome();
            }
            default -> invalid();
        };
    }

    private String handleUserProductsMenu(String input) {
        return switch (input) {
            case "1" -> productsTable(null) + "\n" + userProductsMenu();
            case "2" -> {
                state = LegacyConsoleState.ADD_PRODUCT_ID;
                yield productsTable("CLOTHING") + "\nEnter the clothing ID to add to your cart or type back: ";
            }
            case "3" -> {
                state = LegacyConsoleState.ADD_PRODUCT_ID;
                yield productsTable("ELECTRONICS") + "\nEnter the electronics ID to add to your cart or type back: ";
            }
            case "4", "back" -> {
                state = LegacyConsoleState.MAIN_MENU;
                yield mainMenu();
            }
            default -> invalid();
        };
    }

    private String handleAdminProductsMenu(String input) {
        return switch (input) {
            case "1" -> productsTable(null) + "\n" + adminProductsMenu();
            case "2", "3" -> {
                state = LegacyConsoleState.ADD_STOCK_KIND;
                yield "This safe simulator creates a demo stock item.\nType product ID to create or back: ";
            }
            case "4", "back" -> {
                state = LegacyConsoleState.MAIN_MENU;
                yield mainMenu();
            }
            case "5" -> {
                state = LegacyConsoleState.RESTOCK_ID;
                yield productsTable(null) + "\nEnter ID's Product to RESTOCK: ";
            }
            default -> invalid();
        };
    }

    private String handleCartMenu(String input) {
        return switch (input) {
            case "1" -> cartTable() + "\n" + cartMenu();
            case "2" -> {
                state = LegacyConsoleState.CHECKOUT_CONFIRM;
                yield "=== Checkout ===\nReview your order:\n" + cartTable() + "\nDo you want to proceed with the checkout? (Y/N): ";
            }
            case "3", "back" -> {
                state = LegacyConsoleState.MAIN_MENU;
                yield mainMenu();
            }
            default -> invalid();
        };
    }

    private String handleOrdersMenu(String input) {
        return switch (input) {
            case "1" -> orderHistory() + "\n" + ordersMenu();
            case "2" -> {
                state = LegacyConsoleState.TRACK_ORDER_ID;
                yield orderHistory() + "\nEnter the Order ID to track or type back: ";
            }
            case "3", "back" -> {
                state = LegacyConsoleState.MAIN_MENU;
                yield mainMenu();
            }
            default -> invalid();
        };
    }

    private String handleAddProduct(String input) {
        if ("back".equalsIgnoreCase(input)) {
            state = LegacyConsoleState.USER_PRODUCTS_MENU;
            return userProductsMenu();
        }
        Optional<LegacyProduct> product = products.stream().filter(p -> p.id.equalsIgnoreCase(input)).findFirst();
        if (product.isEmpty() || !product.get().available()) {
            return "The Product not found or OUT of STOCK!\nEnter another ID or back: ";
        }
        currentUser.cart.add(product.get());
        state = LegacyConsoleState.USER_PRODUCTS_MENU;
        return "The product added to your cart !!\n\n" + userProductsMenu();
    }

    private String handleAddStockKind(String input) {
        if ("back".equalsIgnoreCase(input)) {
            state = LegacyConsoleState.ADMIN_PRODUCTS_MENU;
            return adminProductsMenu();
        }
        products.add(new LegacyProduct(input, "Demo Product " + input, new BigDecimal("25.00"), 10, "CLOTHING", "M", null, 0));
        state = LegacyConsoleState.ADMIN_PRODUCTS_MENU;
        return "Product added to simulator stock.\n\n" + adminProductsMenu();
    }

    private String handleRestockQuantity(String input) {
        int quantity = parsePositiveInt(input);
        Optional<LegacyProduct> product = products.stream().filter(p -> p.id.equalsIgnoreCase(pendingRestockId)).findFirst();
        if (quantity <= 0 || product.isEmpty()) {
            state = LegacyConsoleState.ADMIN_PRODUCTS_MENU;
            return "Product is not found or quantity invalid.\n\n" + adminProductsMenu();
        }
        product.get().stock = quantity;
        state = LegacyConsoleState.ADMIN_PRODUCTS_MENU;
        return "The product is RESTOCKED....\n\n" + adminProductsMenu();
    }

    private String handleCheckoutConfirm(String input) {
        if ("N".equalsIgnoreCase(input)) {
            state = LegacyConsoleState.CART_MENU;
            return "Checkout Cancelled...\n\n" + cartMenu();
        }
        if (!"Y".equalsIgnoreCase(input)) {
            return "Input INVALID !!\nDo you want to proceed with the checkout? (Y/N): ";
        }
        if (currentUser.cart.isEmpty()) {
            state = LegacyConsoleState.CART_MENU;
            return "Your Cart is Empty\n\n" + cartMenu();
        }
        BigDecimal total = currentUser.cart.stream().map(p -> p.price).reduce(BigDecimal.ZERO, BigDecimal::add);
        LegacyOrder order = new LegacyOrder("OD" + currentUser.id + (currentUser.orders.size() + 1), currentUser.distance, total);
        order.items.addAll(currentUser.cart);
        currentUser.orders.add(order);
        currentUser.cart.clear();
        state = LegacyConsoleState.CART_MENU;
        return "Your OrderID: " + order.id + "\nYour Order is accepted and being processed\n\n" + cartMenu();
    }

    private String handleTrackOrderId(String input) {
        if ("back".equalsIgnoreCase(input)) {
            state = LegacyConsoleState.ORDERS_MENU;
            return ordersMenu();
        }
        Optional<LegacyOrder> order = currentUser.orders.stream().filter(o -> o.id.equalsIgnoreCase(input)).findFirst();
        if (order.isEmpty()) {
            return "The Order ID is not in the LIST!!\nEnter the Order ID to track or type back: ";
        }
        currentTrackingOrder = order.get();
        state = LegacyConsoleState.TRACK_REFRESH;
        return trackingOutput() + "\nType R to REFRESH the status or B to BACK: ";
    }

    private String handleTrackRefresh(String input) {
        if ("B".equalsIgnoreCase(input)) {
            state = LegacyConsoleState.ORDERS_MENU;
            return ordersMenu();
        }
        if ("R".equalsIgnoreCase(input)) {
            currentTrackingOrder.refresh();
            return trackingOutput() + "\nType R to REFRESH the status or B to BACK: ";
        }
        return "Input INVALID !!\nType R to REFRESH the status or B to BACK: ";
    }

    private String title() {
        return "=== Online Shopping Platform ===\n";
    }

    private String mainMenu() {
        return title() + """
                1. Shop for Products
                2. View Shopping Cart
                3. View Orders
                4. Customer Info
                5. Exit
                Your Choice: """;
    }

    private String userProductsMenu() {
        return title() + """
                1. View All Products
                2. Add Clothing to Cart
                3. Add Electronics to Cart
                4. Back to Main Menu
                Your Choice: """;
    }

    private String adminProductsMenu() {
        return title() + """
                1. View All Products
                2. Add Clothing to Stock
                3. Add Electronics to Stock
                4. Back to Main Menu
                5. RESTOCK
                Your Choice: """;
    }

    private String cartMenu() {
        return """
                === View Shopping Cart ===
                1. View Cart Contents
                2. Checkout
                3. Back to Main Menu
                Your Choice: """;
    }

    private String ordersMenu() {
        return """
                === View Orders ===
                1. View Order History
                2. Track Order
                3. Back to Main Menu
                Your Choice: """;
    }

    private String productsTable(String category) {
        StringBuilder table = new StringBuilder("==== View All Products ====\n");
        table.append(String.format("| %-6s | %-24s | %-10s | %-12s | %-10s |\n", "ID", "Product Name", "Price", "Type", "Available"));
        table.append("----------------------------------------------------------------------------\n");
        products.stream()
                .filter(product -> category == null || product.category.equals(category))
                .forEach(product -> table.append(String.format("| %-6s | %-24s | $%-9.2f | %-12s | %-10s |\n",
                        product.id, product.name, product.price, product.category, product.available() ? "In Stock" : "Out")));
        return table.toString();
    }

    private String cartTable() {
        StringBuilder table = new StringBuilder();
        table.append(String.format("%-8s | %-24s | %-10s\n", "ID", "Product Name", "Price"));
        table.append("-----------------------------------------------\n");
        if (currentUser.cart.isEmpty()) {
            table.append("Your Cart is Empty\n");
        } else {
            currentUser.cart.forEach(product -> table.append(String.format("%-8s | %-24s | $%-10.2f\n", product.id, product.name, product.price)));
        }
        BigDecimal total = currentUser.cart.stream().map(p -> p.price).reduce(BigDecimal.ZERO, BigDecimal::add);
        table.append("-----------------------------------------------\nTotal Price: $").append(total).append("\n");
        return table.toString();
    }

    private String orderHistory() {
        StringBuilder table = new StringBuilder("=== View Order History ===\n");
        table.append(String.format("%-14s | %-19s | %-10s | %-10s\n", "Order ID", "Order Date", "Total", "Status"));
        table.append("----------------------------------------------------------------\n");
        if (currentUser.orders.isEmpty()) {
            table.append("No Order History!!\n");
        }
        currentUser.orders.forEach(order -> table.append(String.format("%-14s | %-19s | $%-9.2f | %-10s\n",
                order.id, order.date, order.total, order.status)));
        return table.toString();
    }

    private String trackingOutput() {
        return "=== Order Tracking ===\n"
                + "Order ID: " + currentTrackingOrder.id + "\n"
                + "Order Status: " + currentTrackingOrder.status + "\n"
                + "Distance: " + currentTrackingOrder.distance + " KM\n"
                + "ProductList Quantity: " + currentTrackingOrder.items.size() + "\n"
                + "Status Detail: " + currentTrackingOrder.progress + " %";
    }

    private String customerInfo() {
        return "=== Customer Info ===\n"
                + "Customer ID: " + currentUser.id + "\n"
                + "Username : " + currentUser.name + "\n"
                + "Email : " + currentUser.email + "\n"
                + "Address : " + currentUser.address;
    }

    private String invalid() {
        return "Type a valid menu number please...!\n\n" + switch (state) {
            case LOGIN_MENU -> welcome();
            case MAIN_MENU -> mainMenu();
            case USER_PRODUCTS_MENU -> userProductsMenu();
            case ADMIN_PRODUCTS_MENU -> adminProductsMenu();
            case CART_MENU -> cartMenu();
            case ORDERS_MENU -> ordersMenu();
            default -> "Your Choice: ";
        };
    }

    private int parsePositiveInt(String input) {
        try {
            int value = Integer.parseInt(input);
            return value > 0 ? value : -1;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private void seed() {
        users.add(new LegacyUser("AD000", "Tsukishima Alan", "GreatGenshin@mihoyo.com", "Inazuma Peak No. 4, Teyvat", 4));
        users.add(new LegacyUser("CU001", "Jeanne Fortes", "loveVanitas@carte.com", "Somewhere Paris", 6));
        products.add(new LegacyProduct("P001", "T-Shirt - Blue", new BigDecimal("19.99"), 50, "CLOTHING", "M", null, 0));
        products.add(new LegacyProduct("P002", "Jeans - Slim Fit", new BigDecimal("39.99"), 20, "CLOTHING", "32/34", null, 0));
        products.add(new LegacyProduct("P005", "Sneakers - Sports", new BigDecimal("59.99"), 70, "CLOTHING", "9", null, 0));
        products.add(new LegacyProduct("P003", "Smartphone - Model X", new BigDecimal("499.99"), 80, "ELECTRONICS", null, "TechCo", 3));
        products.add(new LegacyProduct("P004", "Laptop - Ultrabook", new BigDecimal("899.99"), 20, "ELECTRONICS", null, "MegaElect", 5));
        products.add(new LegacyProduct("P006", "Smartwatch - Fitness", new BigDecimal("129.99"), 50, "ELECTRONICS", null, "TechCo", 2));
    }
}
