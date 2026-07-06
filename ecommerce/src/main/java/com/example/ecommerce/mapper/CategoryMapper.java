package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.request.CreateCategoryRequest;
import com.example.ecommerce.dto.request.UpdateCategoryRequest;
import com.example.ecommerce.dto.response.CategoryResponse;
import com.example.ecommerce.entity.Category;

public class CategoryMapper {

    public static Category toEntity(CreateCategoryRequest createCategoryRequest) {
        Category category = new Category();

        category.setName(createCategoryRequest.getName());
        category.setDescription(createCategoryRequest.getDescription());

        return category;
    }

    public static Category UpdateEntity(Category category, UpdateCategoryRequest updateCategoryRequest) {

        category.setName(updateCategoryRequest.getName());
        category.setDescription(updateCategoryRequest.getDescription());

        return category;
    }

    public static CategoryResponse toResponse(Category category) {
        CategoryResponse categoryResponse = new CategoryResponse();

        categoryResponse.setId(category.getId());
        categoryResponse.setName(category.getName());
        categoryResponse.setDescription(category.getDescription());

        return categoryResponse;
    }
}
