package com.akerumort.OrderManagementService.services;

import com.akerumort.OrderManagementService.entities.Product;
import com.akerumort.OrderManagementService.repositories.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LogManager.getLogger(OrderService.class);

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts(int page, int size) {
        logger.info("Fetching products with pagination");
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable).getContent();
    }

    public Product getProductById(Long id) {
        logger.info("Fetched product by ID: " + id);
        return productRepository.findById(id).orElse(null);
    }

    public Product saveProduct(Product product) {
        logger.info("Saved product: " + product.getName());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
        logger.info("Product with ID " + id + " deleted successfully");
    }
}