package com.mycompany.motorph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * ReadFromTextFile.java
 * Reads employee records from EmployeeDatabase.csv,
 * computes payroll deductions, and displays net pay for each employee.
 */
public class ReadFromTextFile {

    // File path constant for the employee database CSV file
    static final String EMPLOYEE_DATA_FILE = "EmployeeDatabase.csv";

    // Scanner instance for reading user input if needed
    static final Scanner userInputScanner = new Scanner(System.in);

    public static void main(String[] args) {

        // Create a File object pointing to the employee data file
        File employeeFile = new File(EMPLOYEE_DATA_FILE);

        // Validate that the file exists before attempting to open it
        if (!employeeFile.exists()) {
            System.out.println("Error: " + EMPLOYEE_DATA_FILE + " not found.");
            System.out.println("Please make sure the file is in your project root directory.");
            return;
        }

        // Validate that the file has the necessary read permissions
        if (!employeeFile.canRead()) {
            System.out.println("Error: " + EMPLOYEE_DATA_FILE + " cannot be read.");
            return;
        }

        // Display the header for the payroll summary output
        System.out.println("===========================================");
        System.out.println("       MotorPH PAYROLL SUMMARY             ");
        System.out.println("===========================================");

        // Counter to track the total number of successfully processed records
        int processedRecordCount = 0;

        // Use BufferedReader for efficient line-by-line reading of the CSV file
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(employeeFile))) {

            // Skip the header row since it contains column labels, not employee data
            bufferedReader.readLine();

            String currentLine;

            // Iterate through each line of the file until end of file is reached
            while ((currentLine = bufferedReader.readLine()) != null) {

                // Skip blank lines to prevent processing empty records
                if (currentLine.trim().isEmpty()) continue;

                // Parse the CSV line using a custom parser that handles
                // quoted fields containing commas (e.g., addresses)
                String[] employeeFields = parseCSVLine(currentLine);

                // Ensure the record has at least 14 columns to reach Basic Salary
                if (employeeFields.length < 14) {
                    System.out.println("Skipping invalid record: " + currentLine);
                    continue;
                }

                // Extract individual fields from their respective column positions
                String employeeNumber = clean(employeeFields[0]); // Column 0: Employee #
                String lastName       = clean(employeeFields[1]); // Column 1: Last Name
                String firstName      = clean(employeeFields[2]); // Column 2: First Name

                // Combine last name and first name into a full display name
                String fullName = lastName + ", " + firstName;

                // Extract and parse gross salary from column 13 (Basic Salary)
                // Remove commas from formatted numbers (e.g., "52,670" → 52670)
                double grossSalary;
                try {
                    grossSalary = Double.parseDouble(
                        clean(employeeFields[13]).replace(",", "").trim()
                    );
                } catch (NumberFormatException numberFormatException) {
                    // Skip this record if the salary value cannot be parsed as a number
                    System.out.println("Skipping record with invalid salary for: " + fullName);
                    continue;
                }

                // Ensure salary is a positive value before computing deductions
                if (grossSalary <= 0) {
                    System.out.println("Skipping record with non-positive salary for: " + fullName);
                    continue;
                }

                // ── DEDUCTION COMPUTATIONS ──────────────────────────────────

                // Compute SSS contribution using the tiered salary lookup table
                double sssContribution = getSSSContribution(grossSalary);

                // Compute PhilHealth: 3% of gross salary, capped at PHP 1,800 monthly
                // Divided by 2 to get the employee's share for one cutoff period
                double philHealthContribution = Math.min(grossSalary * 0.03, 1800.00) / 2.0;

                // Compute Pag-IBIG: 2% of gross salary, capped at PHP 100 monthly
                double pagIbigContribution = Math.min(grossSalary * 0.02, 100.00);

                // Compute total mandatory contributions to determine taxable income
                // PhilHealth is multiplied by 2 to restore the full monthly amount
                double totalMandatoryContributions = sssContribution
                        + (philHealthContribution * 2)
                        + pagIbigContribution;

                // Taxable income = gross salary minus all mandatory contributions
                double taxableIncome = grossSalary - totalMandatoryContributions;

                // Compute withholding tax using the BIR 6-bracket progressive tax table
                double withholdingTax = computeWithholdingTax(taxableIncome);

                // ── NET PAY COMPUTATION ─────────────────────────────────────

                // Total deductions = SSS + PhilHealth share + Pag-IBIG + withholding tax
                double totalDeductions = sssContribution
                        + philHealthContribution
                        + pagIbigContribution
                        + withholdingTax;

                // Net pay = gross salary minus all computed deductions
                double netPay = grossSalary - totalDeductions;

                // ── OUTPUT ──────────────────────────────────────────────────

                // Print a clearly labeled payroll summary for each employee
                System.out.println("\n-------------------------------------------");
                System.out.printf("Employee #       : %s%n", employeeNumber);
                System.out.printf("Employee Name    : %s%n", fullName);
                System.out.printf("Gross Salary     : %s%n", formatPHP(grossSalary));
                System.out.println("-------------------------------------------");
                System.out.println("Deductions:");
                System.out.printf("  SSS            : %s%n", formatPHP(sssContribution));
                System.out.printf("  PhilHealth     : %s%n", formatPHP(philHealthContribution));
                System.out.printf("  Pag-IBIG       : %s%n", formatPHP(pagIbigContribution));
                System.out.printf("  Income Tax     : %s%n", formatPHP(withholdingTax));
                System.out.println("-------------------------------------------");
                System.out.printf("Total Deductions : %s%n", formatPHP(totalDeductions));
                System.out.printf("Net Pay          : %s%n", formatPHP(netPay));
                System.out.println("===========================================");

                // Increment the record counter after each successful computation
                processedRecordCount++;
            }

        } catch (Exception fileReadException) {
            // Catch and report any unexpected errors during file reading
            System.out.println("Error reading file: " + fileReadException.getMessage());
        }

        // Display the total number of records that were successfully processed
        System.out.printf("%n%d employee record(s) processed.%n", processedRecordCount);
    }

    // ===================== CSV PARSER =====================

    /**
     * Parses a single CSV line into an array of individual field values.
     * Correctly handles quoted fields that may contain commas inside them,
     * such as addresses: "Valero Street, Makati City"
     *
     * @param csvLine A single raw line from the CSV file
     * @return An array of string field values parsed from the line
     */
    public static String[] parseCSVLine(String csvLine) {
        List<String> parsedFields = new ArrayList<>();
        boolean insideQuotedField = false;      // Tracks if current position is inside quotes
        StringBuilder currentField = new StringBuilder();

        for (char character : csvLine.toCharArray()) {
            if (character == '"') {
                // Toggle quoted field mode when a double-quote character is encountered
                insideQuotedField = !insideQuotedField;
            } else if (character == ',' && !insideQuotedField) {
                // A comma outside of quotes signals the end of the current field
                parsedFields.add(currentField.toString());
                currentField.setLength(0); // Clear the buffer for the next field
            } else {
                // Append all other characters to build the current field value
                currentField.append(character);
            }
        }

        // Add the final field after the loop finishes
        parsedFields.add(currentField.toString());
        return parsedFields.toArray(new String[0]);
    }

    // ===================== SSS TIERED CONTRIBUTION =====================

    /**
     * Returns the employee SSS contribution based on a government-mandated tiered table.
     * Contributions range from PHP 135.00 for salaries below 3,250
     * up to PHP 1,125.00 for salaries of 24,750 and above.
     *
     * @param monthlySalary The employee's monthly gross salary
     * @return The corresponding SSS contribution amount
     */
    public static double getSSSContribution(double monthlySalary) {
        if (monthlySalary < 3250)  return 135.00;
        if (monthlySalary < 3750)  return 157.50;
        if (monthlySalary < 4250)  return 180.00;
        if (monthlySalary < 4750)  return 202.50;
        if (monthlySalary < 5250)  return 225.00;
        if (monthlySalary < 5750)  return 247.50;
        if (monthlySalary < 6250)  return 270.00;
        if (monthlySalary < 6750)  return 292.50;
        if (monthlySalary < 7250)  return 315.00;
        if (monthlySalary < 7750)  return 337.50;
        if (monthlySalary < 8250)  return 360.00;
        if (monthlySalary < 8750)  return 382.50;
        if (monthlySalary < 9250)  return 405.00;
        if (monthlySalary < 9750)  return 427.50;
        if (monthlySalary < 10250) return 450.00;
        if (monthlySalary < 10750) return 472.50;
        if (monthlySalary < 11250) return 495.00;
        if (monthlySalary < 11750) return 517.50;
        if (monthlySalary < 12250) return 540.00;
        if (monthlySalary < 12750) return 562.50;
        if (monthlySalary < 13250) return 585.00;
        if (monthlySalary < 13750) return 607.50;
        if (monthlySalary < 14250) return 630.00;
        if (monthlySalary < 14750) return 652.50;
        if (monthlySalary < 15250) return 675.00;
        if (monthlySalary < 15750) return 697.50;
        if (monthlySalary < 16250) return 720.00;
        if (monthlySalary < 16750) return 742.50;
        if (monthlySalary < 17250) return 765.00;
        if (monthlySalary < 17750) return 787.50;
        if (monthlySalary < 18250) return 810.00;
        if (monthlySalary < 18750) return 832.50;
        if (monthlySalary < 19250) return 855.00;
        if (monthlySalary < 19750) return 877.50;
        if (monthlySalary < 20250) return 900.00;
        if (monthlySalary < 20750) return 922.50;
        if (monthlySalary < 21250) return 945.00;
        if (monthlySalary < 21750) return 967.50;
        if (monthlySalary < 22250) return 990.00;
        if (monthlySalary < 22750) return 1012.50;
        if (monthlySalary < 23250) return 1035.00;
        if (monthlySalary < 23750) return 1057.50;
        if (monthlySalary < 24250) return 1080.00;
        if (monthlySalary < 24750) return 1102.50;
        return 1125.00; // Maximum SSS contribution for salaries of 24,750 and above
    }

    // ===================== WITHHOLDING TAX =====================

    /**
     * Computes monthly withholding tax using the BIR 6-bracket progressive tax table.
     * Applied on taxable income after subtracting mandatory contributions.
     *
     * @param taxableIncome The employee's taxable income after deducting contributions
     * @return The computed monthly withholding tax amount
     */
    public static double computeWithholdingTax(double taxableIncome) {
        // No tax for taxable income at or below 20,832
        if (taxableIncome <= 20832)  return 0;

        // Bracket 1: 20,833 – 33,332 → 20% of excess over 20,833
        if (taxableIncome < 33333)   return (taxableIncome - 20833) * 0.20;

        // Bracket 2: 33,333 – 66,666 → PHP 2,500 + 25% of excess over 33,333
        if (taxableIncome < 66667)   return 2500 + (taxableIncome - 33333) * 0.25;

        // Bracket 3: 66,667 – 166,666 → PHP 10,833 + 30% of excess over 66,667
        if (taxableIncome < 166667)  return 10833 + (taxableIncome - 66667) * 0.30;

        // Bracket 4: 166,667 – 666,666 → PHP 40,833.33 + 32% of excess over 166,667
        if (taxableIncome < 666667)  return 40833.33 + (taxableIncome - 166667) * 0.32;

        // Bracket 5: 666,667 and above → PHP 200,833.33 + 35% of excess over 666,667
        return 200833.33 + (taxableIncome - 666667) * 0.35;
    }

    // ===================== UTILITIES =====================

    /**
     * Removes surrounding whitespace and quotation marks from a CSV field value.
     *
     * @param rawValue The raw string value from a parsed CSV field
     * @return A cleaned string with no leading/trailing spaces or quotes
     */
    public static String clean(String rawValue) {
        if (rawValue == null) return "";
        return rawValue.trim().replace("\"", "");
    }

    /**
     * Formats a numeric amount as a Philippine Peso currency string.
     * Example: 52670.5 → "PHP 52,670.50"
     *
     * @param amount The numeric amount to format
     * @return A formatted currency string with PHP prefix
     */
    public static String formatPHP(double amount) {
        return String.format("PHP %,.2f", amount);
    }
}
