package com.example.sport_shop.controller;

import com.example.sport_shop.model.*;
import com.example.sport_shop.repository.RoleRepository;
import com.example.sport_shop.service.CategoryService;
import com.example.sport_shop.service.OrderService;
import com.example.sport_shop.service.ProductService;
import com.example.sport_shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/adminPanel")
public class AdminPanelController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String UPLOAD_DIR = "target/classes/static/images/products/";

    @GetMapping
    public String showAdminPanel() {
        return "adminPanel";
    }

    // Товари
    @GetMapping("/products")
    public String showProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "adminProducts";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product, @RequestParam("image") MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            String fileName = saveImage(image);
            product.setImagePath("/images/products/" + fileName);
        }
        productService.save(product);
        return "redirect:/adminPanel/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/adminPanel/products";
    }

    @PostMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("editProduct", product);
        model.addAttribute("products", productService.findAll());
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "adminProducts";
    }

    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute("editProduct") Product product, @RequestParam("image") MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            String fileName = saveImage(image);
            product.setImagePath("/images/products/" + fileName);
        } else {
            Product existingProduct = productService.findById(product.getId());
            product.setImagePath(existingProduct.getImagePath());
        }
        productService.save(product);
        return "redirect:/adminPanel/products";
    }

    // Замовлення
    @GetMapping("/orders")
    public String showOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        model.addAttribute("newOrder", new Order());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("products", productService.findAll());
        return "adminOrders";
    }

    @PostMapping("/orders/add")
    public String addOrder(
            @RequestParam("userId") Long userId,
            @RequestParam("productIds") List<Long> productIds,
            @RequestParam("shippingAddress") String shippingAddress,
            @RequestParam("status") OrderStatus status) {
        User user = userService.findById(userId);
        List<Product> products = productIds.stream().map(productService::findById).collect(Collectors.toList());
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus(status);
        order.setOrderDate(LocalDateTime.now());
        order.setItems(products.stream()
                .map(product -> new OrderItem(null, order, product, 1, product.getPrice()))
                .collect(Collectors.toList()));
        order.setTotalPrice(order.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum());
        orderService.saveOrder(order);
        return "redirect:/adminPanel/orders";
    }

    @PostMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteById(id);
        return "redirect:/adminPanel/orders";
    }

    @PostMapping("/orders/edit/{id}")
    public String editOrder(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id);
        model.addAttribute("editOrder", order);
        model.addAttribute("orders", orderService.findAll());
        model.addAttribute("newOrder", new Order());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("products", productService.findAll());
        List<Long> selectedProductIds = order.getItems().stream()
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toList());
        model.addAttribute("selectedProductIds", selectedProductIds);
        return "adminOrders";
    }

    @PostMapping("/orders/update")
    public String updateOrder(
            @RequestParam("id") Long id,
            @RequestParam("userId") Long userId,
            @RequestParam("productIds") List<Long> productIds,
            @RequestParam("shippingAddress") String shippingAddress,
            @RequestParam("status") OrderStatus status) {
        Order order = orderService.findById(id);
        User user = userService.findById(userId);
        List<Product> products = productIds.stream().map(productService::findById).collect(Collectors.toList());

        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus(status);

        // Очищаємо старий список items
        order.getItems().clear();
        // Додаємо нові елементи до існуючого списку
        products.forEach(product ->
                order.getItems().add(new OrderItem(null, order, product, 1, product.getPrice()))
        );

        // Оновлюємо totalPrice
        order.setTotalPrice(order.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum());

        orderService.saveOrder(order);
        return "redirect:/adminPanel/orders";
    }

    // Користувачі
    // Відображення списку користувачів
    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "adminUsers";
    }

    // Додавання нового користувача
    @PostMapping("/users/add")
    public String addUser(@ModelAttribute("newUser") User user, @RequestParam("roleNames") List<String> roleNames) {
        // Шифруємо пароль
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Ініціалізуємо Set для ролей
        Set<Role> roles = new HashSet<>();
        // Додаємо ролі вручну
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);
        }
        user.setRoles(roles);

        userService.save(user);
        return "redirect:/adminPanel/users";
    }

    // Відображення форми редагування користувача
    @PostMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("editUser", user);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "adminUsers";
    }

    // Оновлення користувача
    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute("editUser") User user,
                             @RequestParam("roleNames") List<String> roleNames,
                             @RequestParam(value = "password", required = false) String password) {
        User existingUser = userService.findById(user.getId());
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());

        // Оновлюємо пароль, якщо він був введений
        if (password != null && !password.isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(password));
        }

        // Ініціалізуємо Set для ролей
        Set<Role> roles = new HashSet<>();
        // Додаємо ролі вручну
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);
        }
        existingUser.setRoles(roles);

        userService.save(existingUser);
        return "redirect:/adminPanel/users";
    }

    // Видалення користувача
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/adminPanel/users";
    }

    private String saveImage(MultipartFile image) throws IOException {
        String fileName = image.getOriginalFilename();
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        Files.write(filePath, image.getBytes());
        return fileName;
    }
}