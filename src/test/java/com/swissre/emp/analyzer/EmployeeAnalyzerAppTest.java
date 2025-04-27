package com.swissre.emp.analyzer;

import com.swissre.emp.analyzer.dto.Employee;
import com.swissre.emp.analyzer.exceptions.CSVGenerationException;
import com.swissre.emp.analyzer.exceptions.CSVReadException;
import com.swissre.emp.analyzer.services.CsvGeneratorService;
import com.swissre.emp.analyzer.services.CsvReaderService;
import com.swissre.emp.analyzer.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class EmployeeAnalyzerAppTest {

    @InjectMocks
    private EmployeeAnalyzerApp employeeAnalyzerApp;

    @Mock
    private CsvGeneratorService csvGeneratorService;

    @Mock
    private CsvReaderService csvReaderService;

    @Mock
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        //Manually inject dependencies since @SpringBootTest creates a full application context
        ReflectionTestUtils.setField(employeeAnalyzerApp, "filePath", "test.csv");
        ReflectionTestUtils.setField(employeeAnalyzerApp, "records", 100);
    }

    /*@Test
    void contextLoads() {
    }*/

    @Test
    void run_SuccessfulExecution_CallsServicesAndPrintsOutput() {
        // Arrange
        List<Employee> mockEmployees = new ArrayList<>();

        mockEmployees.add(new Employee(1, "CEO", "Last", 100000, null));
        mockEmployees.add(new Employee(2, "M1", "Last", 90000, 1));
        mockEmployees.add(new Employee(3, "John", "Doe", 50000,2 ));

        when(csvReaderService.readEmployeesFromCsv(anyString())).thenReturn(mockEmployees);
        doNothing().when(csvGeneratorService).generate(anyString(), anyInt());
        doNothing().when(employeeService).analyzeSalary();
        doNothing().when(employeeService).analyzeReportingLines();

        // Act & Assert
        assertDoesNotThrow(() -> employeeAnalyzerApp.run());

        verify(csvGeneratorService, times(1)).generate("test.csv", 100);
        verify(csvReaderService, times(1)).readEmployeesFromCsv("test.csv");
        verify(employeeService, times(1)).analyzeSalary();
        verify(employeeService, times(1)).analyzeReportingLines();
    }

    @Test
    void run_CSVGenerationExceptionThrown_CatchesAndPrintsError() {
        // Arrange
        doThrow(new CSVGenerationException("Generation failed")).when(csvGeneratorService).generate(anyString(), anyInt());

        // Act & Assert
        assertDoesNotThrow(() -> employeeAnalyzerApp.run());
    }

    @Test
    void run_CSVReadExceptionThrown_CatchesAndPrintsError()  {
        // Arrange
        doThrow(new CSVReadException("Read failed")).when(csvReaderService).readEmployeesFromCsv(anyString());
        doNothing().when(csvGeneratorService).generate(anyString(), anyInt()); // Avoid exception from generator

        // Act & Assert
        assertDoesNotThrow(() -> employeeAnalyzerApp.run());
    }

    @Test
    void run_GeneralExceptionThrown_CatchesAndPrintsError()  {
        // Arrange
        doThrow(new RuntimeException("Something went wrong")).when(csvReaderService).readEmployeesFromCsv(anyString());
        doNothing().when(csvGeneratorService).generate(anyString(), anyInt());

        // Act & Assert
        assertDoesNotThrow(() -> employeeAnalyzerApp.run());
    }

    @Test
    void run_DefaultFilePathAndRecordsUsed_WhenValuesAreNullOrEmpty()  {
        //Arrange
        ReflectionTestUtils.setField(employeeAnalyzerApp, "filePath", null);
        ReflectionTestUtils.setField(employeeAnalyzerApp, "records", null);
        List<Employee> mockEmployees = new ArrayList<>();
        when(csvReaderService.readEmployeesFromCsv(anyString())).thenReturn(mockEmployees);
        doNothing().when(csvGeneratorService).generate(anyString(), anyInt());
        doNothing().when(employeeService).analyzeSalary();
        doNothing().when(employeeService).analyzeReportingLines();

        //Act
        assertDoesNotThrow(() -> employeeAnalyzerApp.run());

        //Assert
        verify(csvGeneratorService, times(1)).generate("employees.csv", 25);
        verify(csvReaderService, times(1)).readEmployeesFromCsv("employees.csv");
    }

    @Test
    void main_SpringApplicationRuns() {
        // Arrange & Act & Assert (Simple test to check if the main method runs without errors)
        assertDoesNotThrow(() -> SpringApplication.run(EmployeeAnalyzerApp.class));
    }
}
