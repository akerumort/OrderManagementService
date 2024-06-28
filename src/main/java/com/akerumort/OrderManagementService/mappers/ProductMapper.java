package com.akerumort.OrderManagementService.mappers;

import com.akerumort.OrderManagementService.dto.ProductCreateDTO;
import com.akerumort.OrderManagementService.dto.ProductDTO;
import com.akerumort.OrderManagementService.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductCreateDTO productCreateDTO);

    ProductDTO toDTO(Product product);
}
