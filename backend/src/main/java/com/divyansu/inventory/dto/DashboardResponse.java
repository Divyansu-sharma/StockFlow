
package com.divyansu.inventory.dto;

import java.math.BigDecimal;

public class DashboardResponse {

    private Long totalProducts;
    private Long totalCategories;
    private Long totalSellers;
    private Long totalStock;
    private BigDecimal totalInventoryValue;
    private Long lowStockCount;

    public DashboardResponse(
            Long totalProducts,
            Long totalCategories,
            Long totalSellers,
            Long totalStock,
            BigDecimal totalInventoryValue,
            Long lowStockCount) {

        this.totalProducts = totalProducts;
        this.totalCategories = totalCategories;
        this.totalSellers = totalSellers;
        this.totalStock = totalStock;
        this.totalInventoryValue = totalInventoryValue;
        this.lowStockCount = lowStockCount;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }

    public Long getTotalCategories() {
        return totalCategories;
    }

    public Long getTotalSellers() {
        return totalSellers;
    }

    public Long getTotalStock() {
        return totalStock;
    }

    public BigDecimal getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public Long getLowStockCount() {
        return lowStockCount;
    }
}
