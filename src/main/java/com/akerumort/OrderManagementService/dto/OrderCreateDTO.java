package com.akerumort.OrderManagementService.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateDTO {
    private Long customerId;
    private List<Long> productIds;
}
