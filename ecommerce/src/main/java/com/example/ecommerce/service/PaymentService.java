package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.PaymentRequest;
import com.example.ecommerce.dto.response.PaymentResponse;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.PaymentMapper;
import com.example.ecommerce.reposistory.OrderRepository;
import com.example.ecommerce.reposistory.PaymentReposistory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentReposistory paymentReposistory;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public PaymentResponse payOrder(Long orderId, @Valid PaymentRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order does not exist"));

        User currentUser = authenticatedUserService.getCurrentUser();

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You cannot pay another user's order");
        }

        Payment payment = order.getPayment();

        if (payment == null) {
            payment = PaymentMapper.toEntity(request, order);
            order.setPayment(payment);
        } else if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new BadRequestException("Order already paid");
        } else if (payment.getPaymentStatus() == PaymentStatus.PENDING
                || payment.getPaymentStatus() == PaymentStatus.FAILED) {
            payment.setPaymentMethod(request.getPaymentMethod());
        } else {
            throw new BadRequestException("Invalid payment state");
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setPaidAt(LocalDateTime.now());

        order.setOrderStatus(OrderStatus.CONFIRMED);

        Payment savedPayment = paymentReposistory.save(payment);
        orderRepository.save(order);

        return PaymentMapper.toResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentReposistory.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        validatePaymentOwner(payment);

        return PaymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getPayment() == null) {
            throw new ResourceNotFoundException("Payment not found for order");
        }

        validatePaymentOwner(order.getPayment());
        return PaymentMapper.toResponse(order.getPayment());
    }

    private void validatePaymentOwner(Payment payment) {
        User currentUser = authenticatedUserService.getCurrentUser();

        if (!payment.getOrder().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You cannot access another user's payment");
        }
    }
}
