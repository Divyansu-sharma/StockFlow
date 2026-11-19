
package com.divyansu.inventory.controller;

import com.divyansu.inventory.dto.ApiResponse;
import com.divyansu.inventory.dto.CategoryRequest;
import com.divyansu.inventory.dto.CategoryResponse;
import com.divyansu.inventory.service.CategoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryRequest categoryRequest;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {

        categoryRequest = new CategoryRequest();
        categoryRequest.setName("Electronics");
        categoryRequest.setDescription("Electronic products");

        categoryResponse = new CategoryResponse(
                1L,
                "Electronics",
                "Electronic products",
                LocalDateTime.now()
        );
    }

    @Test
    void createCategory_shouldReturn201() {

        when(categoryService.createCategory(any(CategoryRequest.class)))
                .thenReturn(categoryResponse);

        ResponseEntity<ApiResponse<CategoryResponse>> response =
                categoryController.createCategory(categoryRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Category created successfully", response.getBody().getMessage());
        assertEquals("Electronics", response.getBody().getData().getName());

        verify(categoryService, times(1)).createCategory(categoryRequest);
    }

    @Test
    void getAllCategories_shouldReturn200() {

        Page<CategoryResponse> page = new PageImpl<>(
                List.of(categoryResponse)
        );

        when(categoryService.getAllCategories(any()))
                .thenReturn(page);

        ResponseEntity<ApiResponse<Page<CategoryResponse>>> response =
                categoryController.getAllCategories(PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Categories retrieved successfully", response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().getTotalElements());

        verify(categoryService, times(1)).getAllCategories(any());
    }

    @Test
    void getCategoryById_shouldReturn200() {

        when(categoryService.getCategoryById(1L))
                .thenReturn(categoryResponse);

        ResponseEntity<ApiResponse<CategoryResponse>> response =
                categoryController.getCategoryById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Category retrieved successfully", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData().getId());

        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    void updateCategory_shouldReturn200() {

        when(categoryService.updateCategory(eq(1L), any(CategoryRequest.class)))
                .thenReturn(categoryResponse);

        ResponseEntity<ApiResponse<CategoryResponse>> response =
                categoryController.updateCategory(1L, categoryRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Category updated successfully", response.getBody().getMessage());
        assertEquals("Electronics", response.getBody().getData().getName());

        verify(categoryService, times(1))
                .updateCategory(1L, categoryRequest);
    }

    @Test
    void deleteCategory_shouldReturn200() {

        doNothing().when(categoryService).deleteCategory(1L);

        ResponseEntity<ApiResponse<Void>> response =
                categoryController.deleteCategory(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Category deleted successfully", response.getBody().getMessage());
        assertNull(response.getBody().getData());

        verify(categoryService, times(1)).deleteCategory(1L);
    }
}
