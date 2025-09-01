package com.awesomepizza.orderservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.awesomepizza.orderservice.model.OrderStatus;
import com.awesomepizza.orderservice.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findTopByStatusOrderByCreatedAtAsc(OrderStatus status);
    
    Optional<Order> findByCode(String code);
    
    Optional<Order> findTopByStatus(OrderStatus status);
    
    @Query("SELECT o FROM Order o JOIN FETCH o.pizza ORDER BY o.createdAt ASC")
    List<Order> getAll();
    
}
