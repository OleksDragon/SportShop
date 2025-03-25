package com.example.sport_shop.service;

import com.example.sport_shop.model.*;
import com.example.sport_shop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    public Order createOrder(User user, String shippingAddress) {
        Cart cart = cartService.getOrCreateCart(user);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Кошик порожній");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    int quantity = cartItem.getQuantity();
                    productService.updateStock(product, quantity);
                    return new OrderItem(null, order, product, quantity, product.getPrice());
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);
        order.setTotalPrice(orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum());

        cartService.clearCart(cart);
        return orderRepository.save(order);
    }
}