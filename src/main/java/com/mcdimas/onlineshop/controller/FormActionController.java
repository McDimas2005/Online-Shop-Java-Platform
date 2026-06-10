package com.mcdimas.onlineshop.controller;

import com.mcdimas.onlineshop.dto.ProductForm;
import com.mcdimas.onlineshop.service.CartService;
import com.mcdimas.onlineshop.service.CheckoutService;
import com.mcdimas.onlineshop.service.ProductService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FormActionController {
    private final CartService cartService;
    private final CheckoutService checkoutService;
    private final ProductService productService;

    public FormActionController(CartService cartService, CheckoutService checkoutService, ProductService productService) {
        this.cartService = cartService;
        this.checkoutService = checkoutService;
        this.productService = productService;
    }

    @PostMapping("/cart/items")
    public String addToCart(Principal principal, @RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity) {
        cartService.addItem(principal.getName(), productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/items/{id}/update")
    public String updateCartItem(Principal principal, @PathVariable Long id, @RequestParam int quantity) {
        cartService.updateItem(principal.getName(), id, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/items/{id}/remove")
    public String removeCartItem(Principal principal, @PathVariable Long id) {
        cartService.removeItem(principal.getName(), id);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(Principal principal) {
        var order = checkoutService.checkout(principal.getName());
        return "redirect:/checkout/success?orderId=" + order.getId();
    }

    @PostMapping("/admin/products/clothing")
    public String addClothing(@Valid ProductForm form) {
        productService.addClothing(form);
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/electronics")
    public String addElectronics(@Valid ProductForm form) {
        productService.addElectronics(form);
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/{id}/restock")
    public String restock(@PathVariable Long id, @RequestParam int quantity) {
        productService.restock(id, quantity);
        return "redirect:/admin/stock";
    }

    @PostMapping("/admin/products/{id}/deactivate")
    public String deactivate(@PathVariable Long id) {
        productService.deactivate(id);
        return "redirect:/admin/products";
    }
}
