
package com.divyansu.inventory.dto;

import com.divyansu.inventory.entity.TransactionType;
import java.time.LocalDateTime;

public class RecentTransactionResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Long sellerId;
    private String sellerName;
    private String performedByEmail;
    private TransactionType type;
    private Integer quantity;
    private String notes;
    private LocalDateTime createdAt;

    public RecentTransactionResponse(
            Long id,
            Long productId,
            String productName,
            String productSku,
            Long sellerId,
            String sellerName,
            String performedByEmail,
            TransactionType type,
            Integer quantity,
            String notes,
            LocalDateTime createdAt) {

        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productSku = productSku;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.performedByEmail = performedByEmail;
        this.type = type;
        this.quantity = quantity;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductSku() { return productSku; }
    public Long getSellerId() { return sellerId; }
    public String getSellerName() { return sellerName; }
    public String getPerformedByEmail() { return performedByEmail; }
    public TransactionType getType() { return type; }
    public Integer getQuantity() { return quantity; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
