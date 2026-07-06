package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateCategoryRequest;
import com.example.ecommerce.dto.request.UpdateCategoryRequest;
import com.example.ecommerce.dto.response.CategoryResponse;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.reposistory.CategoryReposistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryReposistory categoryReposistory;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic gadgets");
        category.setDeleted(false);
    }

    @Test
    @DisplayName("createCategory - success")
    void createCategory_success() {
        CreateCategoryRequest request = new CreateCategoryRequest("Electronics", "Electronic gadgets");

        when(categoryReposistory.findByName("Electronics")).thenReturn(Optional.empty());
        when(categoryReposistory.save(any(Category.class))).thenReturn(category);

        CategoryResponse response = categoryService.createCategory(request);

        assertNotNull(response);
        assertEquals("Electronics", response.getName());
        verify(categoryReposistory).save(any(Category.class));
    }

    @Test
    @DisplayName("createCategory - throws BadRequestException for duplicate name")
    void createCategory_duplicateName_throwsBadRequest() {
        CreateCategoryRequest request = new CreateCategoryRequest("Electronics", "desc");

        when(categoryReposistory.findByName("Electronics")).thenReturn(Optional.of(category));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> categoryService.createCategory(request));

        assertEquals("Category already exists", exception.getMessage());
        verify(categoryReposistory, never()).save(any());
    }

    @Test
    @DisplayName("getAll - returns only non-deleted categories")
    void getAll_filtersDeleted() {
        Category deleted = new Category();
        deleted.setId(2L);
        deleted.setName("Old");
        deleted.setDeleted(true);

        when(categoryReposistory.findAll()).thenReturn(List.of(category, deleted));

        List<CategoryResponse> responses = categoryService.getAll();

        assertEquals(1, responses.size());
        assertEquals("Electronics", responses.get(0).getName());
    }

    @Test
    @DisplayName("getAll - returns empty list")
    void getAll_emptyList() {
        when(categoryReposistory.findAll()).thenReturn(List.of());

        List<CategoryResponse> responses = categoryService.getAll();

        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("getById - success")
    void getById_success() {
        when(categoryReposistory.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponse response = categoryService.getById(1L);

        assertNotNull(response);
        assertEquals("Electronics", response.getName());
    }

    @Test
    @DisplayName("getById - throws ResourceNotFoundException for deleted category")
    void getById_deletedCategory_throwsException() {
        category.setDeleted(true);
        when(categoryReposistory.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getById(1L));
    }

    @Test
    @DisplayName("getById - throws ResourceNotFoundException when not found")
    void getById_notFound_throwsException() {
        when(categoryReposistory.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getById(99L));
    }

    @Test
    @DisplayName("updateCategory - success")
    void updateCategory_success() {
        when(categoryReposistory.findById(1L)).thenReturn(Optional.of(category));
        when(categoryReposistory.save(any(Category.class))).thenReturn(category);

        UpdateCategoryRequest request = new UpdateCategoryRequest("Updated Electronics", "new desc");

        CategoryResponse response = categoryService.updateCategory(1L, request);

        assertNotNull(response);
        verify(categoryReposistory).save(any(Category.class));
    }

    @Test
    @DisplayName("updateCategory - throws ResourceNotFoundException when not found")
    void updateCategory_notFound_throwsException() {
        when(categoryReposistory.findById(99L)).thenReturn(Optional.empty());

        UpdateCategoryRequest request = new UpdateCategoryRequest("name", "desc");

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(99L, request));
    }

    @Test
    @DisplayName("deleteById - success soft deletes")
    void deleteById_success() {
        when(categoryReposistory.findById(1L)).thenReturn(Optional.of(category));
        when(categoryReposistory.save(any(Category.class))).thenReturn(category);

        categoryService.deleteById(1L);

        assertTrue(category.isDeleted());
        verify(categoryReposistory).save(category);
    }

    @Test
    @DisplayName("deleteById - throws ResourceNotFoundException when not found")
    void deleteById_notFound_throwsException() {
        when(categoryReposistory.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteById(99L));
        verify(categoryReposistory, never()).save(any());
    }

    @Test
    @DisplayName("deleteById - throws ResourceNotFoundException for already deleted category")
    void deleteById_alreadyDeleted_throwsException() {
        category.setDeleted(true);
        when(categoryReposistory.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteById(1L));
    }
}
