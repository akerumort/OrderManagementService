package com.akerumort.OrderManagementService.controllers;

import com.akerumort.OrderManagementService.dto.OrderDTO;
import com.akerumort.OrderManagementService.entities.Order;
import com.akerumort.OrderManagementService.mappers.OrderMapper;
import com.akerumort.OrderManagementService.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

        order = new Order();
        order.setId(1L);

        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
    }

    @Test
    public void testGetAllOrders() throws Exception {
        when(orderService.getAllOrders(anyInt(), anyInt())).thenReturn(Collections.singletonList(order));
        when(orderMapper.toDTO(any(Order.class))).thenReturn(orderDTO);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(orderService, times(1)).getAllOrders(anyInt(), anyInt());
    }

    @Test
    public void testGetOrderById() throws Exception {
        when(orderService.getOrderById(anyLong())).thenReturn(order);
        when(orderMapper.toDTO(any(Order.class))).thenReturn(orderDTO);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(orderService, times(1)).getOrderById(anyLong());
    }
}
