package com.akerumort.OrderManagementService.repositories;

import com.akerumort.OrderManagementService.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
