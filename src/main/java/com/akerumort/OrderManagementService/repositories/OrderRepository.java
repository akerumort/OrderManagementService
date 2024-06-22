package com.akerumort.OrderManagementService.repositories;

import com.akerumort.OrderManagementService.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
