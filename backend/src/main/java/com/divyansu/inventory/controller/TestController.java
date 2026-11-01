package com.divyansu.inventory.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String test() {
        return "JWT authentication is working!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin")
    public String adminTest() {
        return "Welcome ADMIN! Role-based authorization is working.";
    }
}