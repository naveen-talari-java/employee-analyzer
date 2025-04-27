package com.swissre.emp.analyzer.services;

import com.swissre.emp.analyzer.exceptions.CSVGenerationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class CsvGeneratorServiceTest {

    @InjectMocks
    private CsvGeneratorService csvGeneratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generate_ValidInput_CreatesCsvFileWithCorrectData() throws IOException {
        // Arrange
        String filePath = "test_output.csv";
        int numRecords = 5;

        // Act
        csvGeneratorService.generate(filePath, numRecords);

        // Assert
        File file = new File(filePath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0); // File is not empty

        // Read the file content and verify (crude check, more detailed checks can be added)
        String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
        String[] lines = fileContent.split("\n");
        assertEquals(numRecords + 1, lines.length); // Includes header
        assertTrue(lines[0].contains("id,firstName,lastName,salary,managerId")); // Header is correct
        assertTrue(lines[1].contains("FirstName1"));  // First record is as expected

        //Clean up
        file.delete();
    }

    @Test
    void generate_ZeroRecords_CreatesCsvFileWithOnlyHeader() throws IOException {
        // Arrange
        String filePath = "empty_output.csv";
        int numRecords = 0;

        // Act
        csvGeneratorService.generate(filePath, numRecords);

        // Assert
        File file = new File(filePath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
        assertEquals("id,firstName,lastName,salary,managerId\n", fileContent);

        //Clean up
        file.delete();
    }

    @Test
    void generate_InvalidFilePath_ThrowsCSVGenerationException() {
        // Arrange
        String filePath = "/invalid/path/output.csv"; // An invalid path (most likely)
        int numRecords = 5;

        // Act & Assert
        assertThrows(CSVGenerationException.class, () -> csvGeneratorService.generate(filePath, numRecords));
    }

    @Test
    void generate_LargeNumberOfRecords_CreatesCsvFile() throws IOException {
        // Arrange
        String filePath = "large_output.csv";
        int numRecords = 1000;  //A reasonably large number

        // Act
        csvGeneratorService.generate(filePath, numRecords);

        // Assert
        File file = new File(filePath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
        String[] lines = fileContent.split("\n");
        assertEquals(numRecords + 1, lines.length);

        //Clean up
        file.delete();
    }

    //Potential test for handling existing file (overwrite or append - depends on requirements)
    @Test
    void generate_FileAlreadyExists_OverwritesFile() throws IOException {
        // Arrange
        String filePath = "existing_file.csv";
        File file = new File(filePath);
        file.createNewFile();
        FileWriter initialWriter = new FileWriter(file);
        initialWriter.write("Initial Content");
        initialWriter.close();

        // Act
        csvGeneratorService.generate(filePath, 3);

        // Assert
        String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
        assertTrue(fileContent.contains("id,firstName,lastName,salary,managerId"));  //Check for header
        assertTrue(fileContent.contains("FirstName1")); //Check for generated data
        assertFalse(fileContent.contains("Initial Content")); // Original content is overwritten

        // Clean up
        file.delete();
    }
}
