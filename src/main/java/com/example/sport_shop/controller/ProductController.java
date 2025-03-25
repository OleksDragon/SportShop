package com.example.sport_shop.controller;

import com.example.sport_shop.model.Product;
import com.example.sport_shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    // Страница со списком товаров
    @GetMapping("/products")
    public String showProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product/read";
    }

    // Обработка покупки (переход на страницу заказа)
    @GetMapping("/products/buy/{id}")
    public String buyProduct(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        if (product != null && product.getStock() > 0) {
            model.addAttribute("product", product);
            return "order"; // Страница оформления заказа
        } else {
            model.addAttribute("error", "Товар відсутній або закінчився на складі");
            return "redirect:/products";
        }
    }
}