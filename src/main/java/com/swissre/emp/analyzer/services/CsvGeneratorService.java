package com.swissre.emp.analyzer.services;

import com.swissre.emp.analyzer.exceptions.CSVGenerationException;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

@Service
public class CsvGeneratorService {

    public void generate( String filePath, int numRecords) {

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id,firstName,lastName,salary,managerId\n"); // Header

            Random random = new Random();
            for (int i = 1; i <= numRecords; i++) {
                String firstName = "FirstName" + i;
                String lastName = "LastName" + i;
                double salary = 30000 + random.nextDouble() * 70000; // Salary between 30k and 100k
                Integer managerId = i > 1 ? random.nextInt(i - 1) + 1 : null; // CEO has no manager

                writer.write(String.format("%d,%s,%s,%.2f,%s\n", i, firstName, lastName, salary, managerId == null ? "" : String.valueOf(managerId)));
            }
            System.out.println("CSV file generated successfully: " + filePath);

        } catch (IOException e) {
            System.err.println("Error while generating the CSV file");
            throw new CSVGenerationException(e.getMessage());
        }
    }
}
