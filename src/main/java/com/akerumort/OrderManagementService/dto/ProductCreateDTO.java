package com.akerumort.OrderManagementService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.math.BigDecimal;

@Data
public class ProductCreateDTO {

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    private BigDecimal price;
}
