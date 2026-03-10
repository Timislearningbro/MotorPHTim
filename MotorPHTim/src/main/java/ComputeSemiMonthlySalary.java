package com.motorph.main;

/**
 * ComputeSemiMonthlySalary
 *
 * Step 1 – Use existing MotorPH project
 * - Reuse Task 7 (total hours worked).
 *
 * Step 2 – Input and logic
 * - Collect employee name, total hours worked, and hourly rate.
 * - Compute semi monthly salary (hours * rate).
 * - Check if hours or rate is non‑positive using a conditional.
 *
 * Step 3 – Output
 * - Display employee name, total hours, rate, and semi‑monthly salary.
 * - Include comments for each major part.
 */

import java.util.Scanner;

public class ComputeSemiMonthlySalary {
    public static void main(String[] args) {
        // Step 2: Declare variables for employee and work data
        try ( // Step 1: Create Scanner to read input
                Scanner scanner = new Scanner(System.in)) {
            // Step 2: Declare variables for employee and work data
            System.out.print("Enter employee name: ");
            String employeeName = scanner.nextLine(); // e.g., "Juan Dela Cruz"
            System.out.print("Enter total hours worked (from Task 7): ");
            double totalHoursWorked = scanner.nextDouble(); // e.g., 80.0 hours
            System.out.print("Enter hourly rate (₱): ");
            double hourlyRate = scanner.nextDouble(); // e.g., 100.0
            // Step 3: Validate input using a conditional statement
            if (totalHoursWorked <= 0 || hourlyRate <= 0) {
                System.out.println("Error: Hours worked and rate must be greater than zero.");
                System.out.println("Semi‑monthly salary cannot be computed.");
            } else {
                // Compute semi‑monthly salary: hours * rate
                double semiMonthlySalary = totalHoursWorked * hourlyRate;
                
                // Step 4: Display clearly labeled result
                System.out.println("\n=== MotorPH Semi‑Monthly Salary Report ===");
                System.out.println("Employee Name: " + employeeName);
                System.out.println("Total Hours Worked: " + totalHoursWorked + " hours");
                System.out.println("Hourly Rate: ₱" + hourlyRate);
                System.out.println("Semi‑Monthly Salary: ₱" + semiMonthlySalary);
                
                // Optional test message
                System.out.println("Computation verified successfully!");
            }
            // Close Scanner to prevent resource leaks
        }
    }
}
