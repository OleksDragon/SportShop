package com.example.sport_shop;

import com.example.sport_shop.model.Category;
import com.example.sport_shop.model.Product;
import com.example.sport_shop.service.ProductService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SportShopApplication {

	@Autowired
	private ProductService productService;

	public static void main(String[] args) {
		SpringApplication.run(SportShopApplication.class, args);
		System.out.println("Server start!");
	}

	@PostConstruct
	public void init() {
		if (productService.findAll().isEmpty()) { // Добавляем только если база пуста
			// Создаём категории с описанием
			Category sportsEquipment = new Category(null, "Спортивне обладнання", "Обладнання для різних видів спорту", new ArrayList<>());
			Category footwear = new Category(null, "Взуття", "Спортивне взуття для тренувань", new ArrayList<>());
			Category clothing = new Category(null, "Одяг", "Одяг для активного відпочинку", new ArrayList<>());
			Category accessories = new Category(null, "Аксесуари", "Додаткові спортивні аксесуари", new ArrayList<>());

			// Сохраняем категории
			productService.saveCategory(sportsEquipment);
			productService.saveCategory(footwear);
			productService.saveCategory(clothing);
			productService.saveCategory(accessories);

			// Создаём и связываем товары
			List<Product> products = new ArrayList<>();

			// Спортивне обладнання
			Product football = new Product(null, "Футбольний м'яч", "Якісний м'яч для гри", 500.0, 10, sportsEquipment, "/images/football.jpg");
			Product basketball = new Product(null, "Баскетбольний м'яч", "М'яч для баскетболу", 600.0, 8, sportsEquipment, "/images/basketball.jpg");
			Product racket = new Product(null, "Тенісна ракетка", "Легка ракетка для тенісу", 1200.0, 5, sportsEquipment, "/images/racket.jpg");

			// Взуття
			Product nikeShoes = new Product(null, "Кросівки Nike", "Зручні кросівки для бігу", 2000.0, 15, footwear, "/images/nike_shoes.jpg");
			Product adidasShoes = new Product(null, "Кросівки Adidas", "Стильні кросівки", 1800.0, 12, footwear, "/images/adidas_shoes.jpg");
			Product boots = new Product(null, "Футбольні бутси", "Бутси для гри на траві", 1500.0, 7, footwear, "/images/boots.jpg");

			// Одяг
			Product tshirt = new Product(null, "Спортивна футболка", "Дихаюча тканина", 400.0, 20, clothing, "/images/tshirt.jpg");
			Product shorts = new Product(null, "Шорти для тренувань", "Комфортні шорти", 350.0, 18, clothing, "/images/shorts.jpg");
			Product tracksuit = new Product(null, "Спортивний костюм", "Теплий костюм для спорту", 2500.0, 6, clothing, "/images/tracksuit.jpg");

			// Аксесуари (новая категория)
			Product gymBag = new Product(null, "Спортивна сумка", "Містка сумка для тренувань", 800.0, 15, accessories, "/images/gym_bag.jpg");
			Product waterBottle = new Product(null, "Пляшка для води", "Зручна пляшка 1л", 200.0, 25, accessories, "/images/water_bottle.jpg");
			Product kneePads = new Product(null, "Наколінники", "Захист для колін", 450.0, 10, accessories, "/images/knee_pads.jpg");

			// Добавляем товары в списки категорий
			sportsEquipment.getProducts().addAll(List.of(football, basketball, racket));
			footwear.getProducts().addAll(List.of(nikeShoes, adidasShoes, boots));
			clothing.getProducts().addAll(List.of(tshirt, shorts, tracksuit));
			accessories.getProducts().addAll(List.of(gymBag, waterBottle, kneePads));

			// Сохраняем товары
			products.add(football);
			products.add(basketball);
			products.add(racket);
			products.add(nikeShoes);
			products.add(adidasShoes);
			products.add(boots);
			products.add(tshirt);
			products.add(shorts);
			products.add(tracksuit);
			products.add(gymBag);
			products.add(waterBottle);
			products.add(kneePads);

			for (Product product : products) {
				productService.save(product);
			}

			// Обновляем категории с товарами
			productService.saveCategory(sportsEquipment);
			productService.saveCategory(footwear);
			productService.saveCategory(clothing);
			productService.saveCategory(accessories);
		}
	}
}