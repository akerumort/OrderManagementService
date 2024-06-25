package com.akerumort.OrderManagementService.controllers;

import com.akerumort.OrderManagementService.dto.ProductDTO;
import com.akerumort.OrderManagementService.entities.Product;
import com.akerumort.OrderManagementService.mappers.ProductMapper;
import com.akerumort.OrderManagementService.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    @Operation(summary = "Get all products", description = "Get a list of all products")
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Get details of a specific product by ID")
    public ProductDTO getProductById(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        Product product = productService.getProductById(id);
        return productMapper.toDTO(product);
    }

    @PostMapping
    @Operation(summary = "Create a new product", description = "Create a new product with unique ID")
    public ProductDTO createProduct(
            @Parameter(description = "Product details", required = true)
            @RequestBody ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productService.saveProduct(product);
        return productMapper.toDTO(savedProduct);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product", description = "Update an existing product by ID")
    public ProductDTO updateProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated product details", required = true)
            @RequestBody ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        product.setId(id);
        Product updatedProduct = productService.saveProduct(product);
        return productMapper.toDTO(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Delete a product by ID")
    public void deleteProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
