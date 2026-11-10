package com.divyansu.inventory.controller;

import com.divyansu.inventory.dto.ApiResponse;
import com.divyansu.inventory.dto.SellerRequest;
import com.divyansu.inventory.dto.SellerResponse;
import com.divyansu.inventory.service.SellerService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<SellerResponse>> createSeller(
            @Valid @RequestBody SellerRequest request) {

        SellerResponse seller = sellerService.createSeller(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Seller created successfully", seller));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<Page<SellerResponse>>> getAllSellers(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {

        Page<SellerResponse> sellers = sellerService.getAllSellers(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Sellers retrieved successfully", sellers));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<SellerResponse>> getSellerById(
            @PathVariable Long id) {

        SellerResponse seller = sellerService.getSellerById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Seller retrieved successfully", seller));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<SellerResponse>> updateSeller(
            @PathVariable Long id,
            @Valid @RequestBody SellerRequest request) {

        SellerResponse seller = sellerService.updateSeller(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Seller updated successfully", seller));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSeller(@PathVariable Long id) {

        sellerService.deleteSeller(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Seller deleted successfully", null));
    }
}