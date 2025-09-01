package com.awesomepizza.orderservice.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.awesomepizza.orderservice.exceptions.IllegalOrderStateException;
import com.awesomepizza.orderservice.exceptions.OrderAlreadyInProgressException;
import com.awesomepizza.orderservice.model.Order;
import com.awesomepizza.orderservice.model.OrderStatus;
import com.awesomepizza.orderservice.model.Pizza;
import com.awesomepizza.orderservice.repository.OrderRepository;
import com.awesomepizza.orderservice.repository.PizzaRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private PizzaRepository pizzaRepository;

	public Order create(Long pizzaId) {
		Pizza pizza = pizzaRepository.findById(pizzaId)
				.orElseThrow(() -> new ResourceNotFoundException("Pizza with id " + pizzaId + " not found"));

		Order newOrder = new Order();
		newOrder.setPizza(pizza);
		newOrder.setStatus(OrderStatus.PENDING);
		newOrder.setCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

		return orderRepository.save(newOrder);
	}

	/**
	 * Sets the order status to IN_PROGRESS, considering the oldest PENDING order.
	 * @return {@code Order} if present
     * @throws OrderAlreadyInProgressException if another {@code Order} is already being prepared
	 */
	public Optional<Order> takeNext() {
		Optional<Order> next = orderRepository.findTopByStatus(OrderStatus.IN_PROGRESS);
		if(next.isPresent()) {
			throw new OrderAlreadyInProgressException("Unable to take a new order, an order is already being prepared.");
		}
		
		
		return orderRepository.findTopByStatusOrderByCreatedAtAsc(OrderStatus.PENDING).map(order -> {
			order.setStatus(OrderStatus.IN_PROGRESS);
			return orderRepository.save(order);
		});
	}

	public Order complete(Long orderId) {
		
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));

		
		if(order.getStatus() != OrderStatus.IN_PROGRESS) {
			throw new IllegalOrderStateException("Only orders in the in progress status can be completed");
		}
		
		order.setStatus(OrderStatus.COMPLETED);
		return orderRepository.save(order);
	}

	public List<Order> getAll() {
		return orderRepository.getAll();
	}
}
