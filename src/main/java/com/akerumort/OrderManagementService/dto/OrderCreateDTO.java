package com.akerumort.OrderManagementService.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateDTO {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotEmpty(message = "Product IDs are required")
    private List<Long> productIds;
}
