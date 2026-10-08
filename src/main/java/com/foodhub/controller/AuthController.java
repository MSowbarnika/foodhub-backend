package com.foodhub.controller;

import com.foodhub.entity.User;
import com.foodhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public record RegisterRequest(String name, String email, String password) {}
    public record LoginRequest(String email, String password) {}

    @Value("${app.admin-email:}")
    private String adminEmail;

    private final UserRepository users;
    private final BCryptPasswordEncoder encoder;

    public AuthController(UserRepository users, BCryptPasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    private Map<String, Object> toResponse(User u) {
        return Map.of("name", u.getName(), "email", u.getEmail(), "token", u.getToken(), "role", u.getRole());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (users.findByEmail(req.email()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "This email is already registered."));
        }
        User u = new User();
        u.setName(req.name());
        u.setEmail(req.email());
        u.setPassword(encoder.encode(req.password()));
        u.setRole(!adminEmail.isBlank() && adminEmail.equalsIgnoreCase(req.email()) ? "ADMIN" : "USER");
        u.setToken(UUID.randomUUID().toString());
        users.save(u);
        return ResponseEntity.ok(toResponse(u));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        User u = users.findByEmail(req.email()).orElse(null);
        if (u == null || !encoder.matches(req.password(), u.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password."));
        }
        u.setToken(UUID.randomUUID().toString());
        users.save(u);
        return ResponseEntity.ok(toResponse(u));
    }
}