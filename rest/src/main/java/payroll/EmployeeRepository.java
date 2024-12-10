package payroll;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.ScopedValue;

interface EmployeeRepository extends JpaRepository<Employee, Long> {

    <T> ScopedValue<T> findById(Long id, Sort sort);
}
