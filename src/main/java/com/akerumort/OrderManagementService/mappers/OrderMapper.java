package com.akerumort.OrderManagementService.mappers;

import com.akerumort.OrderManagementService.dto.OrderCreateDTO;
import com.akerumort.OrderManagementService.dto.OrderDTO;
import com.akerumort.OrderManagementService.entities.Customer;
import com.akerumort.OrderManagementService.entities.Order;
import com.akerumort.OrderManagementService.entities.Product;
import com.akerumort.OrderManagementService.services.CustomerService;
import com.akerumort.OrderManagementService.services.ProductService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {CustomerService.class, ProductService.class})
public abstract class OrderMapper {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    @Mapping(source = "customerId", target = "customer", qualifiedByName = "mapCustomerIdToCustomer")
    @Mapping(source = "productIds", target = "products", qualifiedByName = "mapProductIdsToProducts")
    @Mapping(target = "orderDate", ignore = true) // дата устанавливается в сервисе
    public abstract Order toEntity(OrderCreateDTO orderCreateDTO);

    public abstract OrderDTO toDTO(Order order);

    @Named("mapProductIdsToProducts")
    List<Product> mapProductIdsToProducts(List<Long> productIds) {
        return productIds.stream().map(productService::getProductById).collect(Collectors.toList());
    }

    @Named("mapCustomerIdToCustomer")
    public Customer mapCustomerIdToCustomer(Long customerId) {
        return customerService.getCustomerById(customerId);
    }
}
