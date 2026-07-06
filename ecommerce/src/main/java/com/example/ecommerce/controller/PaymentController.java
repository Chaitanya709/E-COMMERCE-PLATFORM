package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.PaymentRequest;
import com.example.ecommerce.dto.response.ApiResponse;
import com.example.ecommerce.dto.response.PaymentResponse;
import com.example.ecommerce.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @PostMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> payOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentRequest request) throws AccessDeniedException {

        PaymentResponse response =
                paymentService.payOrder(orderId, request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Payment successful")
        );
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable Long paymentId) throws AccessDeniedException {
        PaymentResponse response =
                paymentService.getPaymentById(paymentId);

        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(
            @PathVariable Long orderId) throws AccessDeniedException {
        PaymentResponse response =
                paymentService.getPaymentByOrderId(orderId);

        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }

}
