
package com.divyansu.inventory.service;

import com.divyansu.inventory.dto.DashboardResponse;
import com.divyansu.inventory.dto.RecentTransactionResponse;
import com.divyansu.inventory.dto.InventoryMovementReportResponse;

import com.divyansu.inventory.entity.InventoryTransaction;
import com.divyansu.inventory.entity.TransactionType;

import com.divyansu.inventory.repository.CategoryRepository;
import com.divyansu.inventory.repository.DashboardRepository;
import com.divyansu.inventory.repository.ProductRepository;
import com.divyansu.inventory.repository.SellerRepository;
import com.divyansu.inventory.repository.InventoryTransactionRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final InventoryTransactionRepository transactionRepository;

    public DashboardService(
            DashboardRepository dashboardRepository,
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            SellerRepository sellerRepository,
            InventoryTransactionRepository transactionRepository) {

        this.dashboardRepository = dashboardRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.sellerRepository = sellerRepository;
        this.transactionRepository = transactionRepository;
    }

    public DashboardResponse getDashboardStatistics() {

        Long totalProducts = productRepository.count();
        Long totalCategories = categoryRepository.count();
        Long totalSellers = sellerRepository.count();
        Long totalStock = dashboardRepository.getTotalStock();

        BigDecimal totalInventoryValue =
                dashboardRepository.getTotalInventoryValue();

        Long lowStockCount = dashboardRepository.getLowStockCount();

        return new DashboardResponse(
                totalProducts,
                totalCategories,
                totalSellers,
                totalStock,
                totalInventoryValue,
                lowStockCount
        );
    }

    public List<RecentTransactionResponse> getRecentTransactions(int limit) {

        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException(
                    "Limit must be between 1 and 100");
        }

        List<InventoryTransaction> transactions =
                transactionRepository.findAllByOrderByCreatedAtDesc(
                        PageRequest.of(0, limit));

        return transactions.stream()
                .map(transaction -> {

                    var product = transaction.getProduct();
                    var seller = transaction.getSeller();
                    var user = transaction.getPerformedBy();

                    return new RecentTransactionResponse(
                            transaction.getId(),
                            product.getId(),
                            product.getName(),
                            product.getSku(),
                            seller != null ? seller.getId() : null,
                            seller != null ? seller.getName() : null,
                            user.getEmail(),
                            transaction.getType(),
                            transaction.getQuantity(),
                            transaction.getNotes(),
                            transaction.getCreatedAt()
                    );
                })
                .toList();
    }

    public InventoryMovementReportResponse getInventoryMovementReport(
            LocalDateTime startDate,
            LocalDateTime endDate) {

        if (startDate != null && endDate != null
                && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date");
        }

        Long totalStockIn;
        Long totalStockOut;
        Long stockInTransactions;
        Long stockOutTransactions;

        if (startDate != null && endDate != null) {

            totalStockIn = transactionRepository.getTotalMovementBetweenDates(
                    TransactionType.IN, startDate, endDate);

            totalStockOut = transactionRepository.getTotalMovementBetweenDates(
                    TransactionType.OUT, startDate, endDate);

            stockInTransactions =
                    transactionRepository.countByTypeAndCreatedAtBetween(
                            TransactionType.IN, startDate, endDate);

            stockOutTransactions =
                    transactionRepository.countByTypeAndCreatedAtBetween(
                            TransactionType.OUT, startDate, endDate);

        } else if (startDate != null) {

            totalStockIn = transactionRepository.getTotalMovementFromDate(
                    TransactionType.IN, startDate);

            totalStockOut = transactionRepository.getTotalMovementFromDate(
                    TransactionType.OUT, startDate);

            stockInTransactions =
                    transactionRepository.countByTypeAndCreatedAtGreaterThanEqual(
                            TransactionType.IN, startDate);

            stockOutTransactions =
                    transactionRepository.countByTypeAndCreatedAtGreaterThanEqual(
                            TransactionType.OUT, startDate);

        } else if (endDate != null) {

            totalStockIn = transactionRepository.getTotalMovementUntilDate(
                    TransactionType.IN, endDate);

            totalStockOut = transactionRepository.getTotalMovementUntilDate(
                    TransactionType.OUT, endDate);

            stockInTransactions =
                    transactionRepository.countByTypeAndCreatedAtLessThanEqual(
                            TransactionType.IN, endDate);

            stockOutTransactions =
                    transactionRepository.countByTypeAndCreatedAtLessThanEqual(
                            TransactionType.OUT, endDate);

        } else {

            totalStockIn = transactionRepository.getTotalMovement(
                    TransactionType.IN);

            totalStockOut = transactionRepository.getTotalMovement(
                    TransactionType.OUT);

            stockInTransactions =
                    transactionRepository.countByType(TransactionType.IN);

            stockOutTransactions =
                    transactionRepository.countByType(TransactionType.OUT);
        }

        return new InventoryMovementReportResponse(
                totalStockIn,
                totalStockOut,
                stockInTransactions,
                stockOutTransactions,
                startDate,
                endDate
        );
    }
}
