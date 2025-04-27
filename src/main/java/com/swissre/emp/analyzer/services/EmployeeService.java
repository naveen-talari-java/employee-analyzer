package com.swissre.emp.analyzer.services;

import com.swissre.emp.analyzer.dto.Employee;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private List<Employee> employees;

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public void analyzeSalary() {
        List<String> underpaidManagers = new ArrayList<>();
        List<String> overpaidManagers = new ArrayList<>();

        Map<Integer, List<Employee>> managerToSubordinates = getManagerToSubordinatesMap();

        for (Employee manager : employees) {
            if (manager.getManagerId() == null) { // Skip CEO
                continue;
            }

            List<Employee> subordinates = managerToSubordinates.get(manager.getId());
            if (subordinates != null && !subordinates.isEmpty()) {
                double averageSubordinateSalary = calculateAverageSalary(subordinates);
                double minAcceptableSalary = averageSubordinateSalary * 1.20;
                double maxAcceptableSalary = averageSubordinateSalary * 1.50;

                if (manager.getSalary() < minAcceptableSalary) {
                    underpaidManagers.add(String.format("Manager %d (%s %s) earns $%.2f, should earn at least $%.2f (underpaid by $%.2f)",
                            manager.getId(), manager.getFirstName(), manager.getLastName(), manager.getSalary(), minAcceptableSalary, minAcceptableSalary - manager.getSalary()));
                } else if (manager.getSalary() > maxAcceptableSalary) {
                    overpaidManagers.add(String.format("Manager %d (%s %s) earns $%.2f, should earn at most $%.2f (overpaid by $%.2f)",
                            manager.getId(), manager.getFirstName(), manager.getLastName(), manager.getSalary(), maxAcceptableSalary, manager.getSalary() - maxAcceptableSalary));
                }
            }
        }

        if (!underpaidManagers.isEmpty()) {
            System.out.println("\n--- Underpaid Managers ---");
            underpaidManagers.forEach(System.out::println);
        } else {
            System.out.println("\n--- No Underpaid Managers ---");
        }

        if (!overpaidManagers.isEmpty()) {
            System.out.println("\n--- Overpaid Managers ---");
            overpaidManagers.forEach(System.out::println);
        } else {
            System.out.println("\n--- No Overpaid Managers ---");
        }
    }

    public void analyzeReportingLines() {
        List<String> longReportingLines = new ArrayList<>();
        Employee ceo = findCeo();

        if (ceo == null) {
            System.out.println("Error: Could not find the CEO.");
            return;
        }

        for (Employee employee : employees) {
            if (employee.getId() != ceo.getId()) {
                int managerCount = countManagersToCeo(employee.getId());
                if (managerCount > 4) {
                    longReportingLines.add(String.format("Employee %d (%s %s) has a reporting line of %d managers.",
                            employee.getId(), employee.getFirstName(), employee.getLastName(), managerCount));
                }
            }
        }

        if (!longReportingLines.isEmpty()) {
            System.out.println("\n--- Employees with Long Reporting Lines ---");
            longReportingLines.forEach(System.out::println);
        } else {
            System.out.println("\n--- No Employees with Long Reporting Lines ---");
        }
    }

    private Map<Integer, List<Employee>> getManagerToSubordinatesMap() {
        Map<Integer, List<Employee>> managerToSubordinates = new HashMap<>();
        for (Employee employee : employees) {
            if (employee.getManagerId() != null) {
                managerToSubordinates.computeIfAbsent(employee.getManagerId(), k -> new ArrayList<>()).add(employee);
            }
        }
        return managerToSubordinates;
    }

    private double calculateAverageSalary(List<Employee> subordinates) {
        return subordinates.stream().mapToDouble(Employee::getSalary).average().orElse(0.0);
    }

    public Employee findCeo() {
        return employees.stream().filter(employee -> employee.getManagerId() == null).findFirst().orElse(null);
    }

    public Employee findEmployee(int id) {
        return employees.stream().filter(employee -> employee.getId() == id).findFirst().orElse(null);
    }

    public List<Employee> findDirectSubordinates(int managerId) {
        return employees.stream().filter(employee -> employee.getManagerId() != null && employee.getManagerId() == managerId).collect(Collectors.toList());
    }

    private int countManagersToCeo(int employeeId) {
        int count = 0;
        Employee current = findEmployee(employeeId);
        Employee ceo = findCeo();

        while (current != null && current.getManagerId() != null) {
            count++;
            current = findEmployee(current.getManagerId());
            if (current != null && current.getId() == ceo.getId()) {
                break; // Stop counting when we reach the CEO
            }
        }
        return count;
    }
}
