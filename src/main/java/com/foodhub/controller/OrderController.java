package com.foodhub.controller;

import com.foodhub.config.AuthHelper;
import com.foodhub.entity.Food;
import com.foodhub.entity.Order;
import com.foodhub.entity.OrderItem;
import com.foodhub.entity.User;
import com.foodhub.repository.FoodRepository;
import com.foodhub.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orders;
    private final FoodRepository foods;
    private final AuthHelper helper;

    public OrderController(OrderRepository orders, FoodRepository foods, AuthHelper helper) {
        this.orders = orders;
        this.foods = foods;
        this.helper = helper;
    }

    @PostMapping
    public ResponseEntity<?> place(@RequestHeader(value = "Authorization", required = false) String auth,
                                   @RequestBody Order order) {
        User u = helper.current(auth);
        if (u == null) return ResponseEntity.status(401).body(Map.of("message", "Please log in."));
        if (order.getItems() == null || order.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Your cart is empty."));
        }

        double subtotal = 0;
        for (OrderItem item : order.getItems()) {
            Food food = item.getFoodId() == null ? null : foods.findById(item.getFoodId()).orElse(null);
            if (food == null || item.getQty() < 1) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid item in cart."));
            }
            item.setName(food.getName());
            item.setPrice(food.getPrice());
            subtotal += food.getPrice() * item.getQty();
        }

        double discount = 0;
        String code = order.getCouponCode();
        if (code != null && !code.isBlank()) {
            Integer percent = CouponController.COUPONS.get(code.toUpperCase());
            if (percent == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid coupon code."));
            }
            discount = Math.round(subtotal * percent) / 100.0;
            order.setCouponCode(code.toUpperCase());
        } else {
            order.setCouponCode(null);
        }

        String method = order.getPaymentMethod();
        if (method == null || !List.of("COD", "UPI", "CARD").contains(method)) method = "COD";

        order.setId(null);
        order.setUserEmail(u.getEmail());
        order.setDiscount(discount);
        order.setTotal(subtotal - discount);
        order.setPaymentMethod(method);
        order.setPaymentStatus("COD".equals(method) ? "Pay on delivery" : "Paid (demo)");
        order.setStatus("Placed");
        order.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
        return ResponseEntity.ok(orders.save(order));
    }

    @GetMapping
    public ResponseEntity<?> myOrders(@RequestHeader(value = "Authorization", required = false) String auth) {
        User u = helper.current(auth);
        if (u == null) return ResponseEntity.status(401).body(Map.of("message", "Please log in."));
        return ResponseEntity.ok(orders.findByUserEmailOrderByIdDesc(u.getEmail()));
    }
}