package com.akerumort.OrderManagementService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name can't be longer than 255 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email can't be longer than 255 characters")
    private String email;
}
