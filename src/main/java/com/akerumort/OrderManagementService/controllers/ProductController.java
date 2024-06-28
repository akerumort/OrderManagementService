package com.akerumort.OrderManagementService.controllers;

import com.akerumort.OrderManagementService.dto.ProductCreateDTO;
import com.akerumort.OrderManagementService.dto.ProductDTO;
import com.akerumort.OrderManagementService.entities.Product;
import com.akerumort.OrderManagementService.mappers.ProductMapper;
import com.akerumort.OrderManagementService.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
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
    @Operation(summary = "Create a new product", description = "Create a new product")
    public ProductDTO createProduct(
            @Parameter(description = "Product details", required = true)
            @Valid @RequestBody ProductCreateDTO productCreateDTO) {
        Product product = productMapper.toEntity(productCreateDTO);
        Product savedProduct = productService.saveProduct(product);
        return productMapper.toDTO(savedProduct);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product", description = "Update an existing product by ID")
    public ProductDTO updateProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated product details", required = true)
            @Valid @RequestBody ProductCreateDTO productCreateDTO) {
        Product existingProduct = productService.getProductById(id);
        Product updatedProduct = productMapper.toEntity(productCreateDTO);
        updatedProduct.setId(existingProduct.getId());
        Product savedProduct = productService.saveProduct(updatedProduct);
        return productMapper.toDTO(savedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Delete a product by ID")
    public void deleteProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
