package com.mycompany.motorph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MotorPH {

    static final String EMP_FILE = "EmployeeDatabase.csv";
    static final String ATT_FILE = "Attendance.csv";
    static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            int loginResult = login();
            if (loginResult == 1) {
                employeeMenu();
            } else if (loginResult == 2) {
                payrollStaffMenu();
            } else {
                System.out.println("Program closed.");
                sc.close();
                System.exit(0);
            }
        }
    }

    // ===================== LOGIN =====================

    public static int login() {
        while (true) {
            System.out.println("=================================");
            System.out.println("     MotorPH SIGN-IN PORTAL      ");
            System.out.println("=================================");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.print("Select an option: ");
            String option = sc.nextLine().trim();

            if (option.equals("2")) return 0;
            if (!option.equals("1")) {
                System.out.println("Invalid option.\n");
                continue;
            }

            System.out.print("Enter Username: ");
            String username = sc.nextLine().trim();
            System.out.print("Enter Password: ");
            String password = sc.nextLine().trim();

            if (username.equals("employee") && password.equals("12345")) {
                System.out.println("\nLogin successful.");
                return 1;
            } else if (username.equals("payroll_staff") && password.equals("12345")) {
                System.out.println("\nLogin successful.");
                return 2;
            } else {
                System.out.println("Incorrect username and/or password.\n");
            }
        }
    }

    // ===================== EMPLOYEE MENU =====================

    public static void employeeMenu() {
        while (true) {
            System.out.println("\nEMPLOYEE MENU");
            System.out.println("1. View Employee Profile");
            System.out.println("2. Logout");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> viewProfileFlow();
                case "2" -> { System.out.println("Logged out."); return; }
                case "3" -> { System.out.println("Program closed."); sc.close(); System.exit(0); }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ===================== VIEW PROFILE FLOW =====================

    public static void viewProfileFlow() {
        while (true) {
            System.out.print("Enter Employee #: ");
            String empNo = sc.nextLine().trim();

            if (!displayProfile(empNo)) {
                System.out.println("Employee does not exist.");
                System.out.print("Try another employee? (Y/N): ");
                String retry = sc.nextLine().trim();
                while (!retry.equalsIgnoreCase("Y") && !retry.equalsIgnoreCase("N")) {
                    System.out.print("Please enter Y or N: ");
                    retry = sc.nextLine().trim();
                }
                if (retry.equalsIgnoreCase("N")) return;
                continue;
            }

            System.out.print("\nView another employee? (Y/N): ");
            String ans = sc.nextLine().trim();
            while (!ans.equalsIgnoreCase("Y") && !ans.equalsIgnoreCase("N")) {
                System.out.print("Please enter Y or N: ");
                ans = sc.nextLine().trim();
            }
            if (ans.equalsIgnoreCase("N")) return;
        }
    }

    // ===================== PAYROLL STAFF MENU =====================

    public static void payrollStaffMenu() {
        while (true) {
            System.out.println("\nPAYROLL STAFF MENU");
            System.out.println("1. Process Payroll");
            System.out.println("2. Logout");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> processPayrollMenu();
                case "2" -> { System.out.println("Logged out."); return; }
                case "3" -> { System.out.println("Program closed."); sc.close(); System.exit(0); }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ===================== PROCESS PAYROLL MENU =====================

    public static void processPayrollMenu() {
        while (true) {
            System.out.println("\nPROCESS PAYROLL");
            System.out.println("1. One employee");
            System.out.println("2. All employees");
            System.out.println("3. Back");
            System.out.print("Select an option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("Enter Employee #: ");
                    String empNo = sc.nextLine().trim();
                    if (!processPayroll(empNo, false)) {
                        System.out.println("Employee does not exist.");
                    }
                    pressEnter();
                }
                case "2" -> {
                    processPayroll("", true);
                    pressEnter();
                }
                case "3" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ===================== DISPLAY PROFILE =====================

    public static boolean displayProfile(String inputEmpNo) {
        try (BufferedReader br = new BufferedReader(new FileReader(EMP_FILE))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = parseCSVLine(line);
                if (data.length < 4) continue;
                String empNo = clean(data[0]);
                if (empNo.equals(inputEmpNo)) {
                    System.out.println("\n===================================");
                    System.out.println("Employee #    : " + empNo);
                    System.out.println("Employee Name : " + clean(data[1]) + ", " + clean(data[2]));
                    System.out.println("Birthday      : " + clean(data[3]));
                    System.out.println("===================================");
                    displayHoursWorked(empNo);
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading employee file.");
        }
        return false;
    }

    // ===================== DISPLAY HOURS WORKED =====================

    public static void displayHoursWorked(String empNo) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");
        System.out.println("\n--- Hours Worked Summary ---");

        for (int month = 6; month <= 12; month++) {
            double firstHalf  = 0;
            double secondHalf = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(ATT_FILE))) {
                br.readLine();
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] data = parseCSVLine(line);
                    if (data.length < 6) continue;
                    if (!clean(data[0]).equals(empNo)) continue;

                    String[] dateParts = data[3].trim().split("/");
                    if (dateParts.length < 3) continue;

                    int recordMonth = Integer.parseInt(dateParts[0].trim());
                    int day         = Integer.parseInt(dateParts[1].trim());
                    int year        = Integer.parseInt(dateParts[2].trim());

                    if (year != 2024 || recordMonth != month) continue;

                    LocalTime login  = LocalTime.parse(data[4].trim(), timeFormat);
                    LocalTime logout = LocalTime.parse(data[5].trim(), timeFormat);
                    double hours = computeHours(login, logout);

                    if (day <= 15) firstHalf  += hours;
                    else           secondHalf += hours;
                }
            } catch (Exception e) {
                continue;
            }

            if (firstHalf == 0 && secondHalf == 0) continue;

            System.out.printf("%n%s%n", getMonthName(month));
            System.out.printf("  1st Cutoff (1-15)   : %.1f hrs%n", firstHalf);
            System.out.printf("  2nd Cutoff (16-end) : %.1f hrs%n", secondHalf);
            System.out.printf("  Monthly Total       : %.1f hrs%n", firstHalf + secondHalf);
        }
        System.out.println("\n===================================");
    }

    // ===================== PROCESS PAYROLL =====================

    public static boolean processPayroll(String searchEmpNo, boolean processAll) {
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(EMP_FILE))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = parseCSVLine(line);
                if (data.length < 19) continue;
                String empNo = clean(data[0]);
                if (processAll || empNo.equals(searchEmpNo)) {
                    String lastName   = clean(data[1]);
                    String firstName  = clean(data[2]);
                    String birthday   = clean(data[3]);
                    double hourlyRate = parseDouble(data[18]);
                    calculateSalary(empNo, firstName, lastName, birthday, hourlyRate);
                    found = true;
                    if (!processAll) break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading employee file.");
        }
        return found;
    }

    // ===================== CALCULATE SALARY =====================

    public static void calculateSalary(String empNo, String firstName, String lastName,
                                        String birthday, double hourlyRate) {

        System.out.println("\n===================================");
        System.out.println("Employee #    : " + empNo);
        System.out.println("Employee Name : " + lastName + ", " + firstName);
        System.out.println("Birthday      : " + birthday);
        System.out.println("===================================");

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");

        for (int month = 6; month <= 12; month++) {
            double firstHalf  = 0;
            double secondHalf = 0;
            int daysInMonth = YearMonth.of(2024, month).lengthOfMonth();

            try (BufferedReader br = new BufferedReader(new FileReader(ATT_FILE))) {
                br.readLine();
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] data = parseCSVLine(line);
                    if (data.length < 6) continue;
                    if (!clean(data[0]).equals(empNo)) continue;

                    String[] dateParts = data[3].trim().split("/");
                    if (dateParts.length < 3) continue;

                    int recordMonth = Integer.parseInt(dateParts[0].trim());
                    int day         = Integer.parseInt(dateParts[1].trim());
                    int year        = Integer.parseInt(dateParts[2].trim());

                    if (year != 2024 || recordMonth != month) continue;

                    LocalTime login  = LocalTime.parse(data[4].trim(), timeFormat);
                    LocalTime logout = LocalTime.parse(data[5].trim(), timeFormat);
                    double hours = computeHours(login, logout);

                    if (day <= 15) firstHalf  += hours;
                    else           secondHalf += hours;
                }
            } catch (Exception e) {
                continue;
            }

            if (firstHalf == 0 && secondHalf == 0) continue;

            double gross1       = firstHalf  * hourlyRate;
            double gross2       = secondHalf * hourlyRate;
            double monthlyGross = gross1 + gross2;

            double sss        = getSSSContribution(monthlyGross);
            double philHealth = Math.min(monthlyGross * 0.03, 1800.00) / 2.0;
            double pagIbig    = Math.min(monthlyGross * 0.02, 100.00);

            double totalContributions = sss + (philHealth * 2) + pagIbig;
            double taxableIncome      = monthlyGross - totalContributions;
            double tax                = computeWithholdingTax(taxableIncome);

            double deductBasic      = (sss / 2) + philHealth + (pagIbig / 2);
            double net1             = gross1 - deductBasic;
            double net2             = gross2 - deductBasic - tax;
            double totalDeductions2 = deductBasic + tax;

            String monthName = getMonthName(month);

            // First Cutoff
            System.out.printf("%nCutoff Date: %s 1 to 15%n", monthName);
            System.out.printf("Total Hours Worked : %.1f hrs%n", firstHalf);
            System.out.printf("Gross Salary       : %s%n", formatPHP(gross1));
            System.out.printf("Net Salary         : %s%n", formatPHP(net1));

            // Second Cutoff
            System.out.printf("%nCutoff Date: %s 16 to %d%n", monthName, daysInMonth);
            System.out.printf("Total Hours Worked : %.1f hrs%n", secondHalf);
            System.out.printf("Gross Salary       : %s%n", formatPHP(gross2));
            System.out.println("Deductions:");
            System.out.printf("  SSS              : %s%n", formatPHP(sss / 2));
            System.out.printf("  PhilHealth       : %s%n", formatPHP(philHealth));
            System.out.printf("  Pag-IBIG         : %s%n", formatPHP(pagIbig / 2));
            System.out.printf("  Tax              : %s%n", formatPHP(tax));
            System.out.printf("Total Deductions   : %s%n", formatPHP(totalDeductions2));
            System.out.printf("Net Salary         : %s%n", formatPHP(net2));
        }
    }

    // ===================== COMPUTE HOURS =====================

    public static double computeHours(LocalTime login, LocalTime logout) {
        LocalTime graceTime  = LocalTime.of(8, 10);
        LocalTime cutoffTime = LocalTime.of(17, 0);

        if (logout.isAfter(cutoffTime)) logout = cutoffTime;

        long minutesWorked = Duration.between(login, logout).toMinutes();

        if (minutesWorked > 60) minutesWorked -= 60;
        else return 0;

        double hours = minutesWorked / 60.0;

        if (!login.isAfter(graceTime)) return 8.0;

        return Math.min(hours, 8.0);
    }

    // ===================== SSS TIERED CONTRIBUTION =====================

    public static double getSSSContribution(double salary) {
        if (salary < 3250)  return 135.00;
        if (salary < 3750)  return 157.50;
        if (salary < 4250)  return 180.00;
        if (salary < 4750)  return 202.50;
        if (salary < 5250)  return 225.00;
        if (salary < 5750)  return 247.50;
        if (salary < 6250)  return 270.00;
        if (salary < 6750)  return 292.50;
        if (salary < 7250)  return 315.00;
        if (salary < 7750)  return 337.50;
        if (salary < 8250)  return 360.00;
        if (salary < 8750)  return 382.50;
        if (salary < 9250)  return 405.00;
        if (salary < 9750)  return 427.50;
        if (salary < 10250) return 450.00;
        if (salary < 10750) return 472.50;
        if (salary < 11250) return 495.00;
        if (salary < 11750) return 517.50;
        if (salary < 12250) return 540.00;
        if (salary < 12750) return 562.50;
        if (salary < 13250) return 585.00;
        if (salary < 13750) return 607.50;
        if (salary < 14250) return 630.00;
        if (salary < 14750) return 652.50;
        if (salary < 15250) return 675.00;
        if (salary < 15750) return 697.50;
        if (salary < 16250) return 720.00;
        if (salary < 16750) return 742.50;
        if (salary < 17250) return 765.00;
        if (salary < 17750) return 787.50;
        if (salary < 18250) return 810.00;
        if (salary < 18750) return 832.50;
        if (salary < 19250) return 855.00;
        if (salary < 19750) return 877.50;
        if (salary < 20250) return 900.00;
        if (salary < 20750) return 922.50;
        if (salary < 21250) return 945.00;
        if (salary < 21750) return 967.50;
        if (salary < 22250) return 990.00;
        if (salary < 22750) return 1012.50;
        if (salary < 23250) return 1035.00;
        if (salary < 23750) return 1057.50;
        if (salary < 24250) return 1080.00;
        if (salary < 24750) return 1102.50;
        return 1125.00;
    }

    // ===================== WITHHOLDING TAX =====================

    public static double computeWithholdingTax(double taxableIncome) {
        if (taxableIncome <= 20832)  return 0;
        if (taxableIncome < 33333)   return (taxableIncome - 20833) * 0.20;
        if (taxableIncome < 66667)   return 2500 + (taxableIncome - 33333) * 0.25;
        if (taxableIncome < 166667)  return 10833 + (taxableIncome - 66667) * 0.30;
        if (taxableIncome < 666667)  return 40833.33 + (taxableIncome - 166667) * 0.32;
        return 200833.33 + (taxableIncome - 666667) * 0.35;
    }

    // ===================== CSV PARSER =====================

    public static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString());
        return result.toArray(new String[0]);
    }

    // ===================== UTILITIES =====================

    public static String clean(String s) {
        if (s == null) return "";
        return s.trim().replace("\"", "");
    }

    public static double parseDouble(String s) {
        try { return Double.parseDouble(clean(s).replace(",", "").trim()); }
        catch (Exception e) { return 0; }
    }

    public static String formatPHP(double amount) {
        return String.format("PHP %,.2f", amount);
    }

    public static String getMonthName(int month) {
        return switch (month) {
            case 6  -> "June";
            case 7  -> "July";
            case 8  -> "August";
            case 9  -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "Month " + month;
        };
    }

    public static void pressEnter() {
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }
}
