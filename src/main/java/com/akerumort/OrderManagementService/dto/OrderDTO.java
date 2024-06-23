package com.akerumort.OrderManagementService.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class OrderDTO {
    private Long id;
    private ProductDTO product;
    private CustomerDTO customer;
    private Timestamp orderDate;
}
