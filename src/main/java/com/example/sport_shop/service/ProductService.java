package com.example.sport_shop.service;

import com.example.sport_shop.model.Category;
import com.example.sport_shop.model.Product;
import com.example.sport_shop.repository.CategoryRepository;
import com.example.sport_shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void updateStock(Product product, int quantity) {
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }
}