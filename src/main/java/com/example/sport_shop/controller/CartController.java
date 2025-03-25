package com.example.sport_shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    @GetMapping("/cart")
    public String showCart(Model model) {
        model.addAttribute("message", "Тут буде ваша корзина");
        return "cart";
    }
}
