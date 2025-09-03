package com.awesomepizza.orderservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.awesomepizza.orderservice.model.Order;
import com.awesomepizza.orderservice.service.OrderService;

@RestController
@RequestMapping(path = "/orders")
public class OrdersController {

	@Autowired
	private OrderService orderService;


	@PostMapping(path = "pizza/{id}")
	public Order create(@PathVariable("id") Long pizzaId) {
		return orderService.create(pizzaId);
	}


	@PostMapping(path = "next")
	public ResponseEntity<Order> takeNextOrder() {
	    return orderService.takeNext()
	            .map(order -> ResponseEntity.ok(order))
	            .orElse(ResponseEntity.notFound().build());
	}
	
	
	@PostMapping(path = "{id}/complete")
	public Order complete(@PathVariable("id") Long orderId) {
	    return orderService.complete(orderId);
	}
	
	@GetMapping(path = "")
	public List<Order> orders() {
		return orderService.getAll();
	}
	
	
	
}