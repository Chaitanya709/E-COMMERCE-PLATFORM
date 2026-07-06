package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.AddCartItemRequest;
import com.example.ecommerce.dto.request.UpdateCartItemRequest;
import com.example.ecommerce.dto.response.CartResponse;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.reposistory.CartReposistory;
import com.example.ecommerce.reposistory.ProductReposistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartReposistory cartReposistory;

    @Mock
    private ProductReposistory productReposistory;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(100));
        product.setStockQuantity(10);
        product.setDeleted(false);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setPriceAtTime(BigDecimal.valueOf(100));

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>(List.of(cartItem)));
    }

    @Test
    @DisplayName("addToCart - success with new item")
    void addToCart_newItem_success() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(productReposistory.findById(1L)).thenReturn(Optional.of(product));
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartReposistory.save(any(Cart.class))).thenReturn(cart);

        AddCartItemRequest request = new AddCartItemRequest(1L, 1);

        CartResponse response = cartService.addToCart(request);

        assertNotNull(response);
        verify(cartReposistory).save(any(Cart.class));
    }

    @Test
    @DisplayName("addToCart - creates new cart if none exists")
    void addToCart_noCart_createsNewCart() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(productReposistory.findById(1L)).thenReturn(Optional.of(product));
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.empty());
        when(cartReposistory.save(any(Cart.class))).thenReturn(cart);

        AddCartItemRequest request = new AddCartItemRequest(1L, 1);

        CartResponse response = cartService.addToCart(request);

        assertNotNull(response);
        verify(cartReposistory, times(2)).save(any(Cart.class));
    }

    @Test
    @DisplayName("addToCart - merges quantity for existing product in cart")
    void addToCart_existingProduct_mergesQuantity() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(productReposistory.findById(1L)).thenReturn(Optional.of(product));
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartReposistory.save(any(Cart.class))).thenReturn(cart);

        AddCartItemRequest request = new AddCartItemRequest(1L, 3);

        cartService.addToCart(request);

        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    @DisplayName("addToCart - throws when product not found")
    void addToCart_productNotFound_throwsException() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(productReposistory.findById(99L)).thenReturn(Optional.empty());

        AddCartItemRequest request = new AddCartItemRequest(99L, 1);

        assertThrows(ResourceNotFoundException.class, () -> cartService.addToCart(request));
    }

    @Test
    @DisplayName("addToCart - throws when quantity invalid")
    void addToCart_invalidQuantity_throwsBadRequest() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(productReposistory.findById(1L)).thenReturn(Optional.of(product));

        AddCartItemRequest request = new AddCartItemRequest(1L, 0);

        assertThrows(BadRequestException.class, () -> cartService.addToCart(request));
    }

    @Test
    @DisplayName("addToCart - throws when stock insufficient")
    void addToCart_insufficientStock_throwsBadRequest() {
        product.setStockQuantity(1);
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(productReposistory.findById(1L)).thenReturn(Optional.of(product));

        AddCartItemRequest request = new AddCartItemRequest(1L, 5);

        assertThrows(BadRequestException.class, () -> cartService.addToCart(request));
    }

    @Test
    @DisplayName("getCart - success")
    void getCart_success() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.of(cart));

        CartResponse response = cartService.getCart();

        assertNotNull(response);
    }

    @Test
    @DisplayName("getCart - creates cart if none exists")
    void getCart_noCart_createsNew() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.empty());
        when(cartReposistory.save(any(Cart.class))).thenReturn(cart);

        CartResponse response = cartService.getCart();

        assertNotNull(response);
    }

    @Test
    @DisplayName("updateCart - success")
    void updateCart_success() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartReposistory.save(any(Cart.class))).thenReturn(cart);

        UpdateCartItemRequest request = new UpdateCartItemRequest(5);

        CartResponse response = cartService.updateCart(1L, request);

        assertNotNull(response);
        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    @DisplayName("updateCart - removes item when quantity is 0")
    void updateCart_zeroQuantity_removesItem() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartReposistory.save(any(Cart.class))).thenReturn(cart);

        UpdateCartItemRequest request = new UpdateCartItemRequest(0);

        CartResponse response = cartService.updateCart(1L, request);

        assertNotNull(response);
        assertTrue(cart.getCartItems().isEmpty());
    }

    @Test
    @DisplayName("updateCart - throws when cart item not found")
    void updateCart_cartItemNotFound_throwsException() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.of(cart));

        UpdateCartItemRequest request = new UpdateCartItemRequest(5);

        assertThrows(ResourceNotFoundException.class, () -> cartService.updateCart(99L, request));
    }

    @Test
    @DisplayName("updateCart - throws when stock insufficient")
    void updateCart_insufficientStock_throwsBadRequest() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.of(cart));

        UpdateCartItemRequest request = new UpdateCartItemRequest(100);

        assertThrows(BadRequestException.class, () -> cartService.updateCart(1L, request));
    }

    @Test
    @DisplayName("updateCart - throws when cart not found")
    void updateCart_cartNotFound_throwsException() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.empty());

        UpdateCartItemRequest request = new UpdateCartItemRequest(5);

        assertThrows(ResourceNotFoundException.class, () -> cartService.updateCart(1L, request));
    }

    @Test
    @DisplayName("deleteCartItem - success")
    void deleteCartItem_success() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartReposistory.save(any(Cart.class))).thenReturn(cart);

        CartResponse response = cartService.deleteCartItem(1L);

        assertNotNull(response);
        assertTrue(cart.getCartItems().isEmpty());
    }

    @Test
    @DisplayName("deleteCartItem - throws when item not found")
    void deleteCartItem_itemNotFound_throwsException() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.of(cart));

        assertThrows(ResourceNotFoundException.class, () -> cartService.deleteCartItem(99L));
    }

    @Test
    @DisplayName("deleteAll - clears all cart items")
    void deleteAll_success() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartReposistory.save(any(Cart.class))).thenReturn(cart);

        CartResponse response = cartService.deleteAll();

        assertNotNull(response);
        assertTrue(cart.getCartItems().isEmpty());
    }

    @Test
    @DisplayName("deleteAll - throws when cart not found")
    void deleteAll_cartNotFound_throwsException() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(cartReposistory.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.deleteAll());
    }
}
