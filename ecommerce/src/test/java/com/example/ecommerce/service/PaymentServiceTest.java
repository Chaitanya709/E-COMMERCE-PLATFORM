package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.PaymentRequest;
import com.example.ecommerce.dto.response.PaymentResponse;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.reposistory.OrderRepository;
import com.example.ecommerce.reposistory.PaymentReposistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentReposistory paymentReposistory;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private PaymentService paymentService;

    private User user;
    private Order order;
    private Payment payment;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PLACED);
        order.setTotalAmount(BigDecimal.valueOf(200));

        payment = new Payment();
        payment.setId(1L);
        payment.setOrder(order);
        payment.setAmount(BigDecimal.valueOf(200));
        payment.setPaymentStatus(PaymentStatus.PENDING);
    }

    @Test
    @DisplayName("payOrder - success with no existing payment")
    void payOrder_noExistingPayment_success() {
        order.setPayment(null);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(paymentReposistory.save(any(Payment.class))).thenReturn(payment);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentRequest request = new PaymentRequest(PaymentMethod.CARD);

        PaymentResponse response = paymentService.payOrder(1L, request);

        assertNotNull(response);
        verify(paymentReposistory).save(any(Payment.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("payOrder - success retrying failed payment")
    void payOrder_retryFailedPayment_success() {
        payment.setPaymentStatus(PaymentStatus.FAILED);
        order.setPayment(payment);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(paymentReposistory.save(any(Payment.class))).thenReturn(payment);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentRequest request = new PaymentRequest(PaymentMethod.UPI);

        PaymentResponse response = paymentService.payOrder(1L, request);

        assertNotNull(response);
        assertEquals(PaymentStatus.SUCCESS, payment.getPaymentStatus());
        assertNotNull(payment.getTransactionId());
        assertNotNull(payment.getPaidAt());
    }

    @Test
    @DisplayName("payOrder - success retrying pending payment")
    void payOrder_retryPendingPayment_success() {
        payment.setPaymentStatus(PaymentStatus.PENDING);
        order.setPayment(payment);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(paymentReposistory.save(any(Payment.class))).thenReturn(payment);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentRequest request = new PaymentRequest(PaymentMethod.NET_BANKING);

        PaymentResponse response = paymentService.payOrder(1L, request);

        assertNotNull(response);
        assertEquals(PaymentStatus.SUCCESS, payment.getPaymentStatus());
    }

    @Test
    @DisplayName("payOrder - throws when order already paid")
    void payOrder_alreadyPaid_throwsBadRequest() {
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        order.setPayment(payment);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);

        PaymentRequest request = new PaymentRequest(PaymentMethod.CARD);

        assertThrows(BadRequestException.class, () -> paymentService.payOrder(1L, request));
    }

    @Test
    @DisplayName("payOrder - throws when order not found")
    void payOrder_orderNotFound_throwsException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        PaymentRequest request = new PaymentRequest(PaymentMethod.CARD);

        assertThrows(ResourceNotFoundException.class, () -> paymentService.payOrder(99L, request));
    }

    @Test
    @DisplayName("payOrder - throws AccessDenied for other user's order")
    void payOrder_accessDenied() {
        User otherUser = new User();
        otherUser.setId(2L);
        order.setUser(otherUser);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);

        PaymentRequest request = new PaymentRequest(PaymentMethod.CARD);

        assertThrows(AccessDeniedException.class, () -> paymentService.payOrder(1L, request));
    }

    @Test
    @DisplayName("payOrder - sets order status to CONFIRMED")
    void payOrder_setsOrderConfirmed() {
        order.setPayment(null);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(paymentReposistory.save(any(Payment.class))).thenReturn(payment);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentRequest request = new PaymentRequest(PaymentMethod.CASH_ON_DELIVERY);

        paymentService.payOrder(1L, request);

        assertEquals(OrderStatus.CONFIRMED, order.getOrderStatus());
    }

    @Test
    @DisplayName("getPaymentById - success")
    void getPaymentById_success() {
        when(paymentReposistory.findById(1L)).thenReturn(Optional.of(payment));
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);

        PaymentResponse response = paymentService.getPaymentById(1L);

        assertNotNull(response);
    }

    @Test
    @DisplayName("getPaymentById - throws when payment not found")
    void getPaymentById_notFound_throwsException() {
        when(paymentReposistory.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentById(99L));
    }

    @Test
    @DisplayName("getPaymentById - throws AccessDenied for other user's payment")
    void getPaymentById_accessDenied() {
        User otherUser = new User();
        otherUser.setId(2L);
        payment.getOrder().setUser(otherUser);

        when(paymentReposistory.findById(1L)).thenReturn(Optional.of(payment));
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);

        assertThrows(AccessDeniedException.class, () -> paymentService.getPaymentById(1L));
    }

    @Test
    @DisplayName("getPaymentByOrderId - success")
    void getPaymentByOrderId_success() {
        order.setPayment(payment);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);

        PaymentResponse response = paymentService.getPaymentByOrderId(1L);

        assertNotNull(response);
    }

    @Test
    @DisplayName("getPaymentByOrderId - throws when order not found")
    void getPaymentByOrderId_orderNotFound_throwsException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentByOrderId(99L));
    }

    @Test
    @DisplayName("getPaymentByOrderId - throws when no payment for order")
    void getPaymentByOrderId_noPayment_throwsException() {
        order.setPayment(null);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentByOrderId(1L));
    }
}
