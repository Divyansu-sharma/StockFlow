
package com.divyansu.inventory.repository;

import com.divyansu.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface DashboardRepository
        extends JpaRepository<Product, Long> {

    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM Product p")
    Long getTotalStock();

    @Query("SELECT COALESCE(SUM(p.price * p.quantity), 0) FROM Product p")
    BigDecimal getTotalInventoryValue();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity <= p.reorderLevel")
    Long getLowStockCount();
}
