package com.awesomepizza.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.awesomepizza.orderservice.model.Pizza;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {
	
}
