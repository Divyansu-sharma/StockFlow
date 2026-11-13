package com.divyansu.inventory.dto;

import java.time.LocalDateTime;

public class InventoryMovementReportResponse {

    private Long totalStockIn;
    private Long totalStockOut;
    private Long netStockMovement;

    private Long stockInTransactions;
    private Long stockOutTransactions;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public InventoryMovementReportResponse(
            Long totalStockIn,
            Long totalStockOut,
            Long stockInTransactions,
            Long stockOutTransactions,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        this.totalStockIn = totalStockIn;
        this.totalStockOut = totalStockOut;
        this.netStockMovement = totalStockIn - totalStockOut;
        this.stockInTransactions = stockInTransactions;
        this.stockOutTransactions = stockOutTransactions;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public Long getStockInTransactions() {
        return stockInTransactions;
    }

    public Long getStockOutTransactions() {
        return stockOutTransactions;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }
}