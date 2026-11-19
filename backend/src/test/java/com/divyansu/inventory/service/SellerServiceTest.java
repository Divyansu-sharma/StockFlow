
package com.divyansu.inventory.service;

import com.divyansu.inventory.dto.SellerRequest;
import com.divyansu.inventory.dto.SellerResponse;
import com.divyansu.inventory.entity.Seller;
import com.divyansu.inventory.exception.SellerNotFoundException;
import com.divyansu.inventory.repository.SellerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerService sellerService;

    private SellerRequest request;

    @BeforeEach
    void setUp() {
        request = new SellerRequest();
        request.setName("ABC Suppliers");
        request.setEmail("abc@example.com");
        request.setPhone("9876543210");
        request.setAddress("Bokaro, Jharkhand");
    }

    @Test
    void createSellerSuccessfully() {

        when(sellerRepository.existsByEmailIgnoreCase("abc@example.com"))
                .thenReturn(false);

        when(sellerRepository.save(any(Seller.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SellerResponse response = sellerService.createSeller(request);

        assertEquals("ABC Suppliers", response.getName());
        assertEquals("abc@example.com", response.getEmail());
        assertEquals("9876543210", response.getPhone());
        assertEquals("Bokaro, Jharkhand", response.getAddress());

        verify(sellerRepository).save(any(Seller.class));
    }

    @Test
    void createSellerWithDuplicateEmail() {

        when(sellerRepository.existsByEmailIgnoreCase("abc@example.com"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> sellerService.createSeller(request)
        );

        verify(sellerRepository, never()).save(any(Seller.class));
    }

    @Test
    void getAllSellersSuccessfully() {

        Seller seller = new Seller();
        seller.setName("ABC Suppliers");
        seller.setEmail("abc@example.com");
        seller.setPhone("9876543210");
        seller.setAddress("Bokaro, Jharkhand");

        Page<Seller> page = new PageImpl<>(List.of(seller));

        when(sellerRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<SellerResponse> response =
                sellerService.getAllSellers(PageRequest.of(0, 10));

        assertEquals(1, response.getTotalElements());
        assertEquals("ABC Suppliers", response.getContent().get(0).getName());
    }

    @Test
    void getSellerByIdSuccessfully() {

        Seller seller = new Seller();
        seller.setName("ABC Suppliers");
        seller.setEmail("abc@example.com");

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(seller));

        SellerResponse response = sellerService.getSellerById(1L);

        assertEquals("ABC Suppliers", response.getName());
        assertEquals("abc@example.com", response.getEmail());

        verify(sellerRepository).findById(1L);
    }

    @Test
    void getSellerByIdWhenNotFound() {

        when(sellerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                SellerNotFoundException.class,
                () -> sellerService.getSellerById(99L)
        );
    }

    @Test
    void updateSellerSuccessfully() {

        Seller seller = new Seller();
        seller.setName("Old Supplier");
        seller.setEmail("old@example.com");

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(seller));

        when(sellerRepository.existsByEmailIgnoreCase("abc@example.com"))
                .thenReturn(false);

        when(sellerRepository.save(any(Seller.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SellerResponse response =
                sellerService.updateSeller(1L, request);

        assertEquals("ABC Suppliers", response.getName());
        assertEquals("abc@example.com", response.getEmail());

        verify(sellerRepository).save(seller);
    }

    @Test
    void updateSellerWithDuplicateEmail() {

        Seller seller = new Seller();
        seller.setName("Old Supplier");
        seller.setEmail("old@example.com");

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(seller));

        when(sellerRepository.existsByEmailIgnoreCase("abc@example.com"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> sellerService.updateSeller(1L, request)
        );

        verify(sellerRepository, never()).save(any(Seller.class));
    }

    @Test
    void deleteSellerSuccessfully() {

        Seller seller = new Seller();
        seller.setName("ABC Suppliers");

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(seller));

        sellerService.deleteSeller(1L);

        verify(sellerRepository).delete(seller);
    }

    @Test
    void deleteSellerWhenNotFound() {

        when(sellerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                SellerNotFoundException.class,
                () -> sellerService.deleteSeller(99L)
        );

        verify(sellerRepository, never()).delete(any(Seller.class));
    }
}
