package com.example.sport_shop.controller;

import com.example.sport_shop.model.User;
import com.example.sport_shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/order")
    public String showOrderForm(Model model) {
        return "order";
    }

    @PostMapping("/order")
    public String createOrder(@AuthenticationPrincipal User user, @RequestParam String shippingAddress, Model model) {
        try {
            orderService.createOrder(user, shippingAddress);
            return "redirect:/products";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "order";
        }
    }
}