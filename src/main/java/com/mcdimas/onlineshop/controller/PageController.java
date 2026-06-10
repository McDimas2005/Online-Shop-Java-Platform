package com.mcdimas.onlineshop.controller;

import com.mcdimas.onlineshop.dto.ProductForm;
import com.mcdimas.onlineshop.entity.ProductCategory;
import com.mcdimas.onlineshop.entity.Role;
import com.mcdimas.onlineshop.service.AccountService;
import com.mcdimas.onlineshop.service.CartService;
import com.mcdimas.onlineshop.service.DashboardService;
import com.mcdimas.onlineshop.service.OrderService;
import com.mcdimas.onlineshop.service.OrderTrackingService;
import com.mcdimas.onlineshop.service.ProductService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {
    private final AccountService accountService;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;
    private final DashboardService dashboardService;
    private final OrderTrackingService trackingService;

    public PageController(AccountService accountService, ProductService productService, CartService cartService,
                          OrderService orderService, DashboardService dashboardService,
                          OrderTrackingService trackingService) {
        this.accountService = accountService;
        this.productService = productService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.dashboardService = dashboardService;
        this.trackingService = trackingService;
    }

    @GetMapping("/")
    public String landing() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String email,
                           @RequestParam String password, @RequestParam String address) {
        var registerRequest = new com.mcdimas.onlineshop.dto.RegisterRequest(name, email, password, address);
        accountService.registerCustomer(registerRequest);
        return "redirect:/login?registered";
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        var user = accountService.byEmail(principal.getName());
        if (user.getRole() == Role.ADMIN) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/customer/dashboard";
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard(Principal principal, Model model) {
        var user = accountService.byEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("orders", orderService.customerOrders(principal.getName()).stream().limit(5).toList());
        model.addAttribute("products", productService.search(null, null, null, null, true).stream().limit(4).toList());
        return "customer-dashboard";
    }

    @GetMapping("/products")
    public String products(@RequestParam(required = false) ProductCategory category,
                           @RequestParam(required = false) String q,
                           @RequestParam(required = false) String brand,
                           @RequestParam(required = false) String size,
                           @RequestParam(required = false) Boolean availableOnly,
                           Model model) {
        model.addAttribute("products", productService.search(category, q, brand, size, availableOnly));
        model.addAttribute("categories", ProductCategory.values());
        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.get(id));
        return "product-detail";
    }

    @GetMapping("/cart")
    public String cart(Principal principal, Model model) {
        model.addAttribute("cart", cartService.cartFor(principal.getName()));
        return "cart";
    }

    @GetMapping("/checkout/success")
    public String checkoutSuccess(@RequestParam Long orderId, Principal principal, Model model) {
        model.addAttribute("order", orderService.customerOrder(orderId, principal.getName()));
        return "checkout-success";
    }

    @GetMapping("/orders")
    public String orders(Principal principal, Model model) {
        model.addAttribute("orders", orderService.customerOrders(principal.getName()));
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Principal principal, Model model) {
        model.addAttribute("order", orderService.customerOrder(id, principal.getName()));
        model.addAttribute("events", trackingService.events(id));
        return "order-detail";
    }

    @GetMapping("/orders/{id}/tracking")
    public String tracking(@PathVariable Long id, Principal principal, Model model) {
        model.addAttribute("order", orderService.customerOrder(id, principal.getName()));
        model.addAttribute("events", trackingService.events(id));
        return "tracking";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        model.addAttribute("profile", accountService.customerProfile(principal.getName()));
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(Principal principal, @RequestParam String email, @RequestParam String address) {
        accountService.updateProfile(principal.getName(), email, address);
        return "redirect:/profile?saved";
    }

    @GetMapping("/admin")
    public String adminRoot() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String admin(Model model) {
        model.addAttribute("metrics", dashboardService.metrics());
        model.addAttribute("orders", orderService.allOrders().stream().limit(10).toList());
        return "admin-dashboard";
    }

    @GetMapping("/admin/products")
    public String adminProducts(Model model) {
        model.addAttribute("products", productService.search(null, null, null, null, null));
        model.addAttribute("productForm", new ProductForm());
        return "admin-products";
    }

    @GetMapping("/admin/products/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model) {
        var product = productService.get(id);
        ProductForm form = new ProductForm();
        form.setProductCode(product.getProductCode());
        form.setProductName(product.getProductName());
        form.setPrice(product.getPrice());
        form.setQuantityInStock(product.getQuantityInStock());
        if (product instanceof com.mcdimas.onlineshop.entity.ClothingProduct clothing) {
            form.setSize(clothing.getSize());
        }
        if (product instanceof com.mcdimas.onlineshop.entity.ElectronicsProduct electronics) {
            form.setBrand(electronics.getBrand());
            form.setWarrantyPeriodYears(electronics.getWarrantyPeriodYears());
        }
        model.addAttribute("product", product);
        model.addAttribute("productForm", form);
        return "admin-product-edit";
    }

    @GetMapping("/admin/stock")
    public String adminStock(Model model) {
        model.addAttribute("lowStock", productService.lowStock(10));
        model.addAttribute("outOfStock", productService.outOfStock());
        return "admin-stock";
    }

    @GetMapping("/admin/orders")
    public String adminOrders(Model model) {
        model.addAttribute("orders", orderService.allOrders());
        return "admin-orders";
    }

    @GetMapping("/legacy-console")
    public String legacyConsole() {
        return "legacy-console";
    }
}
