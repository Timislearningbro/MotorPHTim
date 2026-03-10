import java.util.Scanner;
import java.text.DecimalFormat;

public class PaydayPayrollSimple {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            DecimalFormat fmt = new DecimalFormat("₱#,##0.00");
            
            System.out.println("=== SIMPLE PAYDAY PAYROLL ===");
            System.out.print("Payday (e.g. Mar15): ");
            String payday = sc.nextLine();
            
            while (true) {
                System.out.print("\nName (quit): ");
                String name = sc.nextLine();
                if (name.equalsIgnoreCase("quit")) break;
                
                System.out.print("Hours: ");
                double h = sc.nextDouble();
                System.out.print("Rate: ");
                double r = sc.nextDouble();
                sc.nextLine();
                
                if (h <= 0 || r <= 0) {
                    System.out.println("Invalid data!");
                    continue;
                }
                
                double regH = Math.min(h, 104);
                double otH = h - regH;
                double gross = regH * r + otH * r * 1.25;
                double sss = 1767.5;  // Max bracket for 55k
                double phil = 1250;    // 5% of 50k / 2
                double pag = 100;      // 2% cap
                double totalDed = sss + phil + pag;
                double net = gross - totalDed;
                
                System.out.println("\n" + payday + " PAYSLIP - " + name);
                System.out.println("Gross: " + fmt.format(gross));
                System.out.println("Deductions: " + fmt.format(totalDed));
                System.out.println("NET PAY: " + fmt.format(net));
            }
            
            System.out.println("Payday done!");
        }
    }
}
