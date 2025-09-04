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
			Pizza p1 = new Pizza("Margherita", "pomodoro, mozzarella, basilico");
			Pizza p2 = new Pizza("Diavola", "pomodoro, mozzarella, basilico, salame piccante");
			Pizza p3 = new Pizza("Capricciosa", "pomodoro, mozzarella, funghi, prosciutto cotto, carciofini e olive");
			
			pizzaRepository.save(p1);
			pizzaRepository.save(p2);
			pizzaRepository.save(p3);
		};
	}
}