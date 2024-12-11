
package payroll;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRetrieveAllEmployees() throws Exception {
        Employee employee1 = new Employee("John Doe", "Developer");
        employee1.setId(1L);
        Employee employee2 = new Employee("Jane Smith", "Manager");
        employee2.setId(2L);

        when(employeeRepository.findAll()).thenReturn(List.of(employee1, employee2));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.employeeList[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.employeeList[0].name", is("John Doe")))
                .andExpect(jsonPath("$._embedded.employeeList[0].role", is("Developer")))
                .andExpect(jsonPath("$._embedded.employeeList[1].id", is(2)))
                .andExpect(jsonPath("$._embedded.employeeList[1].name", is("Jane Smith")))
                .andExpect(jsonPath("$._embedded.employeeList[1].role", is("Manager")));

        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void shouldRetrieveSingleEmployee() throws Exception {
        Employee employee = new Employee("John Doe", "Developer");
        employee.setId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.role", is("Developer")));

        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNotFoundForNonExistingEmployee() throws Exception {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isNotFound());

        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void shouldUpdateExistingEmployee() throws Exception {
        Employee existingEmployee = new Employee("John Doe", "Developer");
        existingEmployee.setId(1L);
        Employee updatedEmployee = new Employee("John Doe", "Senior Developer");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        mockMvc.perform(put("/employees/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.role", is("Senior Developer")));

        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void shouldDeleteEmployee() throws Exception {
        doNothing().when(employeeRepository).deleteById(1L);

        mockMvc.perform(delete("/employees/1"))
                .andExpect(status().isOk());

        verify(employeeRepository, times(1)).deleteById(1L);
    }
}
