package payroll;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private AbstractScriptDatabaseInitializer abstractScriptDatabaseInitializer;

    @Test
    void shouldCreateNewEmployee() throws Exception {
        Employee newEmployee = new Employee("John Doe", "Developer");
        Employee savedEmployee = new Employee("John Doe", "Developer");
        savedEmployee.setId(1L);

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        mockMvc.perform(post("/employees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.role", is("Developer")));

        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void shouldReturnSavedEmployeeOnCreatingNewEmployee() throws Exception {
        Employee employee = new Employee("Jane Smith", "Manager");
        Employee savedEmployee = new Employee("Jane Smith", "Manager");
        savedEmployee.setId(2L);

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        mockMvc.perform(post("/employees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Jane Smith")))
                .andExpect(jsonPath("$.role", is("Manager")));

        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void shouldFailToCreateEmployeeWhenInvalidInputIsGiven() throws Exception {
        Employee invalidEmployee = new Employee("", "");

        mockMvc.perform(post("/employees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidEmployee)))
                .andExpect(status().isBadRequest());

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void shouldFailToCreateEmployeeWhenInvalidData() throws Exception {
        Employee invalidEmployee = new Employee("", "");

        mockMvc.perform(post("/employees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidEmployee)))
                .andExpect(status().isBadRequest());

        verify(employeeRepository, never()).save(any(Employee.class));
    }
}