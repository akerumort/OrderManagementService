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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
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
        Order updatedOrder = orderMapper.toEntity(orderCreateDTO);
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

    @GetMapping("/report/pdf")
    @Operation(summary = "Get orders report in PDF", description = "Generate a PDF report of all completed orders")
    public ResponseEntity<byte[]> generatePdfReport() {
        logger.info("Generating PDF orders report...");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            List<Order> orders = orderService.getAllOrders();

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Order Report")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(18));

            document.add(new Paragraph("Generated at: " + Timestamp.valueOf(LocalDateTime.now()))
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(12));

            for (Order order : orders) {
                document.add(new Paragraph("Order ID: " + order.getId()));
                document.add(new Paragraph("Customer ID: " + order.getCustomer().getId()));
                String productIds = order.getProducts().stream()
                        .map(product -> String.valueOf(product.getId()))
                        .collect(Collectors.joining(", "));
                document.add(new Paragraph("Product IDs: " + productIds));
                String productNames = order.getProducts().stream()
                        .map(Product::getName)
                        .collect(Collectors.joining(", "));
                document.add(new Paragraph("Product Names: " + productNames));
                document.add(new Paragraph("Order Date: " + order.getOrderDate()));
                document.add(new Paragraph(" "));
            }

            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders_report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(out.toByteArray());

        } catch (Exception e) {
            logger.error("Error generating PDF report", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/report/excel")
    @Operation(summary = "Get orders report in Excel", description = "Generate an Excel report of all completed orders")
    public ResponseEntity<byte[]> generateExcelReport() {
        logger.info("Generating Excel orders report...");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            List<Order> orders = orderService.getAllOrders();

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Orders Report");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Order ID");
            header.createCell(1).setCellValue("Customer ID");
            header.createCell(2).setCellValue("Product IDs");
            header.createCell(3).setCellValue("Product Names");
            header.createCell(4).setCellValue("Order Date");

            int rowIdx = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(order.getId());
                row.createCell(1).setCellValue(order.getCustomer().getId());
                String productIds = order.getProducts().stream()
                        .map(product -> String.valueOf(product.getId()))
                        .collect(Collectors.joining(", "));
                row.createCell(2).setCellValue(productIds);
                String productNames = order.getProducts().stream()
                        .map(Product::getName)
                        .collect(Collectors.joining(", "));
                row.createCell(3).setCellValue(productNames);
                row.createCell(4).setCellValue(order.getOrderDate().toString());
            }

            workbook.write(out);
            workbook.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders_report.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(out.toByteArray());

        } catch (IOException e) {
            logger.error("Error generating Excel report", e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
