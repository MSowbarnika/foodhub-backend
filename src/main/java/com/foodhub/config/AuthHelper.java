package com.foodhub.config;

import com.foodhub.entity.User;
import com.foodhub.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class AuthHelper {
    private final UserRepository users;

    public AuthHelper(UserRepository users) {
        this.users = users;
    }

    public User current(String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) return null;
        return users.findByToken(auth.substring(7)).orElse(null);
    }

    public boolean isAdmin(String auth) {
        User u = current(auth);
        return u != null && "ADMIN".equals(u.getRole());
    }
}