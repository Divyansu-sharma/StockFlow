
package com.divyansu.inventory.service;

import com.divyansu.inventory.dto.*;
import com.divyansu.inventory.entity.*;
import com.divyansu.inventory.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private InventoryTransactionRepository transactionRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private Product product;
    private User user;
    private Seller seller;

    private void setId(Object entity, Long id) {
        try {
            Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {

        product = new Product();
        setId(product, 1L);
        product.setName("Laptop");
        product.setSku("LAP001");
        product.setQuantity(20);

        user = new User();
        setId(user, 1L);
        user.setEmail("admin@stockflow.com");

        seller = new Seller();
        setId(seller, 1L);
        seller.setName("ABC Suppliers");
        seller.setEmail("abc@example.com");
    }

    // 1. Dashboard statistics

    @Test
    void getDashboardStatisticsSuccessfully() {

        when(productRepository.count()).thenReturn(10L);
        when(categoryRepository.count()).thenReturn(5L);
        when(sellerRepository.count()).thenReturn(3L);

        when(dashboardRepository.getTotalStock())
                .thenReturn(100L);

        when(dashboardRepository.getTotalInventoryValue())
                .thenReturn(new BigDecimal("50000.00"));

        when(dashboardRepository.getLowStockCount())
                .thenReturn(2L);

        DashboardResponse response =
                dashboardService.getDashboardStatistics();

        assertNotNull(response);

        verify(productRepository).count();
        verify(categoryRepository).count();
        verify(sellerRepository).count();
        verify(dashboardRepository).getTotalStock();
        verify(dashboardRepository).getTotalInventoryValue();
        verify(dashboardRepository).getLowStockCount();
    }

    // 2. Recent transactions

    @Test
    void getRecentTransactionsSuccessfully() {

        InventoryTransaction transaction = createTransaction();

        when(transactionRepository.findAllByOrderByCreatedAtDesc(
                PageRequest.of(0, 5)))
                .thenReturn(List.of(transaction));

        List<RecentTransactionResponse> response =
                dashboardService.getRecentTransactions(5);

        assertEquals(1, response.size());

        verify(transactionRepository)
                .findAllByOrderByCreatedAtDesc(PageRequest.of(0, 5));
    }

    // 3. Recent transactions with custom limit

    @Test
    void getRecentTransactionsWithCustomLimit() {

        when(transactionRepository.findAllByOrderByCreatedAtDesc(
                PageRequest.of(0, 10)))
                .thenReturn(List.of());

        List<RecentTransactionResponse> response =
                dashboardService.getRecentTransactions(10);

        assertTrue(response.isEmpty());

        verify(transactionRepository)
                .findAllByOrderByCreatedAtDesc(PageRequest.of(0, 10));
    }

    // 4. Invalid recent transaction limit

    @Test
    void getRecentTransactionsWithInvalidLimit() {

        assertThrows(
                IllegalArgumentException.class,
                () -> dashboardService.getRecentTransactions(0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> dashboardService.getRecentTransactions(101)
        );

        verifyNoInteractions(transactionRepository);
    }

    // 5. Inventory movement without date filters

    @Test
    void getInventoryMovementReportWithoutDates() {

        when(transactionRepository.getTotalMovement(TransactionType.IN))
                .thenReturn(100L);

        when(transactionRepository.getTotalMovement(TransactionType.OUT))
                .thenReturn(40L);

        when(transactionRepository.countByType(TransactionType.IN))
                .thenReturn(10L);

        when(transactionRepository.countByType(TransactionType.OUT))
                .thenReturn(5L);

        InventoryMovementReportResponse response =
                dashboardService.getInventoryMovementReport(null, null);

        assertNotNull(response);

        verify(transactionRepository)
                .getTotalMovement(TransactionType.IN);

        verify(transactionRepository)
                .getTotalMovement(TransactionType.OUT);

        verify(transactionRepository)
                .countByType(TransactionType.IN);

        verify(transactionRepository)
                .countByType(TransactionType.OUT);
    }

    // 6. Inventory movement with start date

    @Test
    void getInventoryMovementReportWithStartDate() {

        LocalDateTime start =
                LocalDateTime.of(2026, 11, 1, 0, 0);

        when(transactionRepository.getTotalMovementFromDate(
                TransactionType.IN, start))
                .thenReturn(80L);

        when(transactionRepository.getTotalMovementFromDate(
                TransactionType.OUT, start))
                .thenReturn(30L);

        when(transactionRepository.countByTypeAndCreatedAtGreaterThanEqual(
                TransactionType.IN, start))
                .thenReturn(8L);

        when(transactionRepository.countByTypeAndCreatedAtGreaterThanEqual(
                TransactionType.OUT, start))
                .thenReturn(3L);

        InventoryMovementReportResponse response =
                dashboardService.getInventoryMovementReport(start, null);

        assertNotNull(response);

        verify(transactionRepository)
                .getTotalMovementFromDate(TransactionType.IN, start);

        verify(transactionRepository)
                .getTotalMovementFromDate(TransactionType.OUT, start);
    }

    // 7. Inventory movement with end date

    @Test
    void getInventoryMovementReportWithEndDate() {

        LocalDateTime end =
                LocalDateTime.of(2026, 11, 19, 23, 59);

        when(transactionRepository.getTotalMovementUntilDate(
                TransactionType.IN, end))
                .thenReturn(60L);

        when(transactionRepository.getTotalMovementUntilDate(
                TransactionType.OUT, end))
                .thenReturn(20L);

        when(transactionRepository.countByTypeAndCreatedAtLessThanEqual(
                TransactionType.IN, end))
                .thenReturn(6L);

        when(transactionRepository.countByTypeAndCreatedAtLessThanEqual(
                TransactionType.OUT, end))
                .thenReturn(2L);

        InventoryMovementReportResponse response =
                dashboardService.getInventoryMovementReport(null, end);

        assertNotNull(response);

        verify(transactionRepository)
                .getTotalMovementUntilDate(TransactionType.IN, end);

        verify(transactionRepository)
                .getTotalMovementUntilDate(TransactionType.OUT, end);
    }

    // 8. Inventory movement with both dates

    @Test
    void getInventoryMovementReportWithDateRange() {

        LocalDateTime start =
                LocalDateTime.of(2026, 11, 1, 0, 0);

        LocalDateTime end =
                LocalDateTime.of(2026, 11, 19, 23, 59);

        when(transactionRepository.getTotalMovementBetweenDates(
                TransactionType.IN, start, end))
                .thenReturn(50L);

        when(transactionRepository.getTotalMovementBetweenDates(
                TransactionType.OUT, start, end))
                .thenReturn(15L);

        when(transactionRepository.countByTypeAndCreatedAtBetween(
                TransactionType.IN, start, end))
                .thenReturn(5L);

        when(transactionRepository.countByTypeAndCreatedAtBetween(
                TransactionType.OUT, start, end))
                .thenReturn(2L);

        InventoryMovementReportResponse response =
                dashboardService.getInventoryMovementReport(start, end);

        assertNotNull(response);

        verify(transactionRepository)
                .getTotalMovementBetweenDates(
                        TransactionType.IN, start, end);

        verify(transactionRepository)
                .getTotalMovementBetweenDates(
                        TransactionType.OUT, start, end);
    }

    // 9. Invalid inventory movement date range

    @Test
    void getInventoryMovementReportWithInvalidDateRange() {

        LocalDateTime start =
                LocalDateTime.of(2026, 11, 20, 0, 0);

        LocalDateTime end =
                LocalDateTime.of(2026, 11, 10, 0, 0);

        assertThrows(
                IllegalArgumentException.class,
                () -> dashboardService.getInventoryMovementReport(start, end)
        );

        verifyNoInteractions(transactionRepository);
    }

    // 10. Product movement without date filters

    @Test
    void getProductMovementReportWithoutDates() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductMovementResponse> expected =
                new PageImpl<>(List.of());

        when(transactionRepository.getProductMovement(pageable))
                .thenReturn(expected);

        Page<ProductMovementResponse> response =
                dashboardService.getProductMovementReport(
                        null, null, pageable);

        assertNotNull(response);
        assertEquals(0, response.getTotalElements());

        verify(transactionRepository).getProductMovement(pageable);
    }

    // 11. Product movement with start date

    @Test
    void getProductMovementReportWithStartDate() {

        LocalDateTime start =
                LocalDateTime.of(2026, 11, 1, 0, 0);

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductMovementResponse> expected =
                new PageImpl<>(List.of());

        when(transactionRepository.getProductMovementFromDate(
                start, pageable))
                .thenReturn(expected);

        Page<ProductMovementResponse> response =
                dashboardService.getProductMovementReport(
                        start, null, pageable);

        assertNotNull(response);

        verify(transactionRepository)
                .getProductMovementFromDate(start, pageable);
    }

    // 12. Product movement with end date

    @Test
    void getProductMovementReportWithEndDate() {

        LocalDateTime end =
                LocalDateTime.of(2026, 11, 19, 23, 59);

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductMovementResponse> expected =
                new PageImpl<>(List.of());

        when(transactionRepository.getProductMovementUntilDate(
                end, pageable))
                .thenReturn(expected);

        Page<ProductMovementResponse> response =
                dashboardService.getProductMovementReport(
                        null, end, pageable);

        assertNotNull(response);

        verify(transactionRepository)
                .getProductMovementUntilDate(end, pageable);
    }

    // 13. Product movement with both dates

    @Test
    void getProductMovementReportWithDateRange() {

        LocalDateTime start =
                LocalDateTime.of(2026, 11, 1, 0, 0);

        LocalDateTime end =
                LocalDateTime.of(2026, 11, 19, 23, 59);

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductMovementResponse> expected =
                new PageImpl<>(List.of());

        when(transactionRepository.getProductMovementBetweenDates(
                start, end, pageable))
                .thenReturn(expected);

        Page<ProductMovementResponse> response =
                dashboardService.getProductMovementReport(
                        start, end, pageable);

        assertNotNull(response);

        verify(transactionRepository)
                .getProductMovementBetweenDates(
                        start, end, pageable);
    }

    // 14. Invalid product movement date range

    @Test
    void getProductMovementReportWithInvalidDateRange() {

        LocalDateTime start =
                LocalDateTime.of(2026, 11, 20, 0, 0);

        LocalDateTime end =
                LocalDateTime.of(2026, 11, 10, 0, 0);

        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> dashboardService.getProductMovementReport(
                        start, end, pageable)
        );

        verifyNoInteractions(transactionRepository);
    }

    private InventoryTransaction createTransaction() {

        InventoryTransaction transaction = new InventoryTransaction();

        setId(transaction, 100L);

        transaction.setProduct(product);
        transaction.setSeller(seller);
        transaction.setPerformedBy(user);
        transaction.setType(TransactionType.IN);
        transaction.setQuantity(5);
        transaction.setNotes("Stock received");

        return transaction;
    }
}
