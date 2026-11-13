
package com.divyansu.inventory.repository;

import com.divyansu.inventory.entity.InventoryTransaction;
import com.divyansu.inventory.entity.TransactionType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
