
package com.divyansu.inventory.controller;

import com.divyansu.inventory.dto.ApiResponse;
import com.divyansu.inventory.dto.SellerRequest;
import com.divyansu.inventory.dto.SellerResponse;
import com.divyansu.inventory.service.SellerService;

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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerControllerTest {

    @Mock
    private SellerService sellerService;

    @InjectMocks
    private SellerController sellerController;

    private SellerRequest sellerRequest;
    private SellerResponse sellerResponse;

    @BeforeEach
    void setUp() {

        sellerRequest = new SellerRequest();
        sellerRequest.setName("ABC Suppliers");
        sellerRequest.setEmail("abc@suppliers.com");
        sellerRequest.setPhone("9876543210");
        sellerRequest.setAddress("Bokaro, Jharkhand");

        sellerResponse = new SellerResponse(
                1L,
                "ABC Suppliers",
                "abc@suppliers.com",
                "9876543210",
                "Bokaro, Jharkhand",
                LocalDateTime.now()
        );
    }

    @Test
    void createSeller_shouldReturn201() {

        when(sellerService.createSeller(any(SellerRequest.class)))
                .thenReturn(sellerResponse);

        ResponseEntity<ApiResponse<SellerResponse>> response =
                sellerController.createSeller(sellerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Seller created successfully", response.getBody().getMessage());
        assertEquals("ABC Suppliers", response.getBody().getData().getName());

        verify(sellerService, times(1)).createSeller(sellerRequest);
    }

    @Test
    void getAllSellers_shouldReturn200() {

        Page<SellerResponse> page = new PageImpl<>(
                List.of(sellerResponse)
        );

        when(sellerService.getAllSellers(any()))
                .thenReturn(page);

        ResponseEntity<ApiResponse<Page<SellerResponse>>> response =
                sellerController.getAllSellers(PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Sellers retrieved successfully", response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().getTotalElements());

        verify(sellerService, times(1)).getAllSellers(any());
    }

    @Test
    void getSellerById_shouldReturn200() {

        when(sellerService.getSellerById(1L))
                .thenReturn(sellerResponse);

        ResponseEntity<ApiResponse<SellerResponse>> response =
                sellerController.getSellerById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Seller retrieved successfully", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData().getId());

        verify(sellerService, times(1)).getSellerById(1L);
    }

    @Test
    void updateSeller_shouldReturn200() {

        when(sellerService.updateSeller(eq(1L), any(SellerRequest.class)))
                .thenReturn(sellerResponse);

        ResponseEntity<ApiResponse<SellerResponse>> response =
                sellerController.updateSeller(1L, sellerRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Seller updated successfully", response.getBody().getMessage());
        assertEquals("ABC Suppliers", response.getBody().getData().getName());

        verify(sellerService, times(1))
                .updateSeller(1L, sellerRequest);
    }

    @Test
    void deleteSeller_shouldReturn200() {

        doNothing().when(sellerService).deleteSeller(1L);

        ResponseEntity<ApiResponse<Void>> response =
                sellerController.deleteSeller(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Seller deleted successfully", response.getBody().getMessage());
        assertNull(response.getBody().getData());

        verify(sellerService, times(1)).deleteSeller(1L);
    }
}
