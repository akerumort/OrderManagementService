package com.akerumort.OrderManagementService.controllers;

import com.akerumort.OrderManagementService.dto.CustomerDTO;
import com.akerumort.OrderManagementService.entities.Customer;
import com.akerumort.OrderManagementService.mappers.CustomerMapper;
import com.akerumort.OrderManagementService.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerMapper customerMapper;

    @GetMapping
    @Operation(summary = "Get all customers", description = "Get a list of all customers")
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers().stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Get details of a specific customer by ID")
    public CustomerDTO getCustomerById(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        return customerMapper.toDTO(customer);
    }

    @PostMapping
    @Operation(summary = "Create a new customer", description = "Create a new customer with unique ID")
    public CustomerDTO createCustomer(
            @Parameter(description = "Customer details", required = true)
            @Valid @RequestBody CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerService.saveCustomer(customer);
        return customerMapper.toDTO(savedCustomer);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing customer", description = "Update an existing customer by ID")
    public CustomerDTO updateCustomer(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated customer details", required = true)
            @Valid @RequestBody CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);
        customer.setId(id);
        Customer updatedCustomer = customerService.saveCustomer(customer);
        return customerMapper.toDTO(updatedCustomer);
    }

    @Operation(summary = "Delete a customer", description = "Delete a customer by ID")
    @DeleteMapping("/{id}")
    public void deleteCustomer(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
}
