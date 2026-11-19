
package com.divyansu.inventory.repository;

import com.divyansu.inventory.entity.InventoryTransaction;
import com.divyansu.inventory.entity.TransactionType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.divyansu.inventory.dto.ProductMovementResponse;


import java.time.LocalDateTime;
import java.util.List;

public interface InventoryTransactionRepository
        extends JpaRepository<InventoryTransaction, Long>,
                JpaSpecificationExecutor<InventoryTransaction> {

    Page<InventoryTransaction> findByProduct_Id(
            Long productId, Pageable pageable);

    List<InventoryTransaction> findAllByOrderByCreatedAtDesc(
            Pageable pageable);

    // Total movement without date filters
    @Query("""
        SELECT COALESCE(SUM(t.quantity), 0)
        FROM InventoryTransaction t
        WHERE t.type = :type
        """)
    Long getTotalMovement(
            @Param("type") TransactionType type);

    // Total movement from start date
    @Query("""
        SELECT COALESCE(SUM(t.quantity), 0)
        FROM InventoryTransaction t
        WHERE t.type = :type
        AND t.createdAt >= :startDate
        """)
    Long getTotalMovementFromDate(
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate);

    // Total movement until end date
    @Query("""
        SELECT COALESCE(SUM(t.quantity), 0)
        FROM InventoryTransaction t
        WHERE t.type = :type
        AND t.createdAt <= :endDate
        """)
    Long getTotalMovementUntilDate(
            @Param("type") TransactionType type,
            @Param("endDate") LocalDateTime endDate);

    // Total movement between dates
    @Query("""
        SELECT COALESCE(SUM(t.quantity), 0)
        FROM InventoryTransaction t
        WHERE t.type = :type
        AND t.createdAt >= :startDate
        AND t.createdAt <= :endDate
        """)
    Long getTotalMovementBetweenDates(
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Transaction counts
    long countByType(TransactionType type);

    long countByTypeAndCreatedAtGreaterThanEqual(
            TransactionType type, LocalDateTime startDate);

    long countByTypeAndCreatedAtLessThanEqual(
            TransactionType type, LocalDateTime endDate);

    long countByTypeAndCreatedAtBetween(
            TransactionType type,
            LocalDateTime startDate,
            LocalDateTime endDate);
    
    @Query(
        value = """
            SELECT new com.divyansu.inventory.dto.ProductMovementResponse(
                p.id,
                p.name,
                p.sku,
                COALESCE(SUM(
                    CASE WHEN t.type = com.divyansu.inventory.entity.TransactionType.IN
                    THEN t.quantity ELSE 0 END
                ), 0),
                COALESCE(SUM(
                    CASE WHEN t.type = com.divyansu.inventory.entity.TransactionType.OUT
                    THEN t.quantity ELSE 0 END
                ), 0)
            )
            FROM InventoryTransaction t
            JOIN t.product p
            GROUP BY p.id, p.name, p.sku
            ORDER BY p.name ASC
            """,
        countQuery = """
            SELECT COUNT(DISTINCT t.product.id)
            FROM InventoryTransaction t
            """
    )
    Page<ProductMovementResponse> getProductMovement(Pageable pageable);


    @Query(
        value = """
            SELECT new com.divyansu.inventory.dto.ProductMovementResponse(
                p.id,
                p.name,
                p.sku,
                COALESCE(SUM(
                    CASE WHEN t.type = com.divyansu.inventory.entity.TransactionType.IN
                    THEN t.quantity ELSE 0 END
                ), 0),
                COALESCE(SUM(
                    CASE WHEN t.type = com.divyansu.inventory.entity.TransactionType.OUT
                    THEN t.quantity ELSE 0 END
                ), 0)
            )
            FROM InventoryTransaction t
            JOIN t.product p
            WHERE t.createdAt >= :startDate
            GROUP BY p.id, p.name, p.sku
            ORDER BY p.name ASC
            """,
        countQuery = """
            SELECT COUNT(DISTINCT t.product.id)
            FROM InventoryTransaction t
            WHERE t.createdAt >= :startDate
            """
    )
    Page<ProductMovementResponse> getProductMovementFromDate(
        @Param("startDate") LocalDateTime startDate,
        Pageable pageable
    );


    @Query(
        value = """
            SELECT new com.divyansu.inventory.dto.ProductMovementResponse(
                p.id,
                p.name,
                p.sku,
                COALESCE(SUM(
                    CASE WHEN t.type = com.divyansu.inventory.entity.TransactionType.IN
                    THEN t.quantity ELSE 0 END
                ), 0),
                COALESCE(SUM(
                    CASE WHEN t.type = com.divyansu.inventory.entity.TransactionType.OUT
                    THEN t.quantity ELSE 0 END
                ), 0)
            )
            FROM InventoryTransaction t
            JOIN t.product p
            WHERE t.createdAt <= :endDate
            GROUP BY p.id, p.name, p.sku
            ORDER BY p.name ASC
            """,
        countQuery = """
            SELECT COUNT(DISTINCT t.product.id)
            FROM InventoryTransaction t
            WHERE t.createdAt <= :endDate
            """
    )
    Page<ProductMovementResponse> getProductMovementUntilDate(
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );


    @Query(
        value = """
            SELECT new com.divyansu.inventory.dto.ProductMovementResponse(
                p.id,
                p.name,
                p.sku,
                COALESCE(SUM(
                    CASE WHEN t.type = com.divyansu.inventory.entity.TransactionType.IN
                    THEN t.quantity ELSE 0 END
                ), 0),
                COALESCE(SUM(
                    CASE WHEN t.type = com.divyansu.inventory.entity.TransactionType.OUT
                    THEN t.quantity ELSE 0 END
                ), 0)
            )
            FROM InventoryTransaction t
            JOIN t.product p
            WHERE t.createdAt >= :startDate
            AND t.createdAt <= :endDate
            GROUP BY p.id, p.name, p.sku
            ORDER BY p.name ASC
            """,
        countQuery = """
            SELECT COUNT(DISTINCT t.product.id)
            FROM InventoryTransaction t
            WHERE t.createdAt >= :startDate
            AND t.createdAt <= :endDate
            """
    )
    Page<ProductMovementResponse> getProductMovementBetweenDates(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
}
