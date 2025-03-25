package com.example.sport_shop.service;

import com.example.sport_shop.model.Cart;
import com.example.sport_shop.model.CartItem;
import com.example.sport_shop.model.Product;
import com.example.sport_shop.model.User;
import com.example.sport_shop.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    public void addToCart(Cart cart, Product product, int quantity) {
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(new CartItem(null, cart, product, 0));

        item.setQuantity(item.getQuantity() + quantity);
        if (item.getId() == null) {
            cart.getItems().add(item);
        }
        cartRepository.save(cart);
    }

    public void clearCart(Cart cart) {
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}