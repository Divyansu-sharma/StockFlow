
package com.divyansu.inventory.service;

import com.divyansu.inventory.dto.InventoryTransactionRequest;
import com.divyansu.inventory.dto.InventoryTransactionResponse;
import com.divyansu.inventory.entity.*;
import com.divyansu.inventory.exception.InsufficientStockException;
import com.divyansu.inventory.exception.ProductNotFoundException;
import com.divyansu.inventory.exception.SellerNotFoundException;
import com.divyansu.inventory.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryTransactionServiceTest {

    @Mock
    private InventoryTransactionRepository transactionRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InventoryTransactionService transactionService;

    private Product product;
    private User user;
    private Seller seller;
    private InventoryTransactionRequest request;

    private final String userEmail = "admin@stockflow.com";

    // Set generated IDs without adding setters to production entities
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
        product.setQuantity(10);

        user = new User();
        setId(user, 1L);
        user.setEmail(userEmail);

        seller = new Seller();
        setId(seller, 1L);
        seller.setName("ABC Suppliers");
        seller.setEmail("abc@example.com");

        request = new InventoryTransactionRequest();
        request.setProductId(1L);
        request.setType(TransactionType.IN);
        request.setQuantity(5);
        request.setNotes("Stock received");
    }

    @Test
    void createStockInTransactionSuccessfully() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        when(transactionRepository.save(any(InventoryTransaction.class)))
                .thenAnswer(invocation -> {
                    InventoryTransaction transaction =
                            invocation.getArgument(0);
                    setId(transaction, 100L);
                    return transaction;
                });

        InventoryTransactionResponse response =
                transactionService.createTransaction(request, userEmail);

        assertEquals(15, product.getQuantity());
        assertEquals(1L, response.getProductId());
        assertEquals(TransactionType.IN, response.getType());
        assertEquals(5, response.getQuantity());

        verify(productRepository).save(product);
        verify(transactionRepository).save(any(InventoryTransaction.class));
    }

    @Test
    void createStockOutTransactionSuccessfully() {

        request.setType(TransactionType.OUT);
        request.setQuantity(4);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        when(transactionRepository.save(any(InventoryTransaction.class)))
                .thenAnswer(invocation -> {
                    InventoryTransaction transaction =
                            invocation.getArgument(0);
                    setId(transaction, 101L);
                    return transaction;
                });

        InventoryTransactionResponse response =
                transactionService.createTransaction(request, userEmail);

        assertEquals(6, product.getQuantity());
        assertEquals(TransactionType.OUT, response.getType());
        assertEquals(4, response.getQuantity());

        verify(productRepository).save(product);
        verify(transactionRepository).save(any(InventoryTransaction.class));
    }

    @Test
    void createTransactionWithSellerSuccessfully() {

        request.setSellerId(1L);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(seller));

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        when(transactionRepository.save(any(InventoryTransaction.class)))
                .thenAnswer(invocation -> {
                    InventoryTransaction transaction =
                            invocation.getArgument(0);
                    setId(transaction, 102L);
                    return transaction;
                });

        InventoryTransactionResponse response =
                transactionService.createTransaction(request, userEmail);

        assertEquals(1L, response.getSellerId());
        assertEquals("ABC Suppliers", response.getSellerName());

        verify(sellerRepository).findById(1L);
    }

    @Test
    void createTransactionWhenProductDoesNotExist() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> transactionService.createTransaction(request, userEmail)
        );

        verify(transactionRepository, never())
                .save(any(InventoryTransaction.class));
    }

    @Test
    void createTransactionWhenUserDoesNotExist() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalStateException.class,
                () -> transactionService.createTransaction(request, userEmail)
        );

        verify(transactionRepository, never())
                .save(any(InventoryTransaction.class));
    }

    @Test
    void createTransactionWhenSellerDoesNotExist() {

        request.setSellerId(99L);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(sellerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                SellerNotFoundException.class,
                () -> transactionService.createTransaction(request, userEmail)
        );

        verify(transactionRepository, never())
                .save(any(InventoryTransaction.class));
    }

    @Test
    void createStockOutTransactionWithInsufficientStock() {

        request.setType(TransactionType.OUT);
        request.setQuantity(20);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        assertThrows(
                InsufficientStockException.class,
                () -> transactionService.createTransaction(request, userEmail)
        );

        assertEquals(10, product.getQuantity());

        verify(productRepository, never()).save(any(Product.class));
        verify(transactionRepository, never())
                .save(any(InventoryTransaction.class));
    }

    @Test
    void getAllTransactionsSuccessfully() {

        InventoryTransaction transaction = createTransaction();

        Page<InventoryTransaction> page =
                new PageImpl<>(List.of(transaction));

        Pageable pageable = PageRequest.of(0, 10);

        when(transactionRepository.findAll(pageable))
                .thenReturn(page);

        Page<InventoryTransactionResponse> response =
                transactionService.getAllTransactions(pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(1L, response.getContent().get(0).getProductId());
        assertEquals(TransactionType.IN, response.getContent().get(0).getType());
    }

    @Test
    void getTransactionsByProductSuccessfully() {

        InventoryTransaction transaction = createTransaction();

        Pageable pageable = PageRequest.of(0, 10);

        when(productRepository.existsById(1L))
                .thenReturn(true);

        when(transactionRepository.findByProduct_Id(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(transaction)));

        Page<InventoryTransactionResponse> response =
                transactionService.getTransactionsByProduct(1L, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(1L, response.getContent().get(0).getProductId());

        verify(transactionRepository).findByProduct_Id(1L, pageable);
    }

    @Test
    void getTransactionsByProductWhenProductDoesNotExist() {

        Pageable pageable = PageRequest.of(0, 10);

        when(productRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ProductNotFoundException.class,
                () -> transactionService.getTransactionsByProduct(99L, pageable)
        );

        verify(transactionRepository, never())
                .findByProduct_Id(anyLong(), any(Pageable.class));
    }

    @Test
    void filterTransactionsSuccessfully() {

        InventoryTransaction transaction = createTransaction();

        Pageable pageable = PageRequest.of(0, 10);

        when(transactionRepository.findAll(
                ArgumentMatchers.<Specification<InventoryTransaction>>any(),
                eq(pageable)
        )).thenReturn(new PageImpl<>(List.of(transaction)));

        Page<InventoryTransactionResponse> response =
                transactionService.filterTransactions(
                        TransactionType.IN,
                        null,
                        null,
                        null,
                        pageable
                );

        assertEquals(1, response.getTotalElements());
        assertEquals(TransactionType.IN, response.getContent().get(0).getType());
    }

    @Test
    void filterTransactionsWithInvalidDateRange() {

        LocalDateTime startDate = LocalDateTime.of(2026, 11, 20, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 11, 10, 0, 0);

        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.filterTransactions(
                        null,
                        null,
                        startDate,
                        endDate,
                        pageable
                )
        );

        verify(transactionRepository, never()).findAll(
                ArgumentMatchers.<Specification<InventoryTransaction>>any(),
                any(Pageable.class)
        );
    }

    @Test
    void filterTransactionsWithInvalidProductId() {

        Pageable pageable = PageRequest.of(0, 10);

        when(productRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ProductNotFoundException.class,
                () -> transactionService.filterTransactions(
                        null,
                        99L,
                        null,
                        null,
                        pageable
                )
        );
    }

    private InventoryTransaction createTransaction() {

        InventoryTransaction transaction = new InventoryTransaction();

        setId(transaction, 100L);

        transaction.setProduct(product);
        transaction.setPerformedBy(user);
        transaction.setSeller(null);
        transaction.setType(TransactionType.IN);
        transaction.setQuantity(5);
        transaction.setNotes("Stock received");

        return transaction;
    }
}
