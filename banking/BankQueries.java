package banking;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class BankQueries {

    public static void display(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();
        int count = 0;

        for (int i = 1; i <= cols; i++) {
            System.out.print(md.getColumnLabel(i) + "\t");
        }
        System.out.println();
        System.out.println("----------------------------------------------");

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

    
    public static void accountDetails(Connection con) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "select a.account_no, c.name, c.phone, c.email, a.balance "
                            + "from accounts a join customers c "
                            + "on a.customer_id = c.customer_id");
            display(rs);
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void bankSummary(Connection con) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "select count(*) as total_accounts, "
                            + "sum(balance) as total_balance, "
                            + "max(balance) as highest_balance, "
                            + "avg(balance) as average_balance "
                            + "from accounts");
            display(rs);
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}