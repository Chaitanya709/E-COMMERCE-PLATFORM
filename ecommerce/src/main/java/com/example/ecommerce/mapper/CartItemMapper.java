package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.request.AddCartItemRequest;
import com.example.ecommerce.dto.request.UpdateCartItemRequest;
import com.example.ecommerce.dto.response.CartItemResponse;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;

public class CartItemMapper {

    public static CartItem toEntity(AddCartItemRequest addCartItemRequest, Cart cart, Product product) {
        CartItem cartItem = new CartItem();

        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(addCartItemRequest.getQuantity());
        cartItem.setPriceAtTime(product.getPrice());

        return cartItem;
    }

    public static CartItem UpdateEntity(CartItem cartItem, UpdateCartItemRequest updateCartItemRequest) {

        cartItem.setQuantity(updateCartItemRequest.getQuantity());

        return cartItem;
    }

    public static CartItemResponse toResponse(CartItem cartItem) {
        CartItemResponse cartItemResponse = new CartItemResponse();

        cartItemResponse.setId(cartItem.getId());
        cartItemResponse.setQuantity(cartItem.getQuantity());
        cartItemResponse.setPriceAtTime(cartItem.getPriceAtTime());

        if (cartItem.getCart() != null) {
            cartItemResponse.setCartId(cartItem.getCart().getId());
        }

        if (cartItem.getProduct() != null) {
            cartItemResponse.setProduct(ProductMapper.toResponse(cartItem.getProduct()));
        }

        return cartItemResponse;
    }
}

