package com.example.orderservice.dto;

public record InventoryResponse(
        String skuCode,
        Boolean isInStock
) {
}
