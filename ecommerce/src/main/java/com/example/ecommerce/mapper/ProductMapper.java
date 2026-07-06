package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.request.CreateProductRequest;
import com.example.ecommerce.dto.request.UpdateProductRequest;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;

import java.time.LocalDateTime;

public class ProductMapper {

    public static Product toEntity(CreateProductRequest createProductRequest) {
        Product product = new Product();

        product.setName(createProductRequest.getName());
        product.setDescription(createProductRequest.getDescription());
        product.setPrice(createProductRequest.getPrice());
        product.setStockQuantity(createProductRequest.getStockQuantity());
        product.setImageUrl(createProductRequest.getImageUrl());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        return product;
    }

    public static Product UpdateEntity(Product product, UpdateProductRequest updateProductRequest) {

        product.setName(updateProductRequest.getName());
        product.setDescription(updateProductRequest.getDescription());
        product.setPrice(updateProductRequest.getPrice());
        product.setStockQuantity(updateProductRequest.getStockQuantity());
        product.setImageUrl(updateProductRequest.getImageUrl());
        product.setUpdatedAt(LocalDateTime.now());

        return product;
    }

    public static ProductResponse toResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();

        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setDescription(product.getDescription());
        productResponse.setPrice(product.getPrice());
        productResponse.setStockQuantity(product.getStockQuantity());
        productResponse.setImageUrl(product.getImageUrl());
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());

        if (product.getCategory() != null) {
            productResponse.setCategory(CategoryMapper.toResponse(product.getCategory()));
        }

        return productResponse;
    }

    private static Category toCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }

        Category category = new Category();
        category.setId(categoryId);
        return category;
    }
}
