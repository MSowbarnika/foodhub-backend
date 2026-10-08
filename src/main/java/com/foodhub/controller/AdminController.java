package com.foodhub.controller;

import com.foodhub.config.AuthHelper;
import com.foodhub.entity.Order;
import com.foodhub.repository.OrderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    public record StatusRequest(String status) {}

    private static final List<String> STATUSES = List.of("Placed", "Preparing", "Delivered");

    private final OrderRepository orders;
    private final AuthHelper helper;

    public AdminController(OrderRepository orders, AuthHelper helper) {
        this.orders = orders;
        this.helper = helper;
    }

    @GetMapping("/orders")
    public ResponseEntity<?> allOrders(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (!helper.isAdmin(auth)) return ResponseEntity.status(403).body(Map.of("message", "Admin access only."));
        return ResponseEntity.ok(orders.findAll(Sort.by(Sort.Direction.DESC, "id")));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateStatus(@RequestHeader(value = "Authorization", required = false) String auth,
                                          @PathVariable Long id, @RequestBody StatusRequest req) {
        if (!helper.isAdmin(auth)) return ResponseEntity.status(403).body(Map.of("message", "Admin access only."));
        if (!STATUSES.contains(req.status())) return ResponseEntity.badRequest().body(Map.of("message", "Invalid status."));
        Order o = orders.findById(id).orElse(null);
        if (o == null) return ResponseEntity.status(404).body(Map.of("message", "Order not found."));
        o.setStatus(req.status());
        return ResponseEntity.ok(orders.save(o));
    }
}