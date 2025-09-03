package com.awesomepizza.orderservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.awesomepizza.orderservice.exceptions.IllegalOrderStateException;
import com.awesomepizza.orderservice.exceptions.OrderAlreadyInProgressException;
import com.awesomepizza.orderservice.model.Order;
import com.awesomepizza.orderservice.model.OrderStatus;
import com.awesomepizza.orderservice.model.Pizza;
import com.awesomepizza.orderservice.repository.OrderRepository;
import com.awesomepizza.orderservice.repository.PizzaRepository;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase
public class OrderServiceTest {

	private Order testOrder;
	private Order completedOrder;
	private Pizza testPizza;

	@MockitoBean
	private OrderRepository orderRepository;

	@MockitoBean
	private PizzaRepository pizzaRepository;

	@InjectMocks
	private OrderService orderService;

	@BeforeEach
	void setUp() {
		testPizza = new Pizza("Margherita", "pomodoro, mozzarella, basilico");
		
		pizzaRepository.save(testPizza);
		
		testOrder = new Order();
		testOrder.setId(1L);
		testOrder.setCode("TESTCODE");
		testOrder.setStatus(OrderStatus.PENDING);
		testOrder.setPizza(testPizza);
		testOrder.setCreatedAt(LocalDateTime.now());
		testOrder.setUpdatedAt(LocalDateTime.now());
		
		completedOrder = new Order();
		completedOrder.setId(1L);
		completedOrder.setCode("TESTCODE");
		completedOrder.setStatus(OrderStatus.COMPLETED);
		completedOrder.setPizza(testPizza);
		completedOrder.setCreatedAt(LocalDateTime.now());
		completedOrder.setUpdatedAt(LocalDateTime.now());

	}

	@Test
	@DisplayName("create an order successfully")
	void shouldCreateOneOrder() {
		when(pizzaRepository.findById(testPizza.getId())).thenReturn(Optional.of(testPizza));
		when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

		Order createdOrder = orderService.create(testPizza.getId());

		when(orderRepository.save(testOrder)).thenReturn(testOrder);
		assertEquals(testOrder, createdOrder);
	}

	@Test
	@DisplayName("throws exception when trying to create an order for a pizza that doesn't exist")
	void shouldThrowsExceptionIfPizzaWasNotFound() {
		long nonExistentPizzaId = 99L;
		when(pizzaRepository.findById(nonExistentPizzaId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			orderService.create(nonExistentPizzaId);
		});

		verify(orderRepository, never()).save(any(Order.class));
	}

	@Test
	@DisplayName("retrieve the next order by changing its status to in progress")
	void shouldProcessNextOrder() {
		testOrder.setStatus(OrderStatus.IN_PROGRESS);
		when(orderRepository.findTopByStatus(OrderStatus.IN_PROGRESS)).thenReturn(Optional.empty());
		when(orderRepository.findTopByStatusOrderByCreatedAtAsc(OrderStatus.PENDING)).thenReturn(Optional.of(testOrder));
		when(orderRepository.save(testOrder)).thenReturn(testOrder);
		
		Optional<Order> nextOrder = orderService.takeNext();
		
		assertNotNull(nextOrder);
		assertEquals(true, nextOrder.isPresent());
		assertEquals(OrderStatus.IN_PROGRESS, nextOrder.get().getStatus());
		verify(orderRepository, atMost(1)).save(testOrder);
	}
	
	
	@Test
	@DisplayName("throws exception when try to take next order while another order is in progress")
	void shouldThrowsExceptionOnTakeNextOrderIfAlreadyInProgressOrderExists() {
		testOrder.setStatus(OrderStatus.IN_PROGRESS);
		when(orderRepository.findTopByStatus(OrderStatus.IN_PROGRESS)).thenReturn(Optional.of(testOrder));
		
		assertThrows(OrderAlreadyInProgressException.class, () -> {
			orderService.takeNext();
		});
		
		verify(orderRepository, never()).findTopByStatusOrderByCreatedAtAsc(OrderStatus.PENDING);
		verify(orderRepository, never()).save(any(Order.class));
	}
	
	
	@Test
	@DisplayName("should complete order")
	void shouldCompleteOrder() {
		testOrder.setStatus(OrderStatus.IN_PROGRESS);
		when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
		when(orderRepository.save(testOrder)).thenReturn(completedOrder);
		
		Order actualOrder = orderService.complete(testOrder.getId());
		
		assertEquals(OrderStatus.COMPLETED, actualOrder.getStatus());
		verify(orderRepository, atMost(1)).save(testOrder);
	}
	
	@Test
	@DisplayName("throws exception when try to complete not in progress order")
	void shouldThrowsExceptionTryToCompleteteNotInPtogressOrder() {
		when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
		
		assertThrows(IllegalOrderStateException.class, () -> {
			orderService.complete(testOrder.getId());
		});
		
		assertNotEquals(OrderStatus.IN_PROGRESS, testOrder.getStatus());
		verify(orderRepository, never()).save(any(Order.class));
	}
	
	@Test
	@DisplayName("throws exception when try to complete not existing order")
	void shouldThrowsExceptionTryToCompleteteNotExistingOrder() {
		when(orderRepository.findById(100L)).thenReturn(Optional.empty());
		
		assertThrows(ResourceNotFoundException.class, () -> {
			orderService.complete(100L);
		});
		
		verify(orderRepository, never()).save(any(Order.class));
	}
	
	@Test
	@DisplayName("retrieve all orders")
	void shouldReturnAllOrders() {
		List<Order> ordersList = new ArrayList<Order>();
		ordersList.add(completedOrder);
		
		when(orderRepository.getAll()).thenReturn(ordersList);
		
		List<Order> orders = orderService.getAll();
		assertEquals(ordersList, orders);
	}

}
