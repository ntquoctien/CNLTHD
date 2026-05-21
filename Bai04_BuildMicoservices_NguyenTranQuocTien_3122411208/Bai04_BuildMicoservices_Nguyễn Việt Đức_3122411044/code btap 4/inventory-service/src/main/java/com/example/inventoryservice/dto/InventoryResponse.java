package com.example.inventoryservice.dto;

public record InventoryResponse(
        String skuCode,
        Boolean isInStock
) {
}
