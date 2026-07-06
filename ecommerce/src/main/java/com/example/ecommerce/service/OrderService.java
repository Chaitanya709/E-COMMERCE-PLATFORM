package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateOrderRequest;
import com.example.ecommerce.dto.request.UpdateOrderStatusRequest;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.OrderItemMapper;
import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.reposistory.CartReposistory;
import com.example.ecommerce.reposistory.OrderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartReposistory cartReposistory;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public OrderResponse createOrder(@Valid CreateOrderRequest request) {
        User user = authenticatedUserService.getCurrentUser();

        if (request.getCartItemIds() == null || request.getCartItemIds().isEmpty()) {
            throw new BadRequestException("Select at least one cart item to place order");
        }

        Cart cart = cartReposistory.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart does not exist"));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Order order = OrderMapper.toEntity(request);
        order.setUser(user);

        if (order.getOrderItem() == null) {
            order.setOrderItem(new ArrayList<>());
        }

        BigDecimal total = BigDecimal.ZERO;
        List<CartItem> orderedItems = new ArrayList<>();

        for (Long cartItemId : request.getCartItemIds()) {
            CartItem cartItem = cart.getCartItems().stream()
                    .filter(item -> item.getId().equals(cartItemId))
                    .findFirst()
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Cart item not found in user's cart: " + cartItemId));

            Product product = cartItem.getProduct();

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException("Stock not sufficient for product: " + product.getName());
            }

            OrderItem orderItem = OrderItemMapper.toEntity(cartItem, order);

            BigDecimal itemTotal = cartItem.getPriceAtTime()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            total = total.add(itemTotal);
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());

            order.getOrderItem().add(orderItem);
            orderedItems.add(cartItem);
        }

        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        cart.getCartItems().removeAll(orderedItems);
        cartReposistory.save(cart);

        return OrderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getMyOrderById(long orderId) {
        User user = authenticatedUserService.getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You cannot access another user's order");
        }

        return OrderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {
        User user = authenticatedUserService.getCurrentUser();

        return orderRepository.findByUserId(user.getId()).stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(long orderId, @Valid UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Order updatedOrder = OrderMapper.UpdateEntity(order, request);
        Order savedOrder = orderRepository.save(updatedOrder);

        return OrderMapper.toResponse(savedOrder);
    }
}
