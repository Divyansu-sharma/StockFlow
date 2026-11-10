package com.divyansu.inventory.service;

import com.divyansu.inventory.dto.SellerRequest;
import com.divyansu.inventory.dto.SellerResponse;
import com.divyansu.inventory.entity.Seller;
import com.divyansu.inventory.exception.SellerNotFoundException;
import com.divyansu.inventory.repository.SellerRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    private SellerResponse mapToResponse(Seller seller) {
        return new SellerResponse(
                seller.getId(),
                seller.getName(),
                seller.getEmail(),
                seller.getPhone(),
                seller.getAddress(),
                seller.getCreatedAt()
        );
    }

    public SellerResponse createSeller(SellerRequest request) {

        if (sellerRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Seller email already exists");
        }

        Seller seller = new Seller();
        seller.setName(request.getName());
        seller.setEmail(request.getEmail());
        seller.setPhone(request.getPhone());
        seller.setAddress(request.getAddress());

        return mapToResponse(sellerRepository.save(seller));
    }

    public Page<SellerResponse> getAllSellers(Pageable pageable) {
        return sellerRepository.findAll(pageable).map(this::mapToResponse);
    }

    public SellerResponse getSellerById(Long id) {

        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException(
                        "Seller not found with id: " + id));

        return mapToResponse(seller);
    }

    public SellerResponse updateSeller(Long id, SellerRequest request) {

        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException(
                        "Seller not found with id: " + id));

        if (sellerRepository.existsByEmailIgnoreCase(request.getEmail())
                && !seller.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Seller email already exists");
        }

        seller.setName(request.getName());
        seller.setEmail(request.getEmail());
        seller.setPhone(request.getPhone());
        seller.setAddress(request.getAddress());

        return mapToResponse(sellerRepository.save(seller));
    }

    public void deleteSeller(Long id) {

        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException(
                        "Seller not found with id: " + id));

        sellerRepository.delete(seller);
    }
}