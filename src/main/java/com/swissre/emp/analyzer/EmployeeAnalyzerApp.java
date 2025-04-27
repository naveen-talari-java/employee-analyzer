package com.swissre.emp.analyzer;

import com.swissre.emp.analyzer.dto.Employee;
import com.swissre.emp.analyzer.exceptions.CSVGenerationException;
import com.swissre.emp.analyzer.exceptions.CSVReadException;
import com.swissre.emp.analyzer.services.CsvReaderService;
import com.swissre.emp.analyzer.services.EmployeeService;
import com.swissre.emp.analyzer.services.CsvGeneratorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@SpringBootApplication
public class EmployeeAnalyzerApp implements CommandLineRunner {

	@Autowired
	private CsvGeneratorService csvGeneratorService;

	@Autowired
	private CsvReaderService csvReaderService;

	@Autowired
	private EmployeeService employeeService;

	@Value("${spring.emp.csv.file.path}")
	private String filePath;

	@Value("${spring.no.of.emp.records}")
	private Integer records;

	@Value("${spring.csv.generation.enabled}")
	private boolean csvGenerationEnabled;

	public static void main(String[] args) {
		SpringApplication.run(EmployeeAnalyzerApp.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		//setting the default value
		if(StringUtils.isEmpty(filePath)) {
			filePath = "employees.csv";
		}
		//setting the default value
		if(Objects.isNull(records)) {
			records=25;
		}

		//Generating the csv file
        try {
			long startTime = System.currentTimeMillis();

            if (csvGenerationEnabled) {
                csvGeneratorService.generate(filePath, records);
                System.out.println(records+" records generated in " + filePath);
            }

            //reading the csv file
            List<Employee> employees = csvReaderService.readEmployeesFromCsv(filePath);
            employeeService.setEmployees(employees);

            //calling the report methods of employeeService
            employeeService.analyzeSalary();
            employeeService.analyzeReportingLines();

			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime; // in milliseconds
			System.out.println("Execution time: " + duration + " milliseconds");

        } catch (CSVGenerationException e) {
			System.err.println("CSVGenerationException raised with root cause: "+e.getMessage());
        } catch (CSVReadException e) {
			System.err.println("CSVReadException raised with root cause: "+e.getMessage());
		} catch (Exception e) {
			System.err.println("Exception raised with root cause: "+e.getMessage());
		}
    }
}
