
package com.divyansu.inventory.controller;

import com.divyansu.inventory.dto.ApiResponse;
import com.divyansu.inventory.dto.DashboardResponse;
import com.divyansu.inventory.dto.RecentTransactionResponse;
import com.divyansu.inventory.dto.InventoryMovementReportResponse;
import com.divyansu.inventory.dto.ProductMovementResponse;
import com.divyansu.inventory.entity.TransactionType;
import com.divyansu.inventory.service.DashboardService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    private DashboardResponse dashboardResponse;
    private RecentTransactionResponse recentTransactionResponse;
    private InventoryMovementReportResponse inventoryMovementResponse;
    private ProductMovementResponse productMovementResponse;

    @BeforeEach
    void setUp() {

        dashboardResponse = new DashboardResponse(
                20L,
                5L,
                8L,
                150L,
                new BigDecimal("750000.00"),
                3L
        );

        recentTransactionResponse = new RecentTransactionResponse(
                1L,
                1L,
                "Laptop",
                "LAP001",
                1L,
                "ABC Suppliers",
                "admin@stockflow.com",
                TransactionType.IN,
                10,
                "Initial stock",
                LocalDateTime.now()
        );

        inventoryMovementResponse = new InventoryMovementReportResponse(
                100L,
                40L,
                10L,
                5L,
                null,
                null
        );

        productMovementResponse = new ProductMovementResponse(
                1L,
                "Laptop",
                "LAP001",
                100L,
                40L
        );
    }

    @Test
    void getDashboardStatistics_shouldReturn200() {

        when(dashboardService.getDashboardStatistics())
                .thenReturn(dashboardResponse);

        ResponseEntity<ApiResponse<DashboardResponse>> response =
                dashboardController.getDashboardStatistics();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(
                "Dashboard statistics retrieved successfully",
                response.getBody().getMessage());

        assertEquals(
                20L,
                response.getBody().getData().getTotalProducts());

        assertEquals(
                150L,
                response.getBody().getData().getTotalStock());

        verify(dashboardService, times(1))
                .getDashboardStatistics();
    }

    @Test
    void getRecentTransactions_shouldReturn200() {

        List<RecentTransactionResponse> transactions =
                List.of(recentTransactionResponse);

        when(dashboardService.getRecentTransactions(5))
                .thenReturn(transactions);

        ResponseEntity<ApiResponse<List<RecentTransactionResponse>>> response =
                dashboardController.getRecentTransactions(5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(
                "Recent transactions retrieved successfully",
                response.getBody().getMessage());

        assertEquals(
                1,
                response.getBody().getData().size());

        assertEquals(
                "Laptop",
                response.getBody().getData().get(0).getProductName());

        verify(dashboardService, times(1))
                .getRecentTransactions(5);
    }

    @Test
    void getRecentTransactions_withCustomLimit_shouldReturn200() {

        List<RecentTransactionResponse> transactions =
                List.of(recentTransactionResponse);

        when(dashboardService.getRecentTransactions(10))
                .thenReturn(transactions);

        ResponseEntity<ApiResponse<List<RecentTransactionResponse>>> response =
                dashboardController.getRecentTransactions(10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());

        verify(dashboardService, times(1))
                .getRecentTransactions(10);
    }

    @Test
    void getInventoryMovementReport_shouldReturn200() {

        LocalDateTime startDate =
                LocalDateTime.of(2026, 1, 1, 0, 0);

        LocalDateTime endDate =
                LocalDateTime.of(2026, 12, 31, 23, 59);

        InventoryMovementReportResponse report =
                new InventoryMovementReportResponse(
                        100L, 40L, 10L, 5L, startDate, endDate);

        when(dashboardService.getInventoryMovementReport(
                startDate, endDate))
                .thenReturn(report);

        ResponseEntity<ApiResponse<InventoryMovementReportResponse>> response =
                dashboardController.getInventoryMovementReport(
                        startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(
                "Inventory movement report retrieved successfully",
                response.getBody().getMessage());

        assertEquals(
                100L,
                response.getBody().getData().getTotalStockIn());

        assertEquals(
                60L,
                response.getBody().getData().getNetStockMovement());

        verify(dashboardService, times(1))
                .getInventoryMovementReport(startDate, endDate);
    }

    @Test
    void getInventoryMovementReport_withoutDates_shouldReturn200() {

        when(dashboardService.getInventoryMovementReport(null, null))
                .thenReturn(inventoryMovementResponse);

        ResponseEntity<ApiResponse<InventoryMovementReportResponse>> response =
                dashboardController.getInventoryMovementReport(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        assertEquals(
                40L,
                response.getBody().getData().getTotalStockOut());

        verify(dashboardService, times(1))
                .getInventoryMovementReport(null, null);
    }

    @Test
    void getProductMovementReport_shouldReturn200() {

        LocalDateTime startDate =
                LocalDateTime.of(2026, 1, 1, 0, 0);

        LocalDateTime endDate =
                LocalDateTime.of(2026, 12, 31, 23, 59);

        Page<ProductMovementResponse> page =
                new PageImpl<>(List.of(productMovementResponse));

        when(dashboardService.getProductMovementReport(
                eq(startDate),
                eq(endDate),
                any()))
                .thenReturn(page);

        ResponseEntity<ApiResponse<Page<ProductMovementResponse>>> response =
                dashboardController.getProductMovementReport(
                        startDate,
                        endDate,
                        PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        assertEquals(
                "Product movement report retrieved successfully",
                response.getBody().getMessage());

        assertEquals(
                1,
                response.getBody().getData().getTotalElements());

        assertEquals(
                "Laptop",
                response.getBody().getData().getContent()
                        .get(0).getProductName());

        verify(dashboardService, times(1))
                .getProductMovementReport(
                        eq(startDate),
                        eq(endDate),
                        any());
    }
}
