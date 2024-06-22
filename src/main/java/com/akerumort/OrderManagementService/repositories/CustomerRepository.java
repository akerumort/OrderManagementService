package com.akerumort.OrderManagementService.repositories;

import com.akerumort.OrderManagementService.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
