package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.request.CreateOrderRequest;
import com.example.ecommerce.dto.request.UpdateOrderStatusRequest;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class OrderMapper {

    public static Order toEntity(CreateOrderRequest createOrderRequest) {
        Order order = new Order();

        order.setOrderStatus(OrderStatus.PLACED);
        order.setPlacedAt(LocalDateTime.now());
        order.setOrderItem(new ArrayList<>());

        return order;
    }

    public static Order UpdateEntity(Order order, UpdateOrderStatusRequest updateOrderStatusRequest) {

        order.setOrderStatus(updateOrderStatusRequest.getOrderStatus());

        return order;
    }

    public static OrderResponse toResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();

        orderResponse.setId(order.getId());
        orderResponse.setTotalAmount(order.getTotalAmount());
        orderResponse.setOrderStatus(order.getOrderStatus());
        orderResponse.setPlacedAt(order.getPlacedAt());

        if (order.getUser() != null) {
            orderResponse.setUser(UserMapper.toResponse(order.getUser()));
        }

        if (order.getOrderItem() != null) {
            orderResponse.setOrderItems(order.getOrderItem().stream()
                    .map(OrderItemMapper::toResponse)
                    .collect(Collectors.toList()));
        }

        return orderResponse;
    }
}
