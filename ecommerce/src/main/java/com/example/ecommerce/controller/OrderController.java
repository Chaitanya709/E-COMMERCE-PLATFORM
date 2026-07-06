package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.CreateOrderRequest;
import com.example.ecommerce.dto.request.UpdateOrderStatusRequest;
import com.example.ecommerce.dto.response.ApiResponse;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

        private final OrderService orderService;

        @PostMapping
        public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
                        @Valid @RequestBody CreateOrderRequest request) {

                OrderResponse response = orderService.createOrder(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success(response, "Order created"));
        }

        @GetMapping("/{orderId}")
        public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
                        @PathVariable long orderId) {

                return ResponseEntity.ok(
                                ApiResponse.success(orderService.getMyOrderById(orderId), "Order fetched"));
        }

        @GetMapping("/me")
        public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders() {
                return ResponseEntity.ok(
                                ApiResponse.success(orderService.getMyOrders(), "User orders fetched"));
        }

        @GetMapping("/admin/all")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
                return ResponseEntity.ok(
                                ApiResponse.success(orderService.getAllOrders(), "All orders fetched"));
        }

        @PutMapping("/{orderId}/status")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
                        @PathVariable long orderId,
                        @Valid @RequestBody UpdateOrderStatusRequest request) {

                return ResponseEntity.ok(
                                ApiResponse.success(orderService.updateOrderStatus(orderId, request),
                                                "Order status updated"));
        }
}