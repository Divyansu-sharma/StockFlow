
package com.divyansu.inventory.repository;

import com.divyansu.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    Page<Product> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(
        String name,
        String sku,
        Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE p.quantity <= p.reorderLevel")
    Page<Product> findLowStockProducts(Pageable pageable);
}
