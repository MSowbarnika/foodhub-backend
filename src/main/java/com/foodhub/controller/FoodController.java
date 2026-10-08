package com.foodhub.controller;

import com.foodhub.config.AuthHelper;
import com.foodhub.entity.Food;
import com.foodhub.repository.FoodRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private final FoodRepository repo;
    private final AuthHelper helper;

    public FoodController(FoodRepository repo, AuthHelper helper) {
        this.repo = repo;
        this.helper = helper;
    }

    private ResponseEntity<?> denied() {
        return ResponseEntity.status(403).body(Map.of("message", "Admin access only."));
    }

    @GetMapping
    public List<Food> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Food> getById(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestHeader(value = "Authorization", required = false) String auth,
                                 @RequestBody Food food) {
        if (!helper.isAdmin(auth)) return denied();
        food.setId(null);
        return ResponseEntity.ok(repo.save(food));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(value = "Authorization", required = false) String auth,
                                    @PathVariable Long id, @RequestBody Food food) {
        if (!helper.isAdmin(auth)) return denied();
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        food.setId(id);
        return ResponseEntity.ok(repo.save(food));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader(value = "Authorization", required = false) String auth,
                                    @PathVariable Long id) {
        if (!helper.isAdmin(auth)) return denied();
        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Deleted."));
    }
}