package com.akerumort.OrderManagementService.controllers;

import com.akerumort.OrderManagementService.dto.OrderCreateDTO;
import com.akerumort.OrderManagementService.dto.OrderDTO;
import com.akerumort.OrderManagementService.entities.Order;
import com.akerumort.OrderManagementService.entities.Product;
import com.akerumort.OrderManagementService.exceptions.CustomValidationException;
import com.akerumort.OrderManagementService.mappers.OrderMapper;
import com.akerumort.OrderManagementService.services.OrderService;
import com.akerumort.OrderManagementService.utils.ValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LogManager.getLogger(OrderController.class);

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
    @Operation(summary = "Create a new order", description = "Create a new order with customer and products")
    public OrderDTO createOrder(
            @Parameter(description = "Order details", required = true)
            @Valid @RequestBody OrderCreateDTO orderCreateDTO, BindingResult bindingResult) {
        ValidationUtil.validateBindingResult(bindingResult);
        Order order = orderMapper.toEntity(orderCreateDTO);
        Order savedOrder = orderService.saveOrder(order);
        return orderMapper.toDTO(savedOrder);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing order", description = "Update an existing order by ID")
    public OrderDTO updateOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated order details", required = true)
            @Valid @RequestBody OrderCreateDTO orderCreateDTO, BindingResult bindingResult) {
        ValidationUtil.validateBindingResult(bindingResult);
        Order existingOrder = orderService.getOrderById(id);
        Order updatedOrder = orderMapper.toEntity(orderCreateDTO);
        updatedOrder.setId(existingOrder.getId());
        Order savedOrder = orderService.saveOrder(updatedOrder);
        return orderMapper.toDTO(savedOrder);
    }

    @Operation(summary = "Delete an order", description = "Delete an order by ID")
    @DeleteMapping("/{id}")
    public void deleteOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        orderService.deleteOrder(id);
    }

    @GetMapping("/report")
    @Operation(summary = "Get orders report", description = "Generate a report of all completed orders")
    public String generateReport() {
        logger.info("Generating orders report...");

        try {
            List<Order> orders = orderService.getAllOrders();
            StringBuilder report = new StringBuilder();
            report.append("Order Report\n");
            report.append("Generated at: ").append(Timestamp.valueOf(LocalDateTime.now())).append("\n\n");

            for (Order order : orders) {
                report.append("Order ID: ").append(order.getId()).append("\n");
                report.append("Customer ID: ").append(order.getCustomer().getId()).append("\n");
                report.append("Product IDs: ");
                String productIds = order.getProducts().stream()
                        .map(product -> String.valueOf(product.getId()))
                        .collect(Collectors.joining(", "));
                report.append(productIds).append("\n");
                report.append("Product Names: ");
                String productNames = order.getProducts().stream()
                        .map(Product::getName)
                        .collect(Collectors.joining(", "));
                report.append(productNames).append("\n");
                report.append("Order Date: ").append(order.getOrderDate()).append("\n\n");
            }

            logger.info("Report generated successfully");
            return report.toString();
        } catch (Exception e) {
            logger.error("Error generating report", e);
            return "Error generating report: " + e.getMessage();
        }
    }
}
