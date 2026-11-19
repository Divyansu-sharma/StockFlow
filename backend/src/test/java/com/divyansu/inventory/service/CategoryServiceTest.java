
package com.divyansu.inventory.service;

import com.divyansu.inventory.dto.CategoryRequest;
import com.divyansu.inventory.dto.CategoryResponse;
import com.divyansu.inventory.entity.Category;
import com.divyansu.inventory.exception.CategoryNotFoundException;
import com.divyansu.inventory.repository.CategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryRequest request;

    @BeforeEach
    void setUp() {
        request = new CategoryRequest();
        request.setName("Electronics");
        request.setDescription("Electronic products");
    }

    @Test
    void createCategorySuccessfully() {

        when(categoryRepository.existsByNameIgnoreCase("Electronics"))
                .thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponse response =
                categoryService.createCategory(request);

        assertEquals("Electronics", response.getName());
        assertEquals("Electronic products", response.getDescription());

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategoryWithDuplicateName() {

        when(categoryRepository.existsByNameIgnoreCase("Electronics"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.createCategory(request)
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getAllCategoriesSuccessfully() {

        Category category = new Category();
        category.setName("Electronics");
        category.setDescription("Electronic products");

        Page<Category> page = new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<CategoryResponse> response =
                categoryService.getAllCategories(PageRequest.of(0, 10));

        assertEquals(1, response.getTotalElements());
        assertEquals("Electronics", response.getContent().get(0).getName());
    }

    @Test
    void getCategoryByIdSuccessfully() {

        Category category = new Category();
        category.setName("Electronics");
        category.setDescription("Electronic products");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        CategoryResponse response =
                categoryService.getCategoryById(1L);

        assertEquals("Electronics", response.getName());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryByIdWhenNotFound() {

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getCategoryById(99L)
        );
    }

    @Test
    void updateCategorySuccessfully() {

        Category category = new Category();
        category.setName("Old Category");
        category.setDescription("Old description");

        request.setName("Electronics");
        request.setDescription("Updated description");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponse response =
                categoryService.updateCategory(1L, request);

        assertEquals("Electronics", response.getName());
        assertEquals("Updated description", response.getDescription());

        verify(categoryRepository).save(category);
    }

    @Test
    void updateCategoryWithDuplicateName() {

        Category category = new Category();
        category.setName("Old Category");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.existsByNameIgnoreCase("Electronics"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.updateCategory(1L, request)
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategorySuccessfully() {

        Category category = new Category();
        category.setName("Electronics");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategoryWhenNotFound() {

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(99L)
        );

        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
