package com.awesomepizza.orderservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.awesomepizza.orderservice.model.Pizza;
import com.awesomepizza.orderservice.repository.PizzaRepository;

@Service
public class PizzaService {
	
	@Autowired
	private PizzaRepository pizzaRepository;
	
	public List<Pizza> getAll() {
		return pizzaRepository.findAll();
	}
	
	public Optional<Pizza> getById(Long id) {
		return pizzaRepository.findById(id);
	}
	
}
