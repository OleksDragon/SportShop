package com.example.sport_shop.service;

import com.example.sport_shop.model.Order;
import com.example.sport_shop.model.OrderItem;
import com.example.sport_shop.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Transactional
    public void saveOrder(Order order) {
        logger.info("Збереження замовлення для користувача: {}", order.getUser().getUsername());
        for (OrderItem item : order.getItems()) {
            logger.info("Оновлення складу для товару: {} (ID: {}), кількість: {}",
                    item.getProduct().getName(), item.getProduct().getId(), item.getQuantity());
            productService.updateStock(item.getProduct(), item.getQuantity());
        }
        orderRepository.save(order);
        logger.info("Замовлення успішно збережено з ID: {}", order.getId());
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}