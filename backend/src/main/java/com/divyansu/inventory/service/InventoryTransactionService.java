
package com.divyansu.inventory.service;

import com.divyansu.inventory.dto.InventoryTransactionRequest;
import com.divyansu.inventory.dto.InventoryTransactionResponse;
import com.divyansu.inventory.entity.*;
import com.divyansu.inventory.exception.InsufficientStockException;
import com.divyansu.inventory.exception.ProductNotFoundException;
import com.divyansu.inventory.exception.SellerNotFoundException;
import com.divyansu.inventory.repository.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    public InventoryTransactionService(
            InventoryTransactionRepository transactionRepository,
            ProductRepository productRepository,
            SellerRepository sellerRepository,
            UserRepository userRepository) {

        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public InventoryTransactionResponse createTransaction(
            InventoryTransactionRequest request,
            String userEmail) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found"));

        Seller seller = null;

        if (request.getSellerId() != null) {
            seller = sellerRepository.findById(request.getSellerId())
                    .orElseThrow(() ->
                            new SellerNotFoundException("Seller not found"));
        }

        if (request.getType() == TransactionType.IN) {

            product.setQuantity(
                    product.getQuantity() + request.getQuantity());

        } else if (request.getType() == TransactionType.OUT) {

            if (product.getQuantity() < request.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock. Available quantity: "
                                + product.getQuantity());
            }

            product.setQuantity(
                    product.getQuantity() - request.getQuantity());
        }

        productRepository.save(product);

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProduct(product);
        transaction.setSeller(seller);
        transaction.setPerformedBy(user);
        transaction.setType(request.getType());
        transaction.setQuantity(request.getQuantity());
        transaction.setNotes(request.getNotes());

        InventoryTransaction saved =
                transactionRepository.save(transaction);

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<InventoryTransactionResponse> getAllTransactions(
            Pageable pageable) {

        return transactionRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<InventoryTransactionResponse> getTransactionsByProduct(
            Long productId, Pageable pageable) {

        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product not found");
        }

        return transactionRepository
                .findByProduct_Id(productId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<InventoryTransactionResponse> filterTransactions(
            TransactionType type,
            Long productId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        if (startDate != null && endDate != null
                && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date");
        }

        if (productId != null && !productRepository.existsById(productId)) {
            throw new ProductNotFoundException(
                    "Product not found with id: " + productId);
        }

        return transactionRepository.findAll(
                InventoryTransactionSpecification.filterTransactions(
                        type, productId, startDate, endDate),
                pageable
        ).map(this::mapToResponse);
    }

    private InventoryTransactionResponse mapToResponse(
            InventoryTransaction transaction) {

        Product product = transaction.getProduct();
        Seller seller = transaction.getSeller();
        User user = transaction.getPerformedBy();

        return new InventoryTransactionResponse(
                transaction.getId(),
                product.getId(),
                product.getName(),
                product.getSku(),
                seller != null ? seller.getId() : null,
                seller != null ? seller.getName() : null,
                user.getId(),
                user.getEmail(),
                transaction.getType(),
                transaction.getQuantity(),
                transaction.getNotes(),
                transaction.getCreatedAt()
        );
    }
}
