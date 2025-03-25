package com.example.sport_shop.controller;

import com.example.sport_shop.model.User;
import com.example.sport_shop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/cart")
    public String showCart(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("cart", cartService.getOrCreateCart(user));
        return "cart";
    }
}