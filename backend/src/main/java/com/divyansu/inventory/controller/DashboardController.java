
package com.divyansu.inventory.controller;

import com.divyansu.inventory.dto.ApiResponse;
import com.divyansu.inventory.dto.DashboardResponse;
import com.divyansu.inventory.service.DashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.divyansu.inventory.dto.InventoryMovementReportResponse;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import com.divyansu.inventory.dto.RecentTransactionResponse;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<DashboardResponse>>
    getDashboardStatistics() {

        DashboardResponse response =
                dashboardService.getDashboardStatistics();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Dashboard statistics retrieved successfully",
                        response
                )
        );
    }


    @GetMapping("/recent-transactions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<List<RecentTransactionResponse>>>
    getRecentTransactions(
            @RequestParam(defaultValue = "5") int limit) {

        List<RecentTransactionResponse> response =
                dashboardService.getRecentTransactions(limit);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Recent transactions retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/inventory-movement")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<InventoryMovementReportResponse>>
    getInventoryMovementReport(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate) {

        InventoryMovementReportResponse response =
                dashboardService.getInventoryMovementReport(
                        startDate, endDate);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Inventory movement report retrieved successfully",
                        response
                )
        );
    }

}
