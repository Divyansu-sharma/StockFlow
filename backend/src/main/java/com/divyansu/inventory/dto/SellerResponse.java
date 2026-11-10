package com.divyansu.inventory.dto;

import java.time.LocalDateTime;

public class SellerResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime createdAt;

    public SellerResponse(Long id, String name, String email,
                          String phone, String address, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}