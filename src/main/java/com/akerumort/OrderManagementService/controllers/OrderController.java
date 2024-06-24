package com.akerumort.OrderManagementService.controllers;

import com.akerumort.OrderManagementService.dto.OrderDTO;
import com.akerumort.OrderManagementService.entities.Order;
import com.akerumort.OrderManagementService.mappers.OrderMapper;
import com.akerumort.OrderManagementService.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @GetMapping
    @Operation(summary = "Get all orders", description = "Get a list of all orders")
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Get details of a specific order by ID")
    public OrderDTO getOrderById(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return orderMapper.toDTO(order);
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Create a new order with unique ID")
    public OrderDTO createOrder(
            @Parameter(description = "Order details", required = true)
            @RequestBody OrderDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);
        Order savedOrder = orderService.saveOrder(order);
        return orderMapper.toDTO(savedOrder);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing order", description = "Update an existing order by ID")
    public OrderDTO updateOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Update order details", required = true)
            @RequestBody OrderDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);
        order.setId(id);
        Order updatedOrder = orderService.saveOrder(order);
        return orderMapper.toDTO(updatedOrder);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order", description = "Delete an order by ID")
    public void deleteOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
