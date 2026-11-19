
package com.divyansu.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.divyansu.inventory.dto.RegisterRequest;
import com.divyansu.inventory.dto.LoginRequest;
import com.divyansu.inventory.entity.Role;
import com.divyansu.inventory.entity.User;
import com.divyansu.inventory.repository.UserRepository;
import com.divyansu.inventory.service.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.STAFF);
    }

    // 1. Successful registration

    @Test
    void registerSuccessfully() {

        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("Password123");

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Password123"))
                .thenReturn("encodedPassword");

        ResponseEntity<?> response = authController.register(request);

        assertEquals(201, response.getStatusCode().value());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("Password123");
    }

    // 2. Registration with duplicate email

    @Test
    void registerWithDuplicateEmail() {

        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("Password123");

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);

        ResponseEntity<?> response = authController.register(request);

        assertEquals(409, response.getStatusCode().value());

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    // 3. Successful login

    @Test
    void loginSuccessfully() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password123");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("Password123", "encodedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken("test@example.com", "STAFF"))
                .thenReturn("test.jwt.token");

        ResponseEntity<?> response = authController.login(request);

        assertEquals(200, response.getStatusCode().value());

        verify(jwtService).generateToken("test@example.com", "STAFF");
    }

    // 4. Login with incorrect password

    @Test
    void loginWithIncorrectPassword() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("WrongPassword");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("WrongPassword", "encodedPassword"))
                .thenReturn(false);

        ResponseEntity<?> response = authController.login(request);

        assertEquals(401, response.getStatusCode().value());

        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    // 5. Login with unknown email

    @Test
    void loginWithUnknownEmail() {

        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("Password123");

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(request);

        assertEquals(401, response.getStatusCode().value());

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }

    // 6. Login with disabled account

    @Test
    void loginWithDisabledAccount() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password123");

        user.setEnabled(false);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("Password123", "encodedPassword"))
                .thenReturn(true);

        ResponseEntity<?> response = authController.login(request);

        assertEquals(403, response.getStatusCode().value());

        verifyNoInteractions(jwtService);
    }
}
