
package com.divyansu.inventory.security;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.divyansu.inventory.service.JwtService;
import com.divyansu.inventory.entity.User;
import com.divyansu.inventory.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import static org.mockito.Mockito.*;
import com.divyansu.inventory.entity.Role;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Test
    void requestWithoutJwtShouldReturn401() throws Exception {

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void requestWithInvalidJwtShouldReturn401() throws Exception {

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithValidAdminJwtShouldReturn200() throws Exception {

        String email = "security-admin-test@stockflow.com";

        userRepository.findByEmail(email)
                .ifPresent(userRepository::delete);

        User user = new User();
        user.setName("Security Admin");
        user.setEmail(email);
        user.setPassword("test-password");
        user.setRole(Role.ADMIN);
        user.setEnabled(true);

        userRepository.save(user);

        String token = jwtService.generateToken(email, "ADMIN");

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void staffShouldNotBeAllowedToDeleteProducts() throws Exception {

        String email = "security-staff-test@stockflow.com";

        userRepository.findByEmail(email)
                .ifPresent(userRepository::delete);

        User user = new User();
        user.setName("Security Staff");
        user.setEmail(email);
        user.setPassword("test-password");
        user.setRole(Role.STAFF);
        user.setEnabled(true);

        userRepository.save(user);

        String token = jwtService.generateToken(email, "STAFF");

        mockMvc.perform(delete("/api/products/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void managerShouldBeAllowedToCreateProducts() throws Exception {

        String email = "security-manager-test@stockflow.com";
        String uniqueSku = "SEC-" + UUID.randomUUID();

        userRepository.findByEmail(email)
                .ifPresent(userRepository::delete);

        User user = new User();
        user.setName("Security Manager");
        user.setEmail(email);
        user.setPassword("test-password");
        user.setRole(Role.MANAGER);
        user.setEnabled(true);

        userRepository.save(user);

        String token = jwtService.generateToken(email, "MANAGER");

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Security Test Product",
                                "sku": "%s",
                                "description": "Testing manager permissions",
                                "price": 100,
                                "quantity": 10,
                                "reorderLevel": 2,
                                "categoryId": 2
                            }
                            """.formatted(uniqueSku)))
                .andExpect(status().isCreated());
    }

    @Test
    void managerShouldNotBeAllowedToDeleteProducts() throws Exception {

        String email = "security-manager-test@stockflow.com";

        User user = userRepository.findByEmail(email).orElseThrow();

        String token = jwtService.generateToken(email, "MANAGER");

        mockMvc.perform(delete("/api/products/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void disabledUserShouldNotAccessProducts() throws Exception {

        String email = "security-disabled-test@stockflow.com";

        userRepository.findByEmail(email)
                .ifPresent(userRepository::delete);

        User user = new User();
        user.setName("Disabled User");
        user.setEmail(email);
        user.setPassword("test-password");
        user.setRole(Role.ADMIN);
        user.setEnabled(false);

        userRepository.save(user);

        String token = jwtService.generateToken(email, "ADMIN");

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void staffShouldBeAllowedToViewProducts() throws Exception {

        String email = "security-staff-test@stockflow.com";

        User user = userRepository.findByEmail(email).orElseThrow();

        String token = jwtService.generateToken(email, "STAFF");

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
