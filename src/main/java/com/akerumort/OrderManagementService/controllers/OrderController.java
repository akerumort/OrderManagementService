package com.akerumort.OrderManagementService.controllers;

import com.akerumort.OrderManagementService.dto.OrderCreateDTO;
import com.akerumort.OrderManagementService.dto.OrderDTO;
import com.akerumort.OrderManagementService.entities.Order;
import com.akerumort.OrderManagementService.entities.Product;
import com.akerumort.OrderManagementService.exceptions.CustomValidationException;
import com.akerumort.OrderManagementService.mappers.OrderMapper;
import com.akerumort.OrderManagementService.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LogManager.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @GetMapping
    @Operation(summary = "Get all orders", description = "Get a list of all orders with pagination")
    public List<OrderDTO> getAllOrders(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        List<Order> orderList = orderService.getAllOrders(page, size);
        return orderList.stream()
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
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            throw new CustomValidationException("Validation errors: " + errors.toString());
        }
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
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            throw new CustomValidationException("Validation errors: " + errors.toString());
        }
        Order existingOrder = orderService.getOrderById(id);
        if (existingOrder == null) {
            throw new CustomValidationException("Order with ID " + id + " does not exist");
        }

        Order updatedOrder = orderMapper.toEntity(orderCreateDTO);
        updatedOrder.setId(id);
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
    @Operation(summary = "Get orders report", description = "Generate a report of all completed orders with pagination")
    public String generateReport(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Generating orders report...");

        try {
            List<Order> allOrders = orderService.getAllOrders(page, size);

            StringBuilder report = new StringBuilder();
            report.append("Order Report\n");
            report.append("Generated at: ").append(Timestamp.valueOf(LocalDateTime.now())).append("\n\n");

            for (Order order : allOrders) {
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

    @GetMapping("/report/pdf")
    @Operation(summary = "Get orders report in PDF", description = "Generate a PDF report of all completed orders with pagination")
    public ResponseEntity<byte[]> generatePdfReport(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Generating PDF orders report...");

        try {
            List<Order> allOrders = orderService.getAllOrders(page, size);
            byte[] pdfReport = orderService.generatePdfReport(allOrders);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders_report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfReport);

        } catch (Exception e) {
            logger.error("Error generating PDF report", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/report/excel")
    @Operation(summary = "Get orders report in Excel", description = "Generate an Excel report of all completed orders with pagination")
    public ResponseEntity<byte[]> generateExcelReport(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Generating Excel orders report...");

        try {
            List<Order> allOrders = orderService.getAllOrders(page, size);
            byte[] excelReport = orderService.generateExcelReport(allOrders);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders_report.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelReport);

        } catch (IOException e) {
            logger.error("Error generating Excel report", e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
