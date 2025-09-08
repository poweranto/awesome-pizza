package com.awesomepizza.orderservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.awesomepizza.orderservice.model.Pizza;
import com.awesomepizza.orderservice.repository.PizzaRepository;

@Configuration
public class LoadDatabase {

	// private static final Logger log =
	// LoggerFactory.getLogger(LoadDatabase.class);

	@Bean
	CommandLineRunner initDatabase(PizzaRepository pizzaRepository) {

		// to avoid constraint exception
		if(pizzaRepository.count() > 0) {
			return args -> {};
		}
		
		return args -> {
			Pizza p1 = new Pizza("Margherita", "pomodoro, mozzarella e basilico");
			Pizza p2 = new Pizza("Marinara", "pomodoro, aglio e origano");
			Pizza p3 = new Pizza("Quattro Stagioni", "pomodoro, mozzarella, funghi, prosciutto cotto, carciofini e olive");
			Pizza p4 = new Pizza("Quattro Formaggi", "mozzarella, gorgonzola, fontina e parmigiano");
			Pizza p5 = new Pizza("Capricciosa", "pomodoro, mozzarella, funghi, prosciutto cotto, carciofini e olive");
			Pizza p6 = new Pizza("Diavola", "pomodoro, mozzarella e salame piccante");
			Pizza p7 = new Pizza("Regina", "pomodoro, mozzarella, prosciutto cotto e funghi champignon");
			Pizza p8 = new Pizza("Napoli", "pomodoro, mozzarella, acciughe e origano");
			
			pizzaRepository.save(p1);
			pizzaRepository.save(p2);
			pizzaRepository.save(p3);
			pizzaRepository.save(p4);
			pizzaRepository.save(p5);
			pizzaRepository.save(p6);
			pizzaRepository.save(p7);
			pizzaRepository.save(p8);
		};
	}
}