package banking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class BankingApp {

    static final String URL = "jdbc:mysql://localhost:3306/bank_db";
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
                System.out.println("===== BANKING SYSTEM =====");
                System.out.println("1. Create Account");
                System.out.println("2. Deposit");
                System.out.println("3. Withdraw");
                System.out.println("4. Fund Transfer");
                System.out.println("5. Transaction History");
                System.out.println("6. Account Details");
                System.out.println("7. Bank Summary");
                System.out.println("8. Exit");
                System.out.print("Enter your choice: ");

                String input = sc.nextLine().trim();
                if (!Validator.isNumber(input)) {
                    System.out.println("Enter a number from 1 to 8");
                    ch = 0;
                    continue;
                }
                ch = Integer.parseInt(input);

                switch (ch) {
                    case 1:
                        BankOperations.createAccount(con, sc);
                        break;
                    case 2:
                        BankOperations.deposit(con, sc);
                        break;
                    case 3:
                        BankOperations.withdraw(con, sc);
                        break;
                    case 4:
                        BankOperations.transfer(con, sc);
                        break;
                    case 5:
                        BankOperations.history(con, sc);
                        break;
                    case 6:
                        BankQueries.accountDetails(con);
                        break;
                    case 7:
                        BankQueries.bankSummary(con);
                        break;
                    case 8:
                        System.out.println("Thank you. Exited.");
                        break;
                    default:
                        System.out.println("Invalid choice");
                }
            } while (ch != 8);

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