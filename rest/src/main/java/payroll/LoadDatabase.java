package payroll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to preload the database with initial data.
 */
@Configuration
class LoadDatabase {

	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	/**
	 * Bean to initialize the database with predefined employees.
	 *
	 * @param repository the repository used to save employees
	 * @return a CommandLineRunner to preload the database
	 */
	@Bean
	CommandLineRunner initDatabase(EmployeeRepository repository) {

		return args -> {
			log.info("Preloading " + repository.save(new Employee("Bilbo Baggins", "burglar")));
			log.info("Preloading " + repository.save(new Employee("Frodo Baggins", "thief")));
		};
	}
}