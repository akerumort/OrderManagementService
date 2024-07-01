package com.akerumort.OrderManagementService.controllers;

import com.akerumort.OrderManagementService.dto.ProductCreateDTO;
import com.akerumort.OrderManagementService.dto.ProductDTO;
import com.akerumort.OrderManagementService.entities.Product;
import com.akerumort.OrderManagementService.exceptions.CustomValidationException;
import com.akerumort.OrderManagementService.mappers.ProductMapper;
import com.akerumort.OrderManagementService.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    @Operation(summary = "Get all products", description = "Get a list of all products with pagination")
    public List<ProductDTO> getAllProducts(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        List<Product> productList = productService.getAllProducts(page, size);
        return productList.stream()
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
            @Valid @RequestBody ProductCreateDTO productCreateDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            throw new CustomValidationException("Validation errors: " + errors.toString());
        }
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
            @Valid @RequestBody ProductCreateDTO productCreateDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            throw new CustomValidationException("Validation errors: " + errors.toString());
        }
        Product product = productMapper.toEntity(productCreateDTO);
        Product updatedProduct = productService.saveProduct(product);
        return productMapper.toDTO(updatedProduct);
    }

    @Operation(summary = "Delete a product", description = "Delete a product by ID")
    @DeleteMapping("/{id}")
    public void deleteProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
