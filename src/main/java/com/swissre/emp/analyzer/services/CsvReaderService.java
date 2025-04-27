package com.swissre.emp.analyzer.services;

import com.swissre.emp.analyzer.dto.Employee;
import com.swissre.emp.analyzer.exceptions.CSVReadException;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvReaderService {

    public List<Employee> readEmployeesFromCsv(String filePath) {
        List<Employee> employees = new ArrayList<>();
        try (Reader reader = new FileReader(filePath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .builder()
                    .setHeader("id", "firstName", "lastName", "salary", "managerId")
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader);
            for (CSVRecord record : records) {
                try {
                    int id = Integer.parseInt(record.get("id"));
                    String firstName = record.get("firstName");
                    String lastName = record.get("lastName");
                    double salary = Double.parseDouble(record.get("salary"));
                    Integer managerId = record.get("managerId").isEmpty() ? null : Integer.parseInt(record.get("managerId"));
                    if (id > 0 && StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)
                                && salary > 0 ) {
                        employees.add(new Employee(id, firstName, lastName, salary, managerId));
                    } else {
                        System.err.println("Warning: Skipping record due to missing data");
                        System.out.println(record);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Skipping record due to invalid number format: " + record);
                    System.out.println(record);
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: Skipping record due to missing field: " + record);
                    System.out.println(record);
                }
            }
        } catch (IOException e) {
            System.err.println("Error while reading the CSV file");
            throw new CSVReadException(e.getMessage());
        }
        return employees;
    }
}
