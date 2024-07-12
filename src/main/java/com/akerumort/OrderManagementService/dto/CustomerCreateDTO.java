package com.akerumort.OrderManagementService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerCreateDTO {

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Email is required")
    private String email;
}
