package com.foodhub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    public static final Map<String, Integer> COUPONS = Map.of("FOOD30", 30);

    @GetMapping("/{code}")
    public ResponseEntity<?> validate(@PathVariable String code) {
        String key = code.toUpperCase();
        Integer percent = COUPONS.get(key);
        if (percent == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Invalid coupon code."));
        }
        return ResponseEntity.ok(Map.of("code", key, "percent", percent));
    }
}