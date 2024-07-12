package com.akerumort.OrderManagementService.schedulers;

import com.akerumort.OrderManagementService.dto.OrderCreateDTO;
import com.akerumort.OrderManagementService.entities.Customer;
import com.akerumort.OrderManagementService.entities.Order;
import com.akerumort.OrderManagementService.entities.Product;
import com.akerumort.OrderManagementService.mappers.OrderMapper;
import com.akerumort.OrderManagementService.services.CustomerService;
import com.akerumort.OrderManagementService.services.OrderService;
import com.akerumort.OrderManagementService.services.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class OrderScheduler {

    private static final Logger logger = LogManager.getLogger(OrderScheduler.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    private Random random = new Random();

    @Async
    @Scheduled(fixedRate = 60000) // every 60 sec
    public void createRandomOrder() {
        try {
            List<Customer> customers = customerService.getAllCustomers(0, 100);
            if (customers.isEmpty()) {
                return;
            }
            Customer randomCustomer = customers.get(random.nextInt(customers.size()));

            List<Product> products = new ArrayList<>(productService.getAllProducts(0, 100));
            if (products.isEmpty()) {
                return;
            }

            Collections.shuffle(products);
            List<Long> randomProductIds = products.subList(0, Math.min(3, products.size()))
                    .stream().map(Product::getId).collect(Collectors.toList());

            OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
            orderCreateDTO.setCustomerId(randomCustomer.getId());
            orderCreateDTO.setProductIds(randomProductIds);

            Order order = orderMapper.toEntity(orderCreateDTO);
            orderService.saveOrder(order);

            logger.info("Random order created successfully with ID: {}", order.getId());

        } catch (Exception e) {
            logger.error("Failed to create random order: ", e);
        }
    }
}
