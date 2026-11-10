package com.divyansu.inventory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SellerRequest {

    @NotBlank(message = "Seller name is required")
    @Size(max = 150, message = "Seller name cannot exceed 150 characters")
    private String name;

    @NotBlank(message = "Seller email is required")
    @Email(message = "Please provide a valid email")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    private String phone;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}