package com.example.sport_shop.controller;

import com.example.sport_shop.model.*;
import com.example.sport_shop.service.OrderService;
import com.example.sport_shop.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @GetMapping("/order")
    public String showOrderForm(Model model) {
        return "order";
    }

    @PostMapping("/order")
    public String createOrder(
            @AuthenticationPrincipal User user,
            @RequestParam("shippingAddress") String shippingAddress,
            @RequestParam Map<String, String> allParams,
            Model model) {
        if (user == null) {
            logger.warn("Спроба оформлення замовлення без авторизації");
            return "redirect:/login";
        }

        try {
            logger.info("Оформлення замовлення для користувача: {}", user.getUsername());
            logger.info("Отримані параметри: {}", allParams);

            List<CartItemDTO> cartItems = new ArrayList<>();
            for (String key : allParams.keySet()) {
                if (key.startsWith("quantity_")) {
                    Long productId = Long.parseLong(key.replace("quantity_", ""));
                    int quantity = Integer.parseInt(allParams.get(key));
                    logger.info("Обробка: productId={}, quantity={}", productId, quantity);
                    if (quantity > 0) {
                        Product product = productService.findById(productId);
                        if (product == null) {
                            logger.error("Товар із ID {} не знайдено", productId);
                            throw new IllegalStateException("Товар із ID " + productId + " не знайдено");
                        }
                        if (product.getStock() < quantity) {
                            logger.warn("Недостатньо товару {} на складі: потрібно {}, є {}", product.getName(), quantity, product.getStock());
                            throw new IllegalStateException("Недостатньо товару " + product.getName() + " на складі");
                        }
                        cartItems.add(new CartItemDTO(product, quantity));
                        logger.info("Додано до замовлення: {} (ID: {}), кількість: {}", product.getName(), productId, quantity);
                    }
                }
            }

            if (cartItems.isEmpty()) {
                logger.warn("Кошик порожній для користувача: {}", user.getUsername());
                throw new IllegalStateException("Кошик порожній");
            }

            Order order = new Order();
            order.setUser(user);
            order.setShippingAddress(shippingAddress);
            order.setStatus(OrderStatus.NEW);
            order.setOrderDate(LocalDateTime.now());
            order.setItems(cartItems.stream()
                    .map(item -> new OrderItem(null, order, item.getProduct(), item.getQuantity(), item.getProduct().getPrice()))
                    .collect(Collectors.toList()));
            order.setTotalPrice(order.getItems().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum());

            logger.info("Перед збереженням замовлення: user={}, itemsCount={}, totalPrice={}",
                    order.getUser().getUsername(), order.getItems().size(), order.getTotalPrice());
            orderService.saveOrder(order);
            logger.info("Замовлення успішно збережено для користувача: {}", user.getUsername());

            model.addAttribute("orderSuccess", true);
            return "order";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            logger.error("Помилка при оформленні замовлення: {}", e.getMessage());
            return "order";
        } catch (Exception e) {
            model.addAttribute("error", "Виникла помилка при оформленні замовлення");
            logger.error("Непередбачена помилка при оформленні замовлення: ", e);
            return "order";
        }
    }
}