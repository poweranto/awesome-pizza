package com.awesomepizza.orderservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.awesomepizza.orderservice.model.Pizza;
import com.awesomepizza.orderservice.service.PizzaService;

@RestController
@RequestMapping(path="/pizzas")
public class PizzasController {

	@Autowired
	private PizzaService pizzaService;
	
	@GetMapping(path="")
	public List<Pizza> pizzas() {
		return pizzaService.getAll();
	}
	
}
