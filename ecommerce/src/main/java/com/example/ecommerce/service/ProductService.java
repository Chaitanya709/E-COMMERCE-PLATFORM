package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateProductRequest;
import com.example.ecommerce.dto.request.UpdateProductRequest;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.reposistory.CategoryReposistory;
import com.example.ecommerce.reposistory.ProductReposistory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductReposistory productReposistory;
    private final CategoryReposistory categoryReposistory;

    @Transactional
    @CacheEvict(value = {"products", "productLists"}, allEntries = true)
    public ProductResponse createProduct(@Valid CreateProductRequest request) {
        Category category = categoryReposistory.findById(request.getCategoryId())
                .filter(existingCategory -> !existingCategory.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = ProductMapper.toEntity(request);
        product.setCategory(category);

        Product savedProduct = productReposistory.save(product);
        return ProductMapper.toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductResponse getById(long id) {
        System.out.println("hit the db");
        Product product = productReposistory.findById(id)
                .filter(existingProduct -> !existingProduct.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return ProductMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "productLists", key = "'allProducts'")
    public List<ProductResponse> getAll() {
        return productReposistory.findAll().stream()
                .filter(product -> !product.isDeleted())
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Transactional
    @CachePut(value = "products", key = "#id")
    @CacheEvict(value = "productLists", allEntries = true)
    public ProductResponse updateProduct(long id, @Valid UpdateProductRequest request) {
        Product product = productReposistory.findById(id)
                .filter(existingProduct -> !existingProduct.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Category category = categoryReposistory.findById(request.getCategoryId())
                .filter(existingCategory -> !existingCategory.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product updatedProduct = ProductMapper.UpdateEntity(product, request);
        updatedProduct.setCategory(category);

        Product savedProduct = productReposistory.save(updatedProduct);
        return ProductMapper.toResponse(savedProduct);
    }

    @Transactional
    @CacheEvict(value = {"products", "productLists"}, allEntries = true)
    public void deleteById(Long id) {
        Product product = productReposistory.findById(id)
                .filter(existingProduct -> !existingProduct.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setDeleted(true);
        productReposistory.save(product);
    }

    @Transactional(readOnly = true)
    //@Cacheable(value = "productLists", key = "'category:' + #categoryId")
    public List<ProductResponse> getProductsByCategory(long categoryId) {
        categoryReposistory.findById(categoryId)
                .filter(existingCategory -> !existingCategory.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return productReposistory.findByCategoryId(categoryId).stream()
                .filter(product -> !product.isDeleted())
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProduct(String keyword) {
        return productReposistory.findByNameContainingIgnoreCase(keyword).stream()
                .filter(product -> !product.isDeleted())
                .map(ProductMapper::toResponse)
                .toList();
    }
}
