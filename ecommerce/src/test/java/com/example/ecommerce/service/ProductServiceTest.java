package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateProductRequest;
import com.example.ecommerce.dto.request.UpdateProductRequest;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.reposistory.CategoryReposistory;
import com.example.ecommerce.reposistory.ProductReposistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductReposistory productReposistory;

    @Mock
    private CategoryReposistory categoryReposistory;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDeleted(false);

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Gaming laptop");
        product.setPrice(BigDecimal.valueOf(999.99));
        product.setStockQuantity(10);
        product.setCategory(category);
        product.setDeleted(false);
    }

    @Test
    @DisplayName("createProduct - success")
    void createProduct_success() {
        CreateProductRequest request = new CreateProductRequest(
                "Laptop", "Gaming laptop", BigDecimal.valueOf(999.99), 10, null, 1L);

        when(categoryReposistory.findById(1L)).thenReturn(Optional.of(category));
        when(productReposistory.save(any(Product.class))).thenReturn(product);

        ProductResponse response = productService.createProduct(request);

        assertNotNull(response);
        assertEquals("Laptop", response.getName());
        verify(productReposistory).save(any(Product.class));
    }

    @Test
    @DisplayName("createProduct - throws ResourceNotFoundException for missing category")
    void createProduct_categoryNotFound_throwsException() {
        CreateProductRequest request = new CreateProductRequest(
                "Laptop", "desc", BigDecimal.valueOf(100), 5, null, 99L);

        when(categoryReposistory.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(request));
        verify(productReposistory, never()).save(any());
    }

    @Test
    @DisplayName("createProduct - throws for deleted category")
    void createProduct_deletedCategory_throwsException() {
        category.setDeleted(true);
        CreateProductRequest request = new CreateProductRequest(
                "Laptop", "desc", BigDecimal.valueOf(100), 5, null, 1L);

        when(categoryReposistory.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(request));
    }

    @Test
    @DisplayName("getById - success")
    void getById_success() {
        when(productReposistory.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getById(1L);

        assertNotNull(response);
        assertEquals("Laptop", response.getName());
        assertEquals(BigDecimal.valueOf(999.99), response.getPrice());
    }

    @Test
    @DisplayName("getById - throws ResourceNotFoundException")
    void getById_notFound_throwsException() {
        when(productReposistory.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getById(99L));
    }

    @Test
    @DisplayName("getById - throws for deleted product")
    void getById_deletedProduct_throwsException() {
        product.setDeleted(true);
        when(productReposistory.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(ResourceNotFoundException.class, () -> productService.getById(1L));
    }

    @Test
    @DisplayName("getAll - returns only non-deleted products")
    void getAll_filtersDeleted() {
        Product deleted = new Product();
        deleted.setId(2L);
        deleted.setName("Old Product");
        deleted.setDeleted(true);

        when(productReposistory.findAll()).thenReturn(List.of(product, deleted));

        List<ProductResponse> responses = productService.getAll();

        assertEquals(1, responses.size());
        assertEquals("Laptop", responses.get(0).getName());
    }

    @Test
    @DisplayName("updateProduct - success")
    void updateProduct_success() {
        when(productReposistory.findById(1L)).thenReturn(Optional.of(product));
        when(categoryReposistory.findById(1L)).thenReturn(Optional.of(category));
        when(productReposistory.save(any(Product.class))).thenReturn(product);

        UpdateProductRequest request = new UpdateProductRequest(
                "Gaming Laptop", "Updated", BigDecimal.valueOf(1299.99), 15, null, 1L);

        ProductResponse response = productService.updateProduct(1L, request);

        assertNotNull(response);
        verify(productReposistory).save(any(Product.class));
    }

    @Test
    @DisplayName("updateProduct - throws ResourceNotFoundException when not found")
    void updateProduct_notFound_throwsException() {
        when(productReposistory.findById(99L)).thenReturn(Optional.empty());

        UpdateProductRequest request = new UpdateProductRequest(
                "name", "desc", BigDecimal.valueOf(100), 5, null, 1L);

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(99L, request));
    }

    @Test
    @DisplayName("deleteById - soft deletes product")
    void deleteById_success() {
        when(productReposistory.findById(1L)).thenReturn(Optional.of(product));
        when(productReposistory.save(any(Product.class))).thenReturn(product);

        productService.deleteById(1L);

        assertTrue(product.isDeleted());
        verify(productReposistory).save(product);
    }

    @Test
    @DisplayName("deleteById - throws ResourceNotFoundException when not found")
    void deleteById_notFound_throwsException() {
        when(productReposistory.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteById(99L));
    }

    @Test
    @DisplayName("getProductsByCategory - returns filtered products")
    void getProductsByCategory_success() {
        when(categoryReposistory.findById(1L)).thenReturn(Optional.of(category));
        when(productReposistory.findByCategoryId(1L)).thenReturn(List.of(product));

        List<ProductResponse> responses = productService.getProductsByCategory(1L);

        assertEquals(1, responses.size());
        assertEquals("Laptop", responses.get(0).getName());
    }

    @Test
    @DisplayName("searchProduct - returns matching products")
    void searchProduct_success() {
        when(productReposistory.findByNameContainingIgnoreCase("laptop")).thenReturn(List.of(product));

        List<ProductResponse> responses = productService.searchProduct("laptop");

        assertEquals(1, responses.size());
    }
}
