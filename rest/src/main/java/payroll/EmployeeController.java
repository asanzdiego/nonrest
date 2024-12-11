
package payroll;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Employee resources. Provides endpoints for
 * standard CRUD operations and includes functionality to return
 * HATEOAS-compliant responses.
 */
@RestController
class EmployeeController {

	private final EmployeeRepository repository;
	private final AbstractScriptDatabaseInitializer abstractScriptDatabaseInitializer;

	/**
	 * Constructor for EmployeeController.
	 *
	 * @param repository                        the repository to manage Employee
	 *                                          entities
	 * @param abstractScriptDatabaseInitializer the database initializer
	 */
	EmployeeController(EmployeeRepository repository,
			AbstractScriptDatabaseInitializer abstractScriptDatabaseInitializer) {
		this.repository = repository;
		this.abstractScriptDatabaseInitializer = abstractScriptDatabaseInitializer;
	}

	/**
	 * Retrieves a collection of all employees as HATEOAS-compliant resources.
	 * Generates links for each employee resource and includes a self-referential
	 * link for the collection.
	 *
	 * @return A CollectionModel containing EntityModel representations of all
	 *         employees, each with its own self-referential link and a link to the
	 *         collection.
	 */
	@GetMapping("/employees")
	CollectionModel<EntityModel<Employee>> all() {

		List<EntityModel<Employee>> employees = repository.findAll().stream()
				.map(employee -> EntityModel.of(employee,
						linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
						linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
				.collect(Collectors.toList());

		return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
	}

	/**
	 * Creates a new employee.
	 *
	 * @param newEmployee the employee to be created
	 * @return the created employee
	 */
	@PostMapping("/employees")
	Employee newEmployee(@RequestBody Employee newEmployee) {
		return repository.save(newEmployee);
	}

	/**
	 * Retrieves a single employee by ID as a HATEOAS-compliant resource.
	 *
	 * @param id the ID of the employee to retrieve
	 * @return an EntityModel containing the employee and its links
	 */
	EntityModel<Employee> one(Long id) {

		Employee employee = repository.findById(id) //
				.orElseThrow(() -> new EmployeeNotFoundException(id));

		return EntityModel.of(employee, //
				linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
				linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
	}

	/**
	 * Updates an existing employee or creates a new one if the employee does not
	 * exist.
	 *
	 * @param newEmployee the employee data to update
	 * @param id          the ID of the employee to update
	 * @return the updated or newly created employee
	 */
	@PutMapping("/employees/{id}")
	Employee updateExistingEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {

		String regex_mail = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

		return repository.findById(id) //
				.map(employee -> {
					employee.setName(newEmployee.getName());
					employee.setRole(newEmployee.getRole());
					return repository.save(employee);
				}) //
				.orElseGet(() -> {
					return repository.save(newEmployee);
				});
	}

	/**
	 * Returns a string representation of the EmployeeController.
	 *
	 * @return a string representation of the EmployeeController
	 */
	@Override
	public String toString() {
		return "EmployeeController{" + "repository=" + repository + ", abstractScriptDatabaseInitializer="
				+ abstractScriptDatabaseInitializer + '}';
	}
		
	}
