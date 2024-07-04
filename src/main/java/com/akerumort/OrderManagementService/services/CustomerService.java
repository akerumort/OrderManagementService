package com.akerumort.OrderManagementService.services;

import com.akerumort.OrderManagementService.entities.Customer;
import com.akerumort.OrderManagementService.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private static final Logger logger = LogManager.getLogger(OrderService.class);

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers(int page, int size) {
        logger.info("Fetching customers with pagination");
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return customerPage.getContent();
    }

    public Customer getCustomerById(Long id) {
        logger.info("Fetched customer by ID: " + id);
        return customerRepository.findById(id).orElse(null);
    }

    public Customer saveCustomer(Customer customer) {
        logger.info("Saved customer: " + customer.getName());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
        logger.info("Customer with ID " + id + " deleted successfully");
    }
}