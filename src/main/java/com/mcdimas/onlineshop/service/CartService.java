package com.mcdimas.onlineshop.service;

import com.mcdimas.onlineshop.entity.Cart;
import com.mcdimas.onlineshop.entity.CartItem;
import com.mcdimas.onlineshop.entity.CustomerProfile;
import com.mcdimas.onlineshop.entity.Product;
import com.mcdimas.onlineshop.exception.AppException;
import com.mcdimas.onlineshop.exception.NotFoundException;
import com.mcdimas.onlineshop.repository.CartItemRepository;
import com.mcdimas.onlineshop.repository.CartRepository;
import com.mcdimas.onlineshop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AccountService accountService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       ProductRepository productRepository, AccountService accountService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public Cart cartFor(String email) {
        return cartRepository.findByCustomerUserEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("Cart not found."));
    }

    @Transactional
    public Cart addItem(String email, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new AppException("Cart quantity must be positive.");
        }
        CustomerProfile profile = accountService.customerProfile(email);
        Cart cart = profile.getCart();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found."));
        if (!product.isAvailable()) {
            throw new AppException("Product is unavailable.");
        }
        int existingQuantity = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .mapToInt(CartItem::getQuantity)
                .sum();
        if (existingQuantity + quantity > product.getQuantityInStock()) {
            throw new AppException("Not enough stock available.");
        }
        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresentOrElse(item -> item.setQuantity(item.getQuantity() + quantity),
                        () -> cart.getItems().add(new CartItem(cart, product, quantity)));
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateItem(String email, Long itemId, int quantity) {
        if (quantity <= 0) {
            throw new AppException("Cart quantity must be positive.");
        }
        Cart cart = cartFor(email);
        CartItem item = ownedItem(cart, itemId);
        if (quantity > item.getProduct().getQuantityInStock()) {
            throw new AppException("Not enough stock available.");
        }
        item.setQuantity(quantity);
        return cart;
    }

    @Transactional
    public Cart removeItem(String email, Long itemId) {
        Cart cart = cartFor(email);
        CartItem item = ownedItem(cart, itemId);
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return cart;
    }

    @Transactional
    public Cart clear(String email) {
        Cart cart = cartFor(email);
        cart.clear();
        return cart;
    }

    private CartItem ownedItem(Cart cart, Long itemId) {
        return cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Cart item not found."));
    }
}
