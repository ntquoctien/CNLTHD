package com.example.orderservice.service;

import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderLineItemsDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.event.OrderPlacedEvent;
import com.example.orderservice.exception.OutOfStockException;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItems;
import com.example.orderservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    @Value("${app.inventory-base-url:http://inventory-service}")
    private String inventoryBaseUrl;

    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackPlaceOrder")
    @Retry(name = "inventory")
    @TimeLimiter(name = "inventory")
    public CompletableFuture<String> placeOrder(OrderRequest orderRequest) {
        return CompletableFuture.supplyAsync(() -> {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());

            List<OrderLineItems> orderLineItems = orderRequest.orderLineItemsDtoList()
                    .stream()
                    .map(this::mapToEntity)
                    .toList();

            order.setOrderLineItemsList(orderLineItems);

            List<String> skuCodes = order.getOrderLineItemsList()
                    .stream()
                    .map(OrderLineItems::getSkuCode)
                    .toList();

            InventoryResponse[] inventoryResponses = webClientBuilder.build()
                    .get()
                    .uri(inventoryBaseUrl + "/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            boolean allProductsInStock = inventoryResponses != null
                    && Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

            if (allProductsInStock) {
                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
                return "Order Placed";
            }

            throw new OutOfStockException("Product is not in stock, please try again later");
        });
    }

    public CompletableFuture<String> fallbackPlaceOrder(OrderRequest orderRequest, Throwable throwable) {
        log.error("Inventory fallback triggered: {}", throwable.getMessage());
        return CompletableFuture.completedFuture("Inventory Service is temporarily unavailable. Please try again later.");
    }

    private OrderLineItems mapToEntity(OrderLineItemsDto dto) {
        return OrderLineItems.builder()
                .skuCode(dto.skuCode())
                .price(dto.price())
                .quantity(dto.quantity())
                .build();
    }
}
