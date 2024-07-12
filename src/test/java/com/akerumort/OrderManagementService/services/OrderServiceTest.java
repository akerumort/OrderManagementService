package com.akerumort.OrderManagementService.services;

import com.akerumort.OrderManagementService.entities.Customer;
import com.akerumort.OrderManagementService.entities.Order;
import com.akerumort.OrderManagementService.entities.Product;
import com.akerumort.OrderManagementService.repositories.CustomerRepository;
import com.akerumort.OrderManagementService.repositories.OrderRepository;
import com.akerumort.OrderManagementService.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private Customer customer;
    private Product product;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
        customer.setId(1L);

        product = new Product();
        product.setId(1L);

        order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setProducts(Collections.singletonList(product));
    }

    @Test
    public void testGetAllOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(Collections.singletonList(order));

        when(orderRepository.findAll(pageable)).thenReturn(orderPage);

        assertEquals(1, orderService.getAllOrders(0, 10).size());
        verify(orderRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetOrderById() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        Order foundOrder = orderService.getOrderById(1L);
        assertNotNull(foundOrder);
        assertEquals(order.getId(), foundOrder.getId());
    }

    @Test
    public void testSaveOrder() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order savedOrder = orderService.saveOrder(order);
        assertNotNull(savedOrder);
        assertEquals(order.getId(), savedOrder.getId());
    }

    @Test
    public void testDeleteOrder() {
        doNothing().when(orderRepository).deleteById(anyLong());

        orderService.deleteOrder(1L);
        verify(orderRepository, times(1)).deleteById(anyLong());
    }
}
