package payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class PayrollQueries {

   
    public static void display(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();
        int count = 0;

        for (int i = 1; i <= cols; i++) {
            System.out.print(md.getColumnLabel(i) + "\t");
        }
        System.out.println();
        System.out.println("----------------------------------------------------------");

        while (rs.next()) {
            for (int i = 1; i <= cols; i++) {
                System.out.print(rs.getString(i) + "\t");
            }
            System.out.println();
            count++;
        }

        if (count == 0) {
            System.out.println("No records found");
        }
    }


    public static void payslip(Connection con, Scanner sc) {
        System.out.print("Enter employee ID: ");
        String id = sc.nextLine().trim();
        if (!Validator.isNumber(id)) {
            System.out.println("Invalid employee ID.");
            return;
        }

        System.out.print("Enter month (yyyy-mm): ");
        String month = sc.nextLine().trim();
        if (!Validator.isValidMonth(month)) {
            System.out.println("Invalid month. Use yyyy-mm.");
            return;
        }

        try {
            PreparedStatement ps = con.prepareStatement(
                    "select e.emp_id, e.name, e.department, p.month_year, "
                            + "p.gross_salary, p.tax_amount, p.net_salary "
                            + "from payroll p join employees e on p.emp_id = e.emp_id "
                            + "where p.emp_id=? and p.month_year=?");
            ps.setInt(1, Integer.parseInt(id));
            ps.setString(2, month);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println();
                System.out.println("========== PAYSLIP ==========");
                System.out.println("Employee ID : " + rs.getInt("emp_id"));
                System.out.println("Name        : " + rs.getString("name"));
                System.out.println("Department  : " + rs.getString("department"));
                System.out.println("Month       : " + rs.getString("month_year"));
                System.out.println("-----------------------------");
                System.out.println("Gross Salary: " + rs.getString("gross_salary"));
                System.out.println("Tax         : " + rs.getString("tax_amount"));
                System.out.println("Net Salary  : " + rs.getString("net_salary"));
                System.out.println("=============================");
            } else {
                System.out.println("No payslip found. Generate the payroll for that month first.");
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

  
    public static void monthlySummary(Connection con) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "select month_year, count(*) as employees_paid, "
                            + "sum(gross_salary) as total_gross, "
                            + "sum(tax_amount) as total_tax, "
                            + "sum(net_salary) as total_net, "
                            + "max(net_salary) as highest_net, "
                            + "round(avg(net_salary),2) as average_net "
                            + "from payroll group by month_year order by month_year");
            display(rs);
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    
    public static void departmentSummary(Connection con) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "select e.department, count(*) as employees, "
                            + "round(avg(s.basic_salary),2) as avg_basic, "
                            + "max(s.basic_salary) as max_basic, "
                            + "min(s.basic_salary) as min_basic "
                            + "from employees e join salaries s on e.emp_id = s.emp_id "
                            + "group by e.department");
            display(rs);
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}