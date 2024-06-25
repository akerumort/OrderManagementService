package com.akerumort.OrderManagementService.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private Long id;
    private CustomerDTO customer;
    private List<ProductDTO> products;
    private Timestamp orderDate;
}
