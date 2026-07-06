package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.AddCartItemRequest;
import com.example.ecommerce.dto.request.UpdateCartItemRequest;
import com.example.ecommerce.dto.response.CartResponse;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.CartItemMapper;
import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.reposistory.CartReposistory;
import com.example.ecommerce.reposistory.ProductReposistory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartReposistory cartReposistory;
    private final ProductReposistory productReposistory;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public CartResponse addToCart(@Valid AddCartItemRequest request) {
        User user = authenticatedUserService.getCurrentUser();

        Product product = findProduct(request.getProductId());
        validateQuantity(request.getQuantity());
        validateStock(product, request.getQuantity());

        Cart cart = cartReposistory.findByUserId(user.getId())
                .orElseGet(() -> cartReposistory.save(CartMapper.toEntity(user)));

        ensureCartItems(cart);

        CartItem existingItem = findCartItemByProduct(cart, product);

        if (existingItem != null) {
            int updatedQuantity = existingItem.getQuantity() + request.getQuantity();
            validateStock(product, updatedQuantity);
            existingItem.setQuantity(updatedQuantity);
        } else {
            CartItem cartItem = CartItemMapper.toEntity(request, cart, product);
            cart.getCartItems().add(cartItem);
        }

        return CartMapper.toResponse(cartReposistory.save(cart));
    }

    @Transactional(readOnly = true)
    public CartResponse getCart() {
        User user = authenticatedUserService.getCurrentUser();

        Cart cart = cartReposistory.findByUserId(user.getId())
                .orElseGet(() -> cartReposistory.save(CartMapper.toEntity(user)));

        ensureCartItems(cart);

        return CartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse updateCart(long cartItemId, @Valid UpdateCartItemRequest request) {
        User user = authenticatedUserService.getCurrentUser();

        Cart cart = findCartByUser(user.getId());
        ensureCartItems(cart);

        CartItem existingItem = findCartItemById(cart, cartItemId);

        if (request.getQuantity() <= 0) {
            cart.getCartItems().remove(existingItem);
            return CartMapper.toResponse(cartReposistory.save(cart));
        }

        validateStock(existingItem.getProduct(), request.getQuantity());
        existingItem.setQuantity(request.getQuantity());

        return CartMapper.toResponse(cartReposistory.save(cart));
    }

    @Transactional
    public CartResponse deleteCartItem(long cartItemId) {
        User user = authenticatedUserService.getCurrentUser();

        Cart cart = findCartByUser(user.getId());
        ensureCartItems(cart);

        CartItem existingItem = findCartItemById(cart, cartItemId);
        cart.getCartItems().remove(existingItem);

        return CartMapper.toResponse(cartReposistory.save(cart));
    }

    @Transactional
    public CartResponse deleteAll() {
        User user = authenticatedUserService.getCurrentUser();

        Cart cart = findCartByUser(user.getId());
        ensureCartItems(cart);

        cart.getCartItems().clear();

        return CartMapper.toResponse(cartReposistory.save(cart));
    }

    private Product findProduct(long productId) {
        return productReposistory.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private Cart findCartByUser(long userId) {
        return cartReposistory.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart does not exist"));
    }

    private CartItem findCartItemById(Cart cart, long cartItemId) {
        return cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item does not exist"));
    }

    private CartItem findCartItemByProduct(Cart cart, Product product) {
        return cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
    }

    private void validateStock(Product product, int quantity) {
        if (product.getStockQuantity() < quantity) {
            throw new BadRequestException("Insufficient stock");
        }
    }

    private void ensureCartItems(Cart cart) {
        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>());
        }
    }
}
