package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.AddCartItemRequest;
import com.example.ecommerce.dto.request.UpdateCartItemRequest;
import com.example.ecommerce.dto.response.ApiResponse;
import com.example.ecommerce.dto.response.CartResponse;
import com.example.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @Valid @RequestBody AddCartItemRequest request) {

        CartResponse response = cartService.addToCart(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Item added to cart"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {
        CartResponse response = cartService.getCart();
        return ResponseEntity.ok(ApiResponse.success(response, "Cart fetched"));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCart(
            @PathVariable long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        CartResponse response = cartService.updateCart(cartItemId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Cart item updated"));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> deleteCartItem(
            @PathVariable long cartItemId) {

        CartResponse response = cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok(ApiResponse.success(response, "Cart item removed"));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart() {
        CartResponse response = cartService.deleteAll();
        return ResponseEntity.ok(ApiResponse.success(response, "Cart cleared"));
    }
}