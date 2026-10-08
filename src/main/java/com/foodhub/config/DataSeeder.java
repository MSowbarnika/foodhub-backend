package com.foodhub.config;

import com.foodhub.entity.Food;
import com.foodhub.repository.FoodRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final FoodRepository repo;

    public DataSeeder(FoodRepository repo) {
        this.repo = repo;
    }

    private Food food(String name, String category, double price, double rating, String emoji, String desc, String img) {
        Food f = new Food(null, name, category, price, rating, emoji, desc);
        f.setImageUrl(img);
        return f;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;
        repo.saveAll(List.of(
            food("Fire Crunch Burger", "Burger", 199, 4.9, "🍔", "Double patty, cheese and crispy onion.", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800"),
            food("Cheesy Volcano Pizza", "Pizza", 299, 4.8, "🍕", "Loaded cheese pizza with a crispy crust.", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800"),
            food("Golden Fried Chicken", "Chicken", 249, 4.7, "🍗", "Crispy fried chicken, juicy inside.", "https://images.unsplash.com/photo-1562967916-eb82221dfb92?w=800"),
            food("Choco Lava Dream", "Dessert", 129, 4.9, "🍰", "Warm chocolate lava cake.", "https://images.unsplash.com/photo-1624353365286-3f8d62daad51?w=800"),
            food("Mango Chill Shake", "Drinks", 99, 4.6, "🥤", "Fresh and thick mango shake.", "https://images.unsplash.com/photo-1541658016709-82535e94bc69?w=800"),
            food("Fudge Brownie", "Dessert", 79, 4.5, "🍫", "Rich, soft chocolate brownie.", "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=800")
        ));
    }
}