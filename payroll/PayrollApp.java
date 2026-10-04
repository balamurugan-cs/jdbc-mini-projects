package payroll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class PayrollApp {

    static final String URL = "jdbc:mysql://localhost:3306/payroll_db";
    static final String USER = "root";
    static final String PASS = "your_mysql_password";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Connection con = null;

        try {
           
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASS);

       
            TableSetup.createTables(con);

            int ch = 0;
            do {
                System.out.println();
                System.out.println("===== PAYROLL SYSTEM =====");
                System.out.println("1. Add Employee");
                System.out.println("2. View Employees");
                System.out.println("3. Update Employee Department");
                System.out.println("4. Delete Employee");
                System.out.println("5. Set Salary");
                System.out.println("6. Generate Payroll");
                System.out.println("7. View Payslip");
                System.out.println("8. Monthly Payroll Summary");
                System.out.println("9. Department Salary Summary");
                System.out.println("10. Exit");
                System.out.print("Enter your choice: ");

                String input = sc.nextLine().trim();
                if (!Validator.isNumber(input)) {
                    System.out.println("Enter a number from 1 to 10");
                    ch = 0;
                    continue;
                }
                ch = Integer.parseInt(input);

                switch (ch) {
                    case 1:
                        PayrollOperations.addEmployee(con, sc);
                        break;
                    case 2:
                        PayrollOperations.viewEmployees(con);
                        break;
                    case 3:
                        PayrollOperations.updateDepartment(con, sc);
                        break;
                    case 4:
                        PayrollOperations.deleteEmployee(con, sc);
                        break;
                    case 5:
                        PayrollOperations.setSalary(con, sc);
                        break;
                    case 6:
                        PayrollOperations.generatePayroll(con, sc);
                        break;
                    case 7:
                        PayrollQueries.payslip(con, sc);
                        break;
                    case 8:
                        PayrollQueries.monthlySummary(con);
                        break;
                    case 9:
                        PayrollQueries.departmentSummary(con);
                        break;
                    case 10:
                        System.out.println("Thank you. Exited.");
                        break;
                    default:
                        System.out.println("Invalid choice");
                }
            } while (ch != 10);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sc.close();
        }
    }
}