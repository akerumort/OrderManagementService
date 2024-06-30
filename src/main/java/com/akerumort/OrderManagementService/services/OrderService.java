package com.akerumort.OrderManagementService.services;

import com.akerumort.OrderManagementService.entities.Order;
import com.akerumort.OrderManagementService.repositories.CustomerRepository;
import com.akerumort.OrderManagementService.repositories.OrderRepository;
import com.akerumort.OrderManagementService.repositories.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger logger = LogManager.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Order> getAllOrders() {
        logger.info("Fetched all orders");
        return orderRepository.findAll();
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
}
