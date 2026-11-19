
package com.divyansu.inventory.service;

import com.divyansu.inventory.dto.ProductRequest;
import com.divyansu.inventory.dto.ProductResponse;
import com.divyansu.inventory.entity.Product;
import com.divyansu.inventory.exception.ProductNotFoundException;
import com.divyansu.inventory.repository.CategoryRepository;
import com.divyansu.inventory.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequest request;

    @BeforeEach
    void setUp() {
        request = new ProductRequest();

        request.setName("Samsung Monitor");
        request.setSku("MON-1012");
        request.setDescription("27 inch monitor");
        request.setPrice(new BigDecimal("15000"));
        request.setQuantity(20);
        request.setReorderLevel(5);
    }

    @Test
    void createProductSuccessfully() {

        when(productRepository.existsBySku("MON-1012"))
                .thenReturn(false);

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response =
                productService.createProduct(request);

        assertEquals("Samsung Monitor", response.getName());
        assertEquals("MON-1012", response.getSku());
        assertEquals(20, response.getQuantity());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProductWithDuplicateSku() {

        when(productRepository.existsBySku("MON-1012"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(request)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductByIdSuccessfully() {

        Product product = new Product();
        product.setName("Samsung Monitor");
        product.setSku("MON-1012");
        product.setPrice(new BigDecimal("15000"));
        product.setQuantity(20);
        product.setReorderLevel(5);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response =
                productService.getProductById(1L);

        assertEquals("Samsung Monitor", response.getName());
        assertEquals("MON-1012", response.getSku());

        verify(productRepository).findById(1L);
    }

    @Test
    void getProductByIdWhenProductDoesNotExist() {

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(99L)
        );
    }

    @Test
    void updateProductSuccessfully() {

        Product existingProduct = new Product();
        existingProduct.setName("Old Monitor");
        existingProduct.setSku("MON-1000");
        existingProduct.setPrice(new BigDecimal("12000"));
        existingProduct.setQuantity(10);
        existingProduct.setReorderLevel(5);

        request.setName("Samsung Monitor");
        request.setSku("MON-1012");

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response =
                productService.updateProduct(1L, request);

        assertEquals("Samsung Monitor", response.getName());
        assertEquals("MON-1012", response.getSku());

        verify(productRepository).save(existingProduct);
    }

    @Test
    void deleteProductSuccessfully() {

        Product product = new Product();
        product.setName("Samsung Monitor");

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProductWhenProductDoesNotExist() {

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.deleteProduct(99L)
        );

        verify(productRepository, never()).delete(any(Product.class));
    }
}
