package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateOrderRequest;
import com.example.ecommerce.dto.request.UpdateOrderStatusRequest;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.reposistory.CartReposistory;
import com.example.ecommerce.reposistory.OrderRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartReposistory cartReposistory;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Cart cart;
    private Product product;
    private CartItem cartItem;
    private Order order;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");
        user.setRole("USER");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(100));
        product.setStockQuantity(10);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setPriceAtTime(BigDecimal.valueOf(100));

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>(List.of(cartItem)));

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderItem(new ArrayList<>());
        order.setOrderStatus(OrderStatus.PLACED);
        order.setPlacedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("createOrder - success")
    void createOrder_success() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(cartReposistory.save(any(Cart.class))).thenReturn(cart);

        CreateOrderRequest request = new CreateOrderRequest(List.of(1L));

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        verify(orderRepository).save(any(Order.class));
        verify(cartReposistory).save(any(Cart.class));
    }

    @Test
    @DisplayName("createOrder - throws when cart item IDs empty")
    void createOrder_emptyCartItems_throwsBadRequest() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);

        CreateOrderRequest request = new CreateOrderRequest(List.of());

        assertThrows(BadRequestException.class, () -> orderService.createOrder(request));
    }

    @Test
    @DisplayName("createOrder - throws when cart item IDs null")
    void createOrder_nullCartItems_throwsBadRequest() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);

        CreateOrderRequest request = new CreateOrderRequest(null);

        assertThrows(BadRequestException.class, () -> orderService.createOrder(request));
    }

    @Test
    @DisplayName("createOrder - throws when cart not found")
    void createOrder_cartNotFound_throwsException() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.empty());

        CreateOrderRequest request = new CreateOrderRequest(List.of(1L));

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(request));
    }

    @Test
    @DisplayName("createOrder - throws when cart is empty")
    void createOrder_emptyCart_throwsBadRequest() {
        Cart emptyCart = new Cart();
        emptyCart.setId(1L);
        emptyCart.setUser(user);
        emptyCart.setCartItems(new ArrayList<>());

        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.of(emptyCart));

        CreateOrderRequest request = new CreateOrderRequest(List.of(1L));

        assertThrows(BadRequestException.class, () -> orderService.createOrder(request));
    }

    @Test
    @DisplayName("createOrder - throws when stock insufficient")
    void createOrder_insufficientStock_throwsBadRequest() {
        product.setStockQuantity(1);
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.of(cart));

        CreateOrderRequest request = new CreateOrderRequest(List.of(1L));

        assertThrows(BadRequestException.class, () -> orderService.createOrder(request));
        product.setStockQuantity(10);
    }

    @Test
    @DisplayName("createOrder - throws when cart item not in user's cart")
    void createOrder_cartItemNotInCart_throwsException() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.of(cart));

        CreateOrderRequest request = new CreateOrderRequest(List.of(99L));

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(request));
    }

    @Test
    @DisplayName("getMyOrderById - success")
    void getMyOrderById_success() {
        order.getUser().setId(1L);
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getMyOrderById(1L);

        assertNotNull(response);
    }

    @Test
    @DisplayName("getMyOrderById - throws AccessDenied for other user's order")
    void getMyOrderById_accessDenied() {
        User otherUser = new User();
        otherUser.setId(2L);
        order.setUser(otherUser);

        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(AccessDeniedException.class, () -> orderService.getMyOrderById(1L));
    }

    @Test
    @DisplayName("getMyOrderById - throws when order not found")
    void getMyOrderById_notFound_throwsException() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getMyOrderById(99L));
    }

    @Test
    @DisplayName("getMyOrders - returns user's orders")
    void getMyOrders_success() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(order));

        List<OrderResponse> responses = orderService.getMyOrders();

        assertEquals(1, responses.size());
    }

    @Test
    @DisplayName("getAllOrders - returns all orders")
    void getAllOrders_success() {
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<OrderResponse> responses = orderService.getAllOrders();

        assertEquals(1, responses.size());
    }

    @Test
    @DisplayName("updateOrderStatus - success")
    void updateOrderStatus_success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.CONFIRMED);

        OrderResponse response = orderService.updateOrderStatus(1L, request);

        assertNotNull(response);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("updateOrderStatus - throws when order not found")
    void updateOrderStatus_notFound_throwsException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.SHIPPED);

        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrderStatus(99L, request));
    }
}
