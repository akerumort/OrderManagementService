package com.akerumort.OrderManagementService.services;

import com.akerumort.OrderManagementService.entities.Order;
import com.akerumort.OrderManagementService.repositories.CustomerRepository;
import com.akerumort.OrderManagementService.repositories.OrderRepository;
import com.akerumort.OrderManagementService.repositories.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order saveOrder(Order order) {
        // Выполнение бизнес-логики перед сохранением заказа
        validateOrder(order);

        logger.info("Order created successfully for customer ID " + order.getCustomer().getId()
                + " and products: " + order.getProducts());
        return orderRepository.save(order);
    }

    private void validateOrder(Order order) {
        // Проверка наличия покупателя
        if (order.getCustomer() == null || order.getCustomer().getId() == null) {
            logger.error("Customer is required for creating an order. Order creation failed.");
            throw new IllegalArgumentException("Customer is required");
        }

        // Проверка наличия товаров и их наличия на складе
        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            logger.error("At least one product is required for creating an order. Order creation failed.");
            throw new IllegalArgumentException("At least one product is required");
        }

            order.getProducts().forEach(product -> {
            if (product.getId() == null) {
                logger.error("Product ID is required for creating an order. Order creation failed.");
                throw new IllegalArgumentException("Product ID is required");
            }

            // Проверка наличия товара в базе данных
            if (productRepository.findById(product.getId()).isEmpty()) {
                logger.error("Product with ID " + product.getId() + " not found. Order creation failed.");
                throw new IllegalArgumentException("Product not found");
            }
        });

        // Проверка наличия покупателя в базе данных
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
