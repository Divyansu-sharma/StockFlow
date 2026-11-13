package com.divyansu.inventory.controller;

import com.divyansu.inventory.dto.ApiResponse;
import com.divyansu.inventory.dto.InventoryTransactionRequest;
import com.divyansu.inventory.dto.InventoryTransactionResponse;
import com.divyansu.inventory.entity.TransactionType;
import com.divyansu.inventory.service.InventoryTransactionService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/api/inventory/transactions")
public class InventoryTransactionController {

    private final InventoryTransactionService transactionService;

    public InventoryTransactionController(
            InventoryTransactionService transactionService) {
        this.transactionService = transactionService;
        
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<InventoryTransactionResponse>>
    createTransaction(
            @Valid @RequestBody InventoryTransactionRequest request,
            Authentication authentication) {

        InventoryTransactionResponse response =
                transactionService.createTransaction(
                        request, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true, "Inventory transaction created successfully",
                        response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>>
    getAllTransactions(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Transactions retrieved successfully",
                        transactionService.getAllTransactions(pageable)));
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>>
    getTransactionsByProduct(
            @PathVariable Long productId,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Product transactions retrieved successfully",
                        transactionService.getTransactionsByProduct(
                                productId, pageable)));
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>>
    filterTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        Page<InventoryTransactionResponse> response =
        transactionService.filterTransactions(
                type, productId, startDate, endDate, pageable
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Transactions filtered successfully",
                        response
                )
        );
    }
}
