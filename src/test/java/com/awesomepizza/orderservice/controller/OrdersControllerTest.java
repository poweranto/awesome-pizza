package com.awesomepizza.orderservice.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.awesomepizza.orderservice.exceptions.OrderAlreadyInProgressException;
import com.awesomepizza.orderservice.model.Order;
import com.awesomepizza.orderservice.model.OrderStatus;
import com.awesomepizza.orderservice.model.Pizza;
import com.awesomepizza.orderservice.service.OrderService;
import com.awesomepizza.orderservice.service.PizzaService;


@SpringBootTest
@AutoConfigureMockMvc
class OrdersControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OrderService orderService;

	@MockitoBean
	private PizzaService pizzaService;

	private Order testOrder;
	private Pizza testPizza;
	private List<Order> orders;

	@BeforeEach
	void setUp() {
		testPizza = new Pizza("Margherita", "Pomodoro e mozzarella");
		testPizza.setId(1L);
		
		testOrder = new Order();
		testOrder.setId(100L);
		testOrder.setCode("TESTCODE");
		testOrder.setStatus(OrderStatus.PENDING);
		testOrder.setPizza(testPizza);
		testOrder.setCreatedAt(LocalDateTime.now());
		testOrder.setUpdatedAt(LocalDateTime.now());

		orders = allOrders();

	}

	List<Order> allOrders() {

		Order o1 = new Order();
		o1.setCode("CODE_1");
		o1.setId(1L);
		o1.setPizza(testPizza);
		o1.setStatus(OrderStatus.PENDING);

		Order o2 = new Order();
		o2.setCode("CODE_2");
		o2.setId(2L);
		o2.setPizza(testPizza);
		o2.setStatus(OrderStatus.PENDING);

		List<Order> list = new ArrayList<Order>();
		list.add(o1);
		list.add(o2);

		return list;
	}

	List<Order> ordersWithOneInProgress() {

		Order o1 = new Order();
		o1.setCode("CODE_1");
		o1.setId(1L);
		o1.setPizza(testPizza);
		o1.setStatus(OrderStatus.PENDING);

		Order o2 = new Order();
		o2.setCode("CODE_2");
		o2.setId(2L);
		o2.setPizza(testPizza);
		o2.setStatus(OrderStatus.IN_PROGRESS);

		List<Order> list = new ArrayList<Order>();
		list.add(o1);
		list.add(o2);

		return list;
	}

	@Test
	@DisplayName("retrieve all orders")
	void shouldReturnAllOrders() throws Exception {
		when(orderService.getAll()).thenReturn(orders);

		mockMvc.perform(get("/orders")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].status", is(OrderStatus.PENDING.toString())));
	}

	
	@Test
	@DisplayName("create e new order if pizza exists")
	void shouldCreateAndReturnAnOrderIfPizzaExists() throws Exception {
		when(orderService.create(anyLong())).thenReturn(testOrder);

		mockMvc.perform(post("/orders/pizza/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is("TESTCODE")))
				.andExpect(jsonPath("$.status", is(OrderStatus.PENDING.toString())))
				.andExpect(jsonPath("$.pizza.id", is(1)));
	}

	
	@Test
	@DisplayName("retrieve the next order by changing its status to in progress")
	void shouldProcessNextOrder() throws Exception {

		testOrder.setStatus(OrderStatus.IN_PROGRESS);
		when(orderService.takeNext()).thenReturn(Optional.of(testOrder));

		
		mockMvc.perform(post("/orders/next")).andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(OrderStatus.IN_PROGRESS.toString())));
	}

	
	@Test
	@DisplayName("retrieve not found on take next order if there are no orders")
	void shouldReturnsNotFoundIfThereAreNoOrders() throws Exception { 
		when(orderService.takeNext()).thenReturn(Optional.empty());

		mockMvc.perform(post("/orders/next")).andExpect(status().isNotFound());
	}

	
	@Test
	@DisplayName("return conflict if order is already in progress and try to take next order")
	void dddd() throws Exception { 
		when(orderService.takeNext()).thenThrow(OrderAlreadyInProgressException.class);

		assertThrows(OrderAlreadyInProgressException.class, () -> {
			orderService.takeNext();
		});
		
		mockMvc.perform(post("/orders/next")).andExpect(status().isConflict());
	}
	
	@Test
	@DisplayName("return not found if pizza not exists and try to create e new order")
	void retrieveBadRequestIfPizzaNotExistsAndTryToCreateAnOrder() throws Exception { 
		when(orderService.create(anyLong())).thenThrow(ResourceNotFoundException.class);

		assertThrows(ResourceNotFoundException.class, () -> {
			orderService.create(anyLong());
		});
		
		mockMvc.perform(post("/orders/pizza/99")).andExpect(status().isNotFound());
	}
	
	
	@Test
	@DisplayName("return bad request if pizza id is not a valid number and try to create e new order")
	void ddd() throws Exception { 
		when(orderService.create(anyLong())).thenThrow(MethodArgumentTypeMismatchException.class);
		mockMvc.perform(post("/orders/pizza/99")).andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("complete order setting status to completed")
	void shouldCompleteOrder() throws Exception {
		testOrder.setStatus(OrderStatus.COMPLETED);
		when(orderService.complete(anyLong())).thenReturn(testOrder);

		mockMvc.perform(post("/orders/100/complete")).andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(OrderStatus.COMPLETED.toString())))
				.andExpect(jsonPath("$.id", is(100)));
	}
	 
}