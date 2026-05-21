package com.example.inventoryservice.controller;

import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryRepository inventoryRepository;

    @BeforeEach
    void setUp() {
        inventoryRepository.deleteAll();
        inventoryRepository.save(Inventory.builder().skuCode("iphone_13").quantity(100).build());
        inventoryRepository.save(Inventory.builder().skuCode("iphone_13_red").quantity(0).build());
    }

    @Test
    void shouldReturnTrueWhenInStock() throws Exception {
        mockMvc.perform(get("/api/inventory").param("skuCode", "iphone_13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].skuCode").value("iphone_13"))
                .andExpect(jsonPath("$[0].isInStock").value(true));
    }

    @Test
    void shouldReturnFalseWhenOutOfStock() throws Exception {
        mockMvc.perform(get("/api/inventory").param("skuCode", "iphone_13_red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].skuCode").value("iphone_13_red"))
                .andExpect(jsonPath("$[0].isInStock").value(false));
    }
}
