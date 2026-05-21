package com.example.orderservice.service;

import com.example.orderservice.dto.OrderLineItemsDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.event.OrderPlacedEvent;
import com.example.orderservice.model.Order;
import com.example.orderservice.repository.OrderRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    private OrderService orderService;
    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        orderService = new OrderService(orderRepository, WebClient.builder(), kafkaTemplate);
        ReflectionTestUtils.setField(orderService, "inventoryBaseUrl", mockWebServer.url("/").toString().replaceAll("/$", ""));
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void shouldPlaceOrderWhenAllItemsInStock() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("[{\"skuCode\":\"iphone_13\",\"isInStock\":true}]")
                .addHeader("Content-Type", "application/json"));

        when(orderRepository.save(ArgumentMatchers.any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderRequest request = new OrderRequest(List.of(
                new OrderLineItemsDto("iphone_13", BigDecimal.valueOf(1200), 1)
        ));

        String result = orderService.placeOrder(request).join();

        assertEquals("Order Placed", result);
        verify(orderRepository, times(1)).save(ArgumentMatchers.any(Order.class));
        verify(kafkaTemplate, times(1)).send(ArgumentMatchers.eq("notificationTopic"), ArgumentMatchers.any(OrderPlacedEvent.class));
    }

    @Test
    void shouldFailWhenProductOutOfStock() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("[{\"skuCode\":\"iphone_13\",\"isInStock\":false}]")
                .addHeader("Content-Type", "application/json"));

        OrderRequest request = new OrderRequest(List.of(
                new OrderLineItemsDto("iphone_13", BigDecimal.valueOf(1200), 1)
        ));

        CompletionException exception = assertThrows(CompletionException.class,
                () -> orderService.placeOrder(request).join());

        assertEquals("Product is not in stock, please try again later", exception.getCause().getMessage());
        verify(orderRepository, never()).save(ArgumentMatchers.any(Order.class));
        verify(kafkaTemplate, never()).send(ArgumentMatchers.eq("notificationTopic"), ArgumentMatchers.any(OrderPlacedEvent.class));
    }
}
