package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.request.PaymentRequest;
import com.example.ecommerce.dto.response.PaymentResponse;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.entity.PaymentStatus;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

public class PaymentMapper {

    public static Payment toEntity(PaymentRequest paymentRequest, Order order){
        Payment payment = new Payment();

        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setOrder(order);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(order.getTotalAmount());
        payment.setCreatedAt(LocalDateTime.now());

        return payment;
    }

    public static PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();

        response.setId(payment.getId());
        response.setOrderId(payment.getOrder().getId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setTransactionId(payment.getTransactionId());
        response.setPaidAt(payment.getPaidAt());

        return response;
    }

    public static Payment toEntityPending(@Valid PaymentRequest request, Order order,Payment payment) {

        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setCreatedAt(LocalDateTime.now());

        return payment;
    }
}
