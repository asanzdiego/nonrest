package payroll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * La clase principal de la aplicación de nómina.
 * Esta clase arranca la aplicación Spring Boot.
 */
@SpringBootApplication
public class PayrollApplication {

	/**
	 * El método principal que se ejecuta al iniciar la aplicación.
	 *
	 * @param args los argumentos de la línea de comandos
	 */
	public static void main(String... args) {
		SpringApplication.run(PayrollApplication.class, args);
	}
}