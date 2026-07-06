package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.CreateCategoryRequest;
import com.example.ecommerce.dto.request.UpdateCategoryRequest;
import com.example.ecommerce.dto.response.ApiResponse;
import com.example.ecommerce.dto.response.CategoryResponse;
import com.example.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        CategoryResponse response = categoryService.createCategory(createCategoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Category created"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAll(), "Categories fetched"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getById(id), "Category fetched"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest) {
        if (updateCategoryRequest == null) {
            throw new IllegalArgumentException("UpdateCategoryRequest must not be null");
        }
        return ResponseEntity
                .ok(ApiResponse.success(categoryService.updateCategory(id, updateCategoryRequest), "Category updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Category deleted"));
    }
}
