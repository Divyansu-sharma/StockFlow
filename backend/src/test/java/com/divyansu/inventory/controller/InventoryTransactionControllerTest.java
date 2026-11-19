
package com.divyansu.inventory.controller;

import com.divyansu.inventory.dto.ApiResponse;
import com.divyansu.inventory.dto.InventoryTransactionRequest;
import com.divyansu.inventory.dto.InventoryTransactionResponse;
import com.divyansu.inventory.entity.TransactionType;
import com.divyansu.inventory.service.InventoryTransactionService;

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
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryTransactionControllerTest {

    @Mock
    private InventoryTransactionService transactionService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private InventoryTransactionController transactionController;

    private InventoryTransactionRequest transactionRequest;
    private InventoryTransactionResponse transactionResponse;

    @BeforeEach
    void setUp() {

        transactionRequest = new InventoryTransactionRequest();
        transactionRequest.setProductId(1L);
        transactionRequest.setSellerId(1L);
        transactionRequest.setType(TransactionType.IN);
        transactionRequest.setQuantity(10);
        transactionRequest.setNotes("Initial stock");

        transactionResponse = new InventoryTransactionResponse(
                1L,
                1L,
                "Laptop",
                "LAP001",
                1L,
                "ABC Suppliers",
                1L,
                "admin@stockflow.com",
                TransactionType.IN,
                10,
                "Initial stock",
                LocalDateTime.now()
        );
    }

    @Test
    void createTransaction_shouldReturn201() {

        when(authentication.getName())
                .thenReturn("admin@stockflow.com");

        when(transactionService.createTransaction(
                any(InventoryTransactionRequest.class),
                eq("admin@stockflow.com")))
                .thenReturn(transactionResponse);

        ResponseEntity<ApiResponse<InventoryTransactionResponse>> response =
                transactionController.createTransaction(
                        transactionRequest, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(
                "Inventory transaction created successfully",
                response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData().getId());
        assertEquals(TransactionType.IN,
                response.getBody().getData().getType());

        verify(transactionService, times(1))
                .createTransaction(
                        transactionRequest,
                        "admin@stockflow.com");
    }

    @Test
    void getAllTransactions_shouldReturn200() {

        Page<InventoryTransactionResponse> page =
                new PageImpl<>(List.of(transactionResponse));

        when(transactionService.getAllTransactions(any()))
                .thenReturn(page);

        ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>> response =
                transactionController.getAllTransactions(
                        PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(
                "Transactions retrieved successfully",
                response.getBody().getMessage());
        assertEquals(1,
                response.getBody().getData().getTotalElements());

        verify(transactionService, times(1))
                .getAllTransactions(any());
    }

    @Test
    void getTransactionsByProduct_shouldReturn200() {

        Page<InventoryTransactionResponse> page =
                new PageImpl<>(List.of(transactionResponse));

        when(transactionService.getTransactionsByProduct(eq(1L), any()))
                .thenReturn(page);

        ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>> response =
                transactionController.getTransactionsByProduct(
                        1L, PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(
                "Product transactions retrieved successfully",
                response.getBody().getMessage());
        assertEquals(1,
                response.getBody().getData().getTotalElements());

        verify(transactionService, times(1))
                .getTransactionsByProduct(eq(1L), any());
    }

    @Test
    void filterTransactions_withAllFilters_shouldReturn200() {

        LocalDateTime startDate =
                LocalDateTime.of(2026, 1, 1, 0, 0);

        LocalDateTime endDate =
                LocalDateTime.of(2026, 12, 31, 23, 59);

        Page<InventoryTransactionResponse> page =
                new PageImpl<>(List.of(transactionResponse));

        when(transactionService.filterTransactions(
                eq(TransactionType.IN),
                eq(1L),
                eq(startDate),
                eq(endDate),
                any()))
                .thenReturn(page);

        ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>> response =
                transactionController.filterTransactions(
                        TransactionType.IN,
                        1L,
                        startDate,
                        endDate,
                        PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(
                "Transactions filtered successfully",
                response.getBody().getMessage());
        assertEquals(1,
                response.getBody().getData().getTotalElements());

        verify(transactionService, times(1))
                .filterTransactions(
                        eq(TransactionType.IN),
                        eq(1L),
                        eq(startDate),
                        eq(endDate),
                        any());
    }

    @Test
    void filterTransactions_withoutOptionalFilters_shouldReturn200() {

        Page<InventoryTransactionResponse> page =
                new PageImpl<>(List.of(transactionResponse));

        when(transactionService.filterTransactions(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any()))
                .thenReturn(page);

        ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>> response =
                transactionController.filterTransactions(
                        null,
                        null,
                        null,
                        null,
                        PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1,
                response.getBody().getData().getTotalElements());

        verify(transactionService, times(1))
                .filterTransactions(
                        isNull(),
                        isNull(),
                        isNull(),
                        isNull(),
                        any());
    }

    @Test
    void filterTransactions_byTransactionType_shouldReturn200() {

        Page<InventoryTransactionResponse> page =
                new PageImpl<>(List.of(transactionResponse));

        when(transactionService.filterTransactions(
                eq(TransactionType.IN),
                isNull(),
                isNull(),
                isNull(),
                any()))
                .thenReturn(page);

        ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>> response =
                transactionController.filterTransactions(
                        TransactionType.IN,
                        null,
                        null,
                        null,
                        PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        verify(transactionService, times(1))
                .filterTransactions(
                        eq(TransactionType.IN),
                        isNull(),
                        isNull(),
                        isNull(),
                        any());
    }
}
