package com.akerumort.OrderManagementService.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private List<ProductDTO> products;
    private CustomerDTO customer;
    private Timestamp orderDate;
}
