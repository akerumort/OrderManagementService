package com.akerumort.OrderManagementService.mappers;

import com.akerumort.OrderManagementService.dto.CustomerCreateDTO;
import com.akerumort.OrderManagementService.dto.CustomerDTO;
import com.akerumort.OrderManagementService.entities.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    Customer toEntity(CustomerCreateDTO customerCreateDTO);

    CustomerDTO toDTO(Customer customer);
}
