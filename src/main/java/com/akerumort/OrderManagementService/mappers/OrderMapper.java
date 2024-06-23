package com.akerumort.OrderManagementService.mappers;

import com.akerumort.OrderManagementService.dto.OrderDTO;
import com.akerumort.OrderManagementService.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, CustomerMapper.class})
public interface OrderMapper {
    @Mapping(source = "product", target = "product")
    @Mapping(source = "customer", target = "customer")
    OrderDTO toDTO(Order order);

    @Mapping(source = "product", target = "product")
    @Mapping(source = "customer", target = "customer")
    Order toEntity(OrderDTO orderDTO);
}
