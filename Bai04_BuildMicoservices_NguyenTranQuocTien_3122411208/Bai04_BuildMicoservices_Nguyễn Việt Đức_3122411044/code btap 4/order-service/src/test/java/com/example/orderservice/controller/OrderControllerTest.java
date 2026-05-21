package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void shouldPlaceOrderSuccessfully() throws Exception {
        String body = """
                {
                  \"orderLineItemsDtoList\": [
                    {
                      \"skuCode\": \"iphone_13\",
                      \"price\": 1200,
                      \"quantity\": 1
                    }
                  ]
                }
                """;

        when(orderService.placeOrder(ArgumentMatchers.any(OrderRequest.class)))
                .thenReturn(CompletableFuture.completedFuture("Order Placed"));

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().string("Order Placed"));
    }

    @Test
    void shouldReturnBadRequestWhenOutOfStock() throws Exception {
        String body = """
                {
                  \"orderLineItemsDtoList\": [
                    {
                      \"skuCode\": \"iphone_13\",
                      \"price\": 1200,
                      \"quantity\": 1
                    }
                  ]
                }
                """;

        when(orderService.placeOrder(ArgumentMatchers.any(OrderRequest.class)))
          .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Product is not in stock, please try again later")));

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
          .andExpect(status().isBadRequest());
    }
}
