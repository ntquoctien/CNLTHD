package com.example.inventoryservice;

import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner loadData(InventoryRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(Inventory.builder().skuCode("iphone_13").quantity(100).build());
                repository.save(Inventory.builder().skuCode("iphone_13_red").quantity(0).build());
            }
        };
    }
}
