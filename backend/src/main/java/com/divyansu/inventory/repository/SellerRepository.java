package com.divyansu.inventory.repository;

import com.divyansu.inventory.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}