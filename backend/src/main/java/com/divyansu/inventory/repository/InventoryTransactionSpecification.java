
package com.divyansu.inventory.repository;

import com.divyansu.inventory.entity.InventoryTransaction;
import com.divyansu.inventory.entity.TransactionType;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventoryTransactionSpecification {

    public static Specification<InventoryTransaction> filterTransactions(
            TransactionType type,
            Long productId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (type != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("type"), type)
                );
            }

            if (productId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("product").get("id"), productId)
                );
            }

            if (startDate != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("createdAt"), startDate)
                );
            }

            if (endDate != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("createdAt"), endDate)
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}
