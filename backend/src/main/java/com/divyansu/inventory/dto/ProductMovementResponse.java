package com.divyansu.inventory.dto;

public class ProductMovementResponse {

    private Long productId;
    private String productName;
    private String productSku;
    private Long totalStockIn;
    private Long totalStockOut;
    private Long netStockMovement;

    public ProductMovementResponse(
            Long productId,
            String productName,
            String productSku,
            Long totalStockIn,
            Long totalStockOut) {

        this.productId = productId;
        this.productName = productName;
        this.productSku = productSku;
        this.totalStockIn = totalStockIn;
        this.totalStockOut = totalStockOut;
        this.netStockMovement = totalStockIn - totalStockOut;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public Long getTotalStockIn() {
        return totalStockIn;
    }

    public Long getTotalStockOut() {
        return totalStockOut;
    }

    public Long getNetStockMovement() {
        return netStockMovement;
    }
}