package com.example.sport_shop.controller;

import com.example.sport_shop.model.Product;
import com.example.sport_shop.model.User;
import com.example.sport_shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public String showProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product/read";
    }

    @GetMapping("/products/buy/{id}")
    public String buyProduct(@PathVariable Long id, @AuthenticationPrincipal User user, Model model) {
        if (user == null) {
            return "redirect:/login";
        }

        Product product = productService.findById(id);
        if (product != null && product.getStock() > 0) {
            model.addAttribute("productToAdd", product);
            return "redirect:/cart";
        } else {
            model.addAttribute("error", "Товар відсутній або закінчився на складі");
            return "redirect:/products";
        }
    }
}