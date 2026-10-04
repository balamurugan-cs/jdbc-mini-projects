package banking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class BankOperations {

 

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

   
    private static double getBalance(Connection con, int accNo) throws SQLException {
        PreparedStatement ps = con.prepareStatement(
                "select balance from accounts where account_no=?");
        ps.setInt(1, accNo);
        ResultSet rs = ps.executeQuery();
        double bal = -1;
        if (rs.next()) {
            bal = rs.getDouble("balance");
        }
        rs.close();
        ps.close();
        return bal;
    }

    private static void saveTxn(Connection con, int accNo, String type, double amount)
            throws SQLException {
        PreparedStatement ps = con.prepareStatement(
                "insert into transactions(account_no,txn_type,amount) values (?,?,?)");
        ps.setInt(1, accNo);
        ps.setString(2, type);
        ps.setDouble(3, amount);
        ps.executeUpdate();
        ps.close();
    }


    public static void createAccount(Connection con, Scanner sc) {
        System.out.print("Enter name: ");
        String name = sc.nextLine().trim();
        if (!Validator.isValidName(name)) {
            System.out.println("Invalid name. Use letters only.");
            return;
        }

        System.out.print("Enter phone (10 digits): ");
        String phone = sc.nextLine().trim();
        if (!Validator.isValidPhone(phone)) {
            System.out.println("Invalid phone. Enter exactly 10 digits.");
            return;
        }

        System.out.print("Enter email: ");
        String email = sc.nextLine().trim();
        if (!Validator.isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return;
        }

        System.out.print("Enter opening balance: ");
        String amt = sc.nextLine().trim();
        if (!Validator.isValidAmount(amt)) {
            System.out.println("Invalid amount.");
            return;
        }
        double opening = Double.parseDouble(amt);

        try {
            con.setAutoCommit(false);

            PreparedStatement psCust = con.prepareStatement(
                    "insert into customers(name,phone,email) values (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            psCust.setString(1, name);
            psCust.setString(2, phone);
            psCust.setString(3, email);
            int row1 = psCust.executeUpdate();

            ResultSet keys = psCust.getGeneratedKeys();
            keys.next();
            int customerId = keys.getInt(1);

            PreparedStatement psAcc = con.prepareStatement(
                    "insert into accounts(customer_id,balance) values (?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            psAcc.setInt(1, customerId);
            psAcc.setDouble(2, opening);
            int row2 = psAcc.executeUpdate();

            ResultSet accKeys = psAcc.getGeneratedKeys();
            accKeys.next();
            int accNo = accKeys.getInt(1);

            if (row1 > 0 && row2 > 0) {
                saveTxn(con, accNo, "DEPOSIT", opening);
                con.commit();
                System.out.println("Account created successfully. Account No: " + accNo);
            } else {
                con.rollback();
                System.out.println("Account creation failed");
            }
        } catch (SQLException e) {
            rollback(con);
            if (e.getErrorCode() == 1062) {
                System.out.println("This phone number is already registered");
            } else {
                System.out.println("Error: " + e.getMessage());
            }
        } finally {
            autoCommitOn(con);
        }
    }


    public static void deposit(Connection con, Scanner sc) {
        System.out.print("Enter account number: ");
        String acc = sc.nextLine().trim();
        if (!Validator.isNumber(acc)) {
            System.out.println("Invalid account number.");
            return;
        }

        System.out.print("Enter amount to deposit: ");
        String amt = sc.nextLine().trim();
        if (!Validator.isValidAmount(amt)) {
            System.out.println("Invalid amount.");
            return;
        }

        int accNo = Integer.parseInt(acc);
        double amount = Double.parseDouble(amt);

        try {
            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement(
                    "update accounts set balance = balance + ? where account_no=?");
            ps.setDouble(1, amount);
            ps.setInt(2, accNo);
            int row = ps.executeUpdate();

            if (row > 0) {
                saveTxn(con, accNo, "DEPOSIT", amount);
                con.commit();
                System.out.println("Deposited successfully. New balance: "
                        + getBalance(con, accNo));
            } else {
                con.rollback();
                System.out.println("Account not found");
            }
        } catch (SQLException e) {
            rollback(con);
            System.out.println("Error: " + e.getMessage());
        } finally {
            autoCommitOn(con);
        }
    }


    public static void withdraw(Connection con, Scanner sc) {
        System.out.print("Enter account number: ");
        String acc = sc.nextLine().trim();
        if (!Validator.isNumber(acc)) {
            System.out.println("Invalid account number.");
            return;
        }

        System.out.print("Enter amount to withdraw: ");
        String amt = sc.nextLine().trim();
        if (!Validator.isValidAmount(amt)) {
            System.out.println("Invalid amount.");
            return;
        }

        int accNo = Integer.parseInt(acc);
        double amount = Double.parseDouble(amt);

        try {
            con.setAutoCommit(false);

            double balance = getBalance(con, accNo);
            if (balance == -1) {
                con.rollback();
                System.out.println("Account not found");
                return;
            }
            if (balance < amount) {
                con.rollback();
                System.out.println("Insufficient balance. Available: " + balance);
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                    "update accounts set balance = balance - ? where account_no=?");
            ps.setDouble(1, amount);
            ps.setInt(2, accNo);
            int row = ps.executeUpdate();

            if (row > 0) {
                saveTxn(con, accNo, "WITHDRAW", amount);
                con.commit();
                System.out.println("Withdrawn successfully. New balance: "
                        + getBalance(con, accNo));
            } else {
                con.rollback();
                System.out.println("Withdrawal failed");
            }
        } catch (SQLException e) {
            rollback(con);
            System.out.println("Error: " + e.getMessage());
        } finally {
            autoCommitOn(con);
        }
    }


    public static void transfer(Connection con, Scanner sc) {
        System.out.print("Enter FROM account number: ");
        String fromStr = sc.nextLine().trim();
        System.out.print("Enter TO account number: ");
        String toStr = sc.nextLine().trim();

        if (!Validator.isNumber(fromStr) || !Validator.isNumber(toStr)) {
            System.out.println("Invalid account number.");
            return;
        }

        System.out.print("Enter amount to transfer: ");
        String amt = sc.nextLine().trim();
        if (!Validator.isValidAmount(amt)) {
            System.out.println("Invalid amount.");
            return;
        }

        int from = Integer.parseInt(fromStr);
        int to = Integer.parseInt(toStr);
        double amount = Double.parseDouble(amt);

        if (from == to) {
            System.out.println("Cannot transfer to the same account");
            return;
        }

        try {
            con.setAutoCommit(false);

            double fromBal = getBalance(con, from);
            double toBal = getBalance(con, to);

            if (fromBal == -1 || toBal == -1) {
                con.rollback();
                System.out.println("Account not found");
                return;
            }
            if (fromBal < amount) {
                con.rollback();
                System.out.println("Insufficient balance. Available: " + fromBal);
                return;
            }

           
            PreparedStatement psDebit = con.prepareStatement(
                    "update accounts set balance = balance - ? where account_no=?");
            psDebit.setDouble(1, amount);
            psDebit.setInt(2, from);
            int r1 = psDebit.executeUpdate();

    
            PreparedStatement psCredit = con.prepareStatement(
                    "update accounts set balance = balance + ? where account_no=?");
            psCredit.setDouble(1, amount);
            psCredit.setInt(2, to);
            int r2 = psCredit.executeUpdate();

            if (r1 > 0 && r2 > 0) {
                saveTxn(con, from, "TRANSFER_OUT", amount);
                saveTxn(con, to, "TRANSFER_IN", amount);
                con.commit();
                System.out.println("Transfer successful");
            } else {
                con.rollback();
                System.out.println("Transfer failed. No money was deducted.");
            }
        } catch (SQLException e) {
            rollback(con);
            System.out.println("Transfer failed. No money was deducted. " + e.getMessage());
        } finally {
            autoCommitOn(con);
        }
    }

   

    public static void history(Connection con, Scanner sc) {
        System.out.print("Enter account number: ");
        String acc = sc.nextLine().trim();
        if (!Validator.isNumber(acc)) {
            System.out.println("Invalid account number.");
            return;
        }

        try {
            PreparedStatement ps = con.prepareStatement(
                    "select txn_id, txn_type, amount, txn_date from transactions "
                            + "where account_no=? order by txn_id desc");
            ps.setInt(1, Integer.parseInt(acc));
            ResultSet rs = ps.executeQuery();

            BankQueries.display(rs);

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}