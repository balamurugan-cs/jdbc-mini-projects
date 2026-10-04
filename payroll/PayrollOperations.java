package payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class PayrollOperations {


    private static void rollback(Connection con) {
        try {
            con.rollback();
            System.out.println("Transaction rolled back");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void autoCommitOn(Connection con) {
        try {
            con.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static boolean employeeExists(Connection con, int empId) throws SQLException {
        PreparedStatement ps = con.prepareStatement(
                "select 1 from employees where emp_id=?");
        ps.setInt(1, empId);
        ResultSet rs = ps.executeQuery();
        boolean found = rs.next();
        rs.close();
        ps.close();
        return found;
    }

    
    private static double calculateTax(double gross) {
        double rate;
        if (gross <= 25000) {
            rate = 0;
        } else if (gross <= 50000) {
            rate = 0.05;
        } else {
            rate = 0.10;
        }
        return Math.round(gross * rate * 100.0) / 100.0;
    }


    public static void addEmployee(Connection con, Scanner sc) {
        System.out.print("Enter employee name: ");
        String name = sc.nextLine().trim();
        if (!Validator.isValidName(name)) {
            System.out.println("Invalid name. Use letters only.");
            return;
        }

        System.out.print("Enter email: ");
        String email = sc.nextLine().trim();
        if (!Validator.isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return;
        }

        System.out.print("Enter department: ");
        String dept = sc.nextLine().trim();
        if (!Validator.isValidName(dept)) {
            System.out.println("Invalid department. Use letters only.");
            return;
        }

        try {
            PreparedStatement ps = con.prepareStatement(
                    "insert into employees(name,email,department) values (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, dept);
            int row = ps.executeUpdate();

            if (row > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                System.out.println("Employee added successfully. Employee ID: "
                        + keys.getInt(1));
            } else {
                System.out.println("Adding employee failed");
            }
            ps.close();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("This email is already registered");
            } else {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


    public static void viewEmployees(Connection con) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from employees");
            PayrollQueries.display(rs);
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public static void updateDepartment(Connection con, Scanner sc) {
        System.out.print("Enter employee ID: ");
        String id = sc.nextLine().trim();
        if (!Validator.isNumber(id)) {
            System.out.println("Invalid employee ID.");
            return;
        }

        System.out.print("Enter new department: ");
        String dept = sc.nextLine().trim();
        if (!Validator.isValidName(dept)) {
            System.out.println("Invalid department. Use letters only.");
            return;
        }

        try {
            PreparedStatement ps = con.prepareStatement(
                    "update employees set department=? where emp_id=?");
            ps.setString(1, dept);
            ps.setInt(2, Integer.parseInt(id));
            int row = ps.executeUpdate();

            if (row > 0) {
                System.out.println("Department updated");
            } else {
                System.out.println("Employee not found");
            }
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public static void deleteEmployee(Connection con, Scanner sc) {
        System.out.print("Enter employee ID to delete: ");
        String id = sc.nextLine().trim();
        if (!Validator.isNumber(id)) {
            System.out.println("Invalid employee ID.");
            return;
        }

        try {
            PreparedStatement ps = con.prepareStatement(
                    "delete from employees where emp_id=?");
            ps.setInt(1, Integer.parseInt(id));
            int row = ps.executeUpdate();

            if (row > 0) {
                System.out.println("Employee deleted");
            } else {
                System.out.println("Employee not found");
            }
            ps.close();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                System.out.println("Cannot delete. Employee has salary or payroll records.");
            } else {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


    public static void setSalary(Connection con, Scanner sc) {
        System.out.print("Enter employee ID: ");
        String id = sc.nextLine().trim();
        if (!Validator.isNumber(id)) {
            System.out.println("Invalid employee ID.");
            return;
        }

        System.out.print("Enter basic salary: ");
        String basic = sc.nextLine().trim();
        if (!Validator.isValidAmount(basic)) {
            System.out.println("Invalid basic salary.");
            return;
        }

        System.out.print("Enter allowances (0 if none): ");
        String allow = sc.nextLine().trim();
        if (!Validator.isValidAllowance(allow)) {
            System.out.println("Invalid allowances.");
            return;
        }

        try {
            int empId = Integer.parseInt(id);

            if (!employeeExists(con, empId)) {
                System.out.println("Employee not found");
                return;
            }

     
            PreparedStatement psUpdate = con.prepareStatement(
                    "update salaries set basic_salary=?, allowances=? where emp_id=?");
            psUpdate.setDouble(1, Double.parseDouble(basic));
            psUpdate.setDouble(2, Double.parseDouble(allow));
            psUpdate.setInt(3, empId);
            int row = psUpdate.executeUpdate();
            psUpdate.close();

            if (row > 0) {
                System.out.println("Salary updated");
                return;
            }

         
            PreparedStatement psInsert = con.prepareStatement(
                    "insert into salaries(emp_id,basic_salary,allowances) values (?,?,?)");
            psInsert.setInt(1, empId);
            psInsert.setDouble(2, Double.parseDouble(basic));
            psInsert.setDouble(3, Double.parseDouble(allow));
            row = psInsert.executeUpdate();
            psInsert.close();

            if (row > 0) {
                System.out.println("Salary saved successfully");
            } else {
                System.out.println("Saving salary failed");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

  

    public static void generatePayroll(Connection con, Scanner sc) {
        System.out.print("Enter month (yyyy-mm, example 2026-10): ");
        String month = sc.nextLine().trim();
        if (!Validator.isValidMonth(month)) {
            System.out.println("Invalid month. Use yyyy-mm.");
            return;
        }

        try {
            con.setAutoCommit(false);

            
            PreparedStatement psFetch = con.prepareStatement(
                    "select emp_id, basic_salary, allowances from salaries "
                            + "where emp_id not in "
                            + "(select emp_id from payroll where month_year=?)");
            psFetch.setString(1, month);
            ResultSet rs = psFetch.executeQuery();

            PreparedStatement psInsert = con.prepareStatement(
                    "insert into payroll(emp_id,month_year,gross_salary,tax_amount,net_salary) "
                            + "values (?,?,?,?,?)");

            int count = 0;
            while (rs.next()) {
                double gross = rs.getDouble("basic_salary") + rs.getDouble("allowances");
                double tax = calculateTax(gross);
                double net = Math.round((gross - tax) * 100.0) / 100.0;

                psInsert.setInt(1, rs.getInt("emp_id"));
                psInsert.setString(2, month);
                psInsert.setDouble(3, gross);
                psInsert.setDouble(4, tax);
                psInsert.setDouble(5, net);
                psInsert.addBatch();
                count++;
            }

            if (count == 0) {
                con.rollback();
                System.out.println("Nothing to process. Salary not set, "
                        + "or payroll already generated for " + month);
            } else {
                psInsert.executeBatch();
                con.commit();
                System.out.println("Payroll generated for " + count
                        + " employee(s) for " + month);
            }

            rs.close();
            psFetch.close();
            psInsert.close();
        } catch (SQLException e) {
            rollback(con);
            System.out.println("Payroll generation failed: " + e.getMessage());
        } finally {
            autoCommitOn(con);
        }
    }
}