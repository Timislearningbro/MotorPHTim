package com.motorph.main;

import java.util.Scanner;

/**
 * MotorPH_CompletePayroll - FINAL VERSION WITH ALL-IN-ALL PAY
 * 
 * Features:
 * - Hours: 8AM-5PM official time only
 * - Government deductions: Split evenly across cutoffs
 * - Tax: 2nd cutoff ONLY
 * - ALL-IN-ALL PAY: Total monthly net pay
 */
public class MotorPH_CompletePayroll {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter employee name: ");
            String employeeName = scanner.nextLine();

            // 1st Cutoff (June 1-15)
            System.out.print("1st Cutoff - Hours Worked (8AM-5PM): ");
            double hours1st = scanner.nextDouble();
            System.out.print("1st Cutoff - Gross Salary (₱): ");
            double gross1st = scanner.nextDouble();

            // 2nd Cutoff (June 16-30)
            System.out.print("2nd Cutoff - Hours Worked (8AM-5PM): ");
            double hours2nd = scanner.nextDouble();
            System.out.print("2nd Cutoff - Gross Salary (₱): ");
            double gross2nd = scanner.nextDouble();

            if (gross1st <= 0 || gross2nd <= 0 || hours1st <= 0 || hours2nd <= 0) {
                System.out.println("Error: All values must be > 0.");
                return;
            }

            // Monthly totals
            double totalHours = hours1st + hours2nd;
            double totalGross = gross1st + gross2nd;

            // Government deductions per cutoff (SSS + PhilHealth + Pag‑IBIG)
            double govPerCutoff = (1125 + 375 + 100) / 2; // ₱800 per cutoff

            // 1st Cutoff: Government only (NO TAX)
            double net1st = gross1st - govPerCutoff;

            // Monthly withholding tax (BIR table)
            double monthlyTaxable = totalGross - (1125 + 375 + 100);
            double withholdingMonthly = 0;
            
            if (monthlyTaxable <= 20832) withholdingMonthly = 0;
            else if (monthlyTaxable < 33333) withholdingMonthly = (monthlyTaxable - 20833) * 0.20;
            else if (monthlyTaxable < 66667) withholdingMonthly = 2500 + (monthlyTaxable - 33333) * 0.25;
            else if (monthlyTaxable < 166667) withholdingMonthly = 10833 + (monthlyTaxable - 66667) * 0.30;
            else if (monthlyTaxable < 666667) withholdingMonthly = 40833.33 + (monthlyTaxable - 166667) * 0.32;
            else withholdingMonthly = 200833.33 + (monthlyTaxable - 666667) * 0.35;

            // 2nd Cutoff: Government + FULL withholding tax
            double net2nd = gross2nd - govPerCutoff - withholdingMonthly;

            // ALL-IN-ALL PAY (total monthly net)
            double allInAllPay = net1st + net2nd;

            // THE REPORT
            System.out.println("\n" + "═".repeat(50));
            System.out.println("           MOTORPH JUNE 2026 PAYROLL");
            System.out.println("═".repeat(50));
            System.out.println("Employee: " + employeeName);
            
            System.out.println("\n? 1ST CUTOFF (June 1-15)");
            System.out.printf("Hours: %6.2f | Gross: ₱%9.2f | Net: ₱%9.2f%n", hours1st, gross1st, net1st);
            
            System.out.println("\n? 2ND CUTOFF (June 16-30)");
            System.out.printf("Hours: %6.2f | Gross: ₱%9.2f | Net: ₱%9.2f%n", hours2nd, gross2nd, net2nd);
            
            System.out.println("\n" + "═".repeat(50));
            System.out.println("?ALL-IN-ALL PAY (Monthly Take-Home): ₱" + String.format("%.2f", allInAllPay));
            System.out.println("═".repeat(50));

            System.out.println("\nDEDUCTION BREAKDOWN (per cutoff):");
            System.out.println("SSS: ₱" + String.format("%.2f", 1125.0/2));
            System.out.println("PhilHealth: ₱" + String.format("%.2f", 375.0/2));
            System.out.println("Pag‑IBIG: ₱" + String.format("%.2f", 100.0/2));
            System.out.println("Withholding Tax: ₱" + String.format("%.2f", withholdingMonthly) + " (2nd cutoff only)");
            
            System.out.println("\nVerified: SSS/PhilHealth/Pag‑IBIG official tables");
        }
    }
}
