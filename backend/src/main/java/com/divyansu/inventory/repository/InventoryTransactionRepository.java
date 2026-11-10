
package com.divyansu.inventory.repository;

import com.divyansu.inventory.entity.InventoryTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryTransactionRepository
        extends JpaRepository<InventoryTransaction, Long> {

    Page<InventoryTransaction> findByProduct_Id(
            Long productId, Pageable pageable);
}
