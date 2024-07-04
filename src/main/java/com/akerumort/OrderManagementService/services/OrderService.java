package com.akerumort.OrderManagementService.services;

import com.akerumort.OrderManagementService.entities.Order;
import com.akerumort.OrderManagementService.entities.Product;
import com.akerumort.OrderManagementService.repositories.CustomerRepository;
import com.akerumort.OrderManagementService.repositories.OrderRepository;
import com.akerumort.OrderManagementService.repositories.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

@Service
public class OrderService {
    private static final Logger logger = LogManager.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Order> getAllOrders(int page, int size)  {
        logger.info("Fetching orders with pagination");
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable).getContent();
    }

    public Order getOrderById(Long id) {
        logger.info("Fetched order by ID: " + id);
        return orderRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Order not found"));
    }

    public Order saveOrder(Order order) {
        validateOrder(order);
        order.setOrderDate(new Timestamp(System.currentTimeMillis())); // текущая дата и время

        String productsInfo = order.getProducts().stream()
                .map(product -> "ID: " + product.getId())
                .collect(Collectors.joining("; "));

        logger.info("Order created successfully for customer ID " + order.getCustomer().getId()
                + " and products: " + productsInfo);
        return orderRepository.save(order);
    }

    private void validateOrder(Order order) {
        if (order.getCustomer() == null || order.getCustomer().getId() == null) {
            logger.error("Customer is required for creating an order. Order creation failed.");
            throw new IllegalArgumentException("Customer is required");
        }

        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            logger.error("At least one product is required for creating an order. Order creation failed.");
            throw new IllegalArgumentException("At least one product is required");
        }

        order.getProducts().forEach(product -> {
            if (product.getId() == null) {
                logger.error("Product ID is required for creating an order. Order creation failed.");
                throw new IllegalArgumentException("Product ID is required");
            }

            if (productRepository.findById(product.getId()).isEmpty()) {
                logger.error("Product with ID " + product.getId() + " not found. Order creation failed.");
                throw new IllegalArgumentException("Product not found");
            }
        });

        if (customerRepository.findById(order.getCustomer().getId()).isEmpty()) {
            logger.error("Customer with ID " + order.getCustomer().getId() + " not found. Order creation failed.");
            throw new IllegalArgumentException("Customer not found");
        }
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
        logger.info("Order with ID " + id + " deleted successfully");
    }

    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("Order Report\n");
        report.append("Generated at: ").append(Timestamp.valueOf(LocalDateTime.now())).append("\n\n");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage;

        do {
            orderPage = orderRepository.findAll(pageable);
            for (Order order : orderPage.getContent()) {
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
            pageable = orderPage.nextPageable();
        } while (orderPage.hasNext());

        return report.toString();
    }

    public byte[] generatePdfReport() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Order Report")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(18));

        document.add(new Paragraph("Generated at: " + Timestamp.valueOf(LocalDateTime.now()))
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(12));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage;

        do {
            orderPage = orderRepository.findAll(pageable);
            processOrderBatch(orderPage.getContent(), document);
            pageable = orderPage.nextPageable();
        } while (orderPage.hasNext());

        document.close();
        return out.toByteArray();
    }

    private void processOrderBatch(List<Order> orders, Document document) {
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
    }

    public byte[] generateExcelReport() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders Report");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Order ID");
        header.createCell(1).setCellValue("Customer ID");
        header.createCell(2).setCellValue("Product IDs");
        header.createCell(3).setCellValue("Product Names");
        header.createCell(4).setCellValue("Order Date");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage;
        int rowIdx = 1;

        do {
            orderPage = orderRepository.findAll(pageable);
            rowIdx = processOrderBatch(orderPage.getContent(), sheet, rowIdx);
            pageable = orderPage.nextPageable();
        } while (orderPage.hasNext());

        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    private int processOrderBatch(List<Order> orders, Sheet sheet, int rowIdx) {
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
        return rowIdx;
    }

}
