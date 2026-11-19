
package com.divyansu.inventory.controller;

import com.divyansu.inventory.dto.ApiResponse;
import com.divyansu.inventory.dto.ProductRequest;
import com.divyansu.inventory.dto.ProductResponse;
import com.divyansu.inventory.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductRequest request;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {

        request = new ProductRequest();
        request.setName("Laptop");
        request.setSku("LAP001");
        request.setDescription("Business Laptop");
        request.setPrice(new BigDecimal("50000"));
        request.setQuantity(20);
        request.setReorderLevel(5);

        productResponse = new ProductResponse(
                1L,
                "Laptop",
                "LAP001",
                "Business Laptop",
                new BigDecimal("50000"),
                20,
                5,
                null,
                null,
                null,
                null
        );
    }

    @Test
    void createProductSuccessfully() {

        when(productService.createProduct(request))
                .thenReturn(productResponse);

        ResponseEntity<ApiResponse<ProductResponse>> response =
                productController.createProduct(request);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Product created successfully",
                response.getBody().getMessage());
        assertEquals("Laptop", response.getBody().getData().getName());

        verify(productService).createProduct(request);
    }

    @Test
    void getAllProductsSuccessfully() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductResponse> page =
                new PageImpl<>(List.of(productResponse));

        when(productService.getAllProducts(pageable))
                .thenReturn(page);

        ResponseEntity<ApiResponse<Page<ProductResponse>>> response =
                productController.getAllProducts(pageable);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1,
                response.getBody().getData().getTotalElements());

        verify(productService).getAllProducts(pageable);
    }

    @Test
    void getProductByIdSuccessfully() {

        when(productService.getProductById(1L))
                .thenReturn(productResponse);

        ResponseEntity<ApiResponse<ProductResponse>> response =
                productController.getProductById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Laptop", response.getBody().getData().getName());

        verify(productService).getProductById(1L);
    }

    @Test
    void updateProductSuccessfully() {

        when(productService.updateProduct(1L, request))
                .thenReturn(productResponse);

        ResponseEntity<ApiResponse<ProductResponse>> response =
                productController.updateProduct(1L, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Laptop", response.getBody().getData().getName());

        verify(productService).updateProduct(1L, request);
    }

    @Test
    void deleteProductSuccessfully() {

        doNothing().when(productService).deleteProduct(1L);

        ResponseEntity<ApiResponse<Void>> response =
                productController.deleteProduct(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertNull(response.getBody().getData());

        verify(productService).deleteProduct(1L);
    }

    @Test
    void searchProductsSuccessfully() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductResponse> page =
                new PageImpl<>(List.of(productResponse));

        when(productService.searchProducts("Laptop", pageable))
                .thenReturn(page);

        ResponseEntity<ApiResponse<Page<ProductResponse>>> response =
                productController.searchProducts("Laptop", pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1,
                response.getBody().getData().getTotalElements());

        verify(productService).searchProducts("Laptop", pageable);
    }

    @Test
    void getLowStockProductsSuccessfully() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductResponse> page =
                new PageImpl<>(List.of(productResponse));

        when(productService.getLowStockProducts(pageable))
                .thenReturn(page);

        ResponseEntity<ApiResponse<Page<ProductResponse>>> response =
                productController.getLowStockProducts(pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1,
                response.getBody().getData().getTotalElements());

        verify(productService).getLowStockProducts(pageable);
    }
}
