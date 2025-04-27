package com.swissre.emp.analyzer.services;

import com.swissre.emp.analyzer.dto.Employee;
import com.swissre.emp.analyzer.exceptions.CSVReadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvReaderServiceTest {

    @InjectMocks
    private CsvReaderService csvReaderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void readEmployeesFromCsv_ValidFile_ReturnsListOfEmployees() throws IOException {
        // Arrange
        File tempFile = createTempCsvFile("id,firstName,lastName,salary,managerId\n" +
                "1,John,Doe,50000.0,101\n" +
                "2,Jane,Smith,60000.0,");

        // Act
        List<Employee> employees = csvReaderService.readEmployeesFromCsv(tempFile.getAbsolutePath());

        // Assert
        assertEquals(2, employees.size());

        assertEquals(1, employees.get(0).getId());
        assertEquals("John", employees.get(0).getFirstName());
        assertEquals("Doe", employees.get(0).getLastName());
        assertEquals(50000.0, employees.get(0).getSalary());
        assertEquals(101, employees.get(0).getManagerId());

        assertEquals(2, employees.get(1).getId());
        assertEquals("Jane", employees.get(1).getFirstName());
        assertEquals("Smith", employees.get(1).getLastName());
        assertEquals(60000.0, employees.get(1).getSalary());
        assertNull(employees.get(1).getManagerId());

        tempFile.delete();
    }

    @Test
    void readEmployeesFromCsv_FileWithInvalidNumberFormat_SkipsInvalidRecords() throws IOException {
        // Arrange
        File tempFile = createTempCsvFile("id,firstName,lastName,salary,managerId\n" +
                "1,John,Doe,50000.0,101\n" +
                "invalid,Jane,Smith,60000.0,202\n" +
                "3,Peter,Jones,abc,303\n" +
                "4,Anna,White,70000.0,");

        // Act
        List<Employee> employees = csvReaderService.readEmployeesFromCsv(tempFile.getAbsolutePath());

        // Assert
        assertEquals(2, employees.size()); // Only valid records are processed

        assertEquals(1, employees.get(0).getId());
        assertEquals("John", employees.get(0).getFirstName());
        assertEquals("Doe", employees.get(0).getLastName());
        assertEquals(50000.0, employees.get(0).getSalary());
        assertEquals(101, employees.get(0).getManagerId());

        assertEquals(4, employees.get(1).getId());
        assertEquals("Anna", employees.get(1).getFirstName());
        assertEquals("White", employees.get(1).getLastName());
        assertEquals(70000.0, employees.get(1).getSalary());
        assertNull(employees.get(1).getManagerId());

        tempFile.delete();
    }

    @Test
    void readEmployeesFromCsv_FileWithMissingFields_SkipsInvalidRecords() throws IOException {
        // Arrange
        File tempFile = createTempCsvFile("id,firstName,lastName,salary,managerId\n" +
                "1,John,Doe,50000.0,101\n" +
                "2,Jane,Smith,60000.0,\n" +
                "3,Peter,Jones,,303\n" +
                "4,Anna,,70000.0,404\n" +
                "5,,,70000.0,505");

        // Act
        List<Employee> employees = csvReaderService.readEmployeesFromCsv(tempFile.getAbsolutePath());

        System.out.println(employees);

        // Assert
        assertEquals(2, employees.size());

        assertEquals(1, employees.get(0).getId());
        assertEquals("John", employees.get(0).getFirstName());
        assertEquals("Doe", employees.get(0).getLastName());
        assertEquals(50000.0, employees.get(0).getSalary());
        assertEquals(101, employees.get(0).getManagerId());

        tempFile.delete();
    }


    @Test
    void readEmployeesFromCsv_EmptyFile_ReturnsEmptyList() throws IOException {
        // Arrange
        File tempFile = createTempCsvFile("id,firstName,lastName,salary,managerId"); //Only Header

        // Act
        List<Employee> employees = csvReaderService.readEmployeesFromCsv(tempFile.getAbsolutePath());

        // Assert
        assertTrue(employees.isEmpty());
        tempFile.delete();
    }

    @Test
    void readEmployeesFromCsv_FileNotExists_ThrowsCSVReadException() {
        // Arrange
        String nonExistentFilePath = "nonexistent.csv";

        // Act & Assert
        assertThrows(CSVReadException.class, () -> csvReaderService.readEmployeesFromCsv(nonExistentFilePath));
    }

    private File createTempCsvFile(String content) throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit(); // Ensure the file is deleted after the JVM exits
        FileWriter writer = new FileWriter(tempFile);
        writer.write(content);
        writer.close();
        return tempFile;
    }

}
