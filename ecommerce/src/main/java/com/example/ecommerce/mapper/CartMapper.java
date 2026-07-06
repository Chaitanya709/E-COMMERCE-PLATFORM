package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.response.CartItemResponse;
import com.example.ecommerce.dto.response.CartResponse;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.User;

import java.util.ArrayList;
import java.util.List;

public class CartMapper {

    public static Cart toEntity(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cart;
    }

    public static CartResponse toResponse(Cart cart) {
        CartResponse cartResponse = new CartResponse();

        cartResponse.setId(cart.getId());

        if (cart.getUser() != null) {
            cartResponse.setUser(UserMapper.toResponse(cart.getUser()));
        }

        if (cart.getCartItems() != null) {
            cartResponse.setCartItems(toCartItemResponses(cart.getCartItems()));
        }

        return cartResponse;
    }

    private static List<CartItemResponse> toCartItemResponses(List<CartItem> cartItems) {
        List<CartItemResponse> cartItemResponses = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            cartItemResponses.add(CartItemMapper.toResponse(cartItem));
        }

        return cartItemResponses;
    }
}
