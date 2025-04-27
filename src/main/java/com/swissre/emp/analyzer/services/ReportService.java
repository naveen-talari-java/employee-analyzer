package com.swissre.emp.analyzer.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    public void printSalaryDiscrepancyReport(List<String> underpaid, List<String> overpaid) {
        if (!underpaid.isEmpty()) {
            System.out.println("\n--- Underpaid Managers ---");
            underpaid.forEach(System.out::println);
        } else {
            System.out.println("\n--- No Underpaid Managers ---");
        }

        if (!overpaid.isEmpty()) {
            System.out.println("\n--- Overpaid Managers ---");
            overpaid.forEach(System.out::println);
        } else {
            System.out.println("\n--- No Overpaid Managers ---");
        }
    }

    public void printLongReportingLinesReport(List<String> longLines) {
        if (!longLines.isEmpty()) {
            System.out.println("\n--- Employees with Long Reporting Lines ---");
            longLines.forEach(System.out::println);
        } else {
            System.out.println("\n--- No Employees with Long Reporting Lines ---");
        }
    }
}
