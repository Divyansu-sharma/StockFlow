
package com.divyansu.inventory.service;

import com.divyansu.inventory.dto.ProductRequest;
import com.divyansu.inventory.entity.Product;
import com.divyansu.inventory.repository.CategoryRepository;
import com.divyansu.inventory.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.divyansu.inventory.exception.CategoryNotFoundException;
import com.divyansu.inventory.exception.ProductNotFoundException;
import com.divyansu.inventory.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.divyansu.inventory.entity.Category;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException(
                    "Product with this SKU already exists"
            );
        }

        Product product = new Product();

        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setReorderLevel(request.getReorderLevel());

        if (request.getCategoryId() != null) {
                Category category = categoryRepository.findById(
                        request.getCategoryId()
                ).orElseThrow(() ->
                        new CategoryNotFoundException("Category not found"));

                product.setCategory(category);
        }

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    public java.util.List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }   

    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with ID: " + id
                        )
                );

        return mapToResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with ID: " + id
                        )
                );

        if (!product.getSku().equals(request.getSku())
                && productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException(
                    "Product with this SKU already exists"
            );
        }

        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setReorderLevel(request.getReorderLevel());

        if (request.getCategoryId() != null) {
                Category category = categoryRepository.findById(
                        request.getCategoryId()
                ).orElseThrow(() ->
                        new CategoryNotFoundException("Category not found"));

                product.setCategory(category);
                } else {
                product.setCategory(null);
        }

        Product updatedProduct = productRepository.save(product);

        return mapToResponse(updatedProduct);
    }

    
    @Transactional
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with ID: " + id
                        )
                );

        productRepository.delete(product);
    }

    private ProductResponse mapToResponse(Product product) {

    Category category = product.getCategory();

    return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getSku(),
            product.getDescription(),
            product.getPrice(),
            product.getQuantity(),
            product.getReorderLevel(),
            product.getCreatedAt(),
            product.getUpdatedAt(),
            category != null ? category.getId() : null,
            category != null ? category.getName() : null
    );
}

public Page<ProductResponse> getAllProducts(Pageable pageable) {
    return productRepository.findAll(pageable)
            .map(this::mapToResponse);
}

public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
    return productRepository
            .findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(
                    keyword, keyword, pageable)
            .map(this::mapToResponse);
}

public Page<ProductResponse> getLowStockProducts(Pageable pageable) {
    return productRepository.findLowStockProducts(pageable)
            .map(this::mapToResponse);
}
}


