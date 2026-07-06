package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.response.OrderItemResponse;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;

public class OrderItemMapper {

    public static OrderItemResponse toResponse(OrderItem orderItem) {
        OrderItemResponse orderItemResponse = new OrderItemResponse();

        orderItemResponse.setId(orderItem.getId());
        orderItemResponse.setQuantity(orderItem.getQuantity());
        orderItemResponse.setPriceAtPurchase(orderItem.getPriceAtPurchase());

        if (orderItem.getOrder() != null) {
            orderItemResponse.setOrderId(orderItem.getOrder().getId());
        }

        if (orderItem.getProduct() != null) {
            orderItemResponse.setProduct(ProductMapper.toResponse(orderItem.getProduct()));
        }

        return orderItemResponse;
    }

    public static OrderItem toEntity(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();

        orderItem.setOrder(order);
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPriceAtPurchase(cartItem.getPriceAtTime());

        return orderItem;
    }
}
