package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateCategoryRequest;
import com.example.ecommerce.dto.request.UpdateCategoryRequest;
import com.example.ecommerce.dto.response.CategoryResponse;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.reposistory.CategoryReposistory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryReposistory categoryReposistory;

    @Transactional
   // @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(@Valid CreateCategoryRequest request) {
        categoryReposistory.findByName(request.getName())
                .ifPresent(category -> {
                    throw new BadRequestException("Category already exists");
                });

        Category categoryEntity = CategoryMapper.toEntity(request);
        Category savedCategory = categoryReposistory.save(categoryEntity);
        return CategoryMapper.toResponse(savedCategory);
    }

    @Transactional(readOnly = true)
     //@Cacheable(value = "categories")
    public List<CategoryResponse> getAll() {
        System.out.println("hit the db");
        return categoryReposistory.findAll().stream()
                .filter(category -> !category.isDeleted())
                .map(CategoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        Category category = categoryReposistory.findById(id)
                .filter(existingCategory -> !existingCategory.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return CategoryMapper.toResponse(category);
    }

    @Transactional
    //@CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse updateCategory(Long id, @Valid UpdateCategoryRequest request) {
        Category category = categoryReposistory.findById(id)
                .filter(existingCategory -> !existingCategory.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Category does not exist"));

        Category updatedCategory = CategoryMapper.UpdateEntity(category, request);
        Category savedCategory = categoryReposistory.save(updatedCategory);
        return CategoryMapper.toResponse(savedCategory);
    }

    @Transactional
    //@CacheEvict(value = "categories", allEntries = true)
    public void deleteById(Long id) {
        Category category = categoryReposistory.findById(id)
                .filter(existingCategory -> !existingCategory.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setDeleted(true);
        categoryReposistory.save(category);
    }
}
