package banking;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TableSetup {

    public static void createTables(Connection con) throws SQLException {
        Statement st = con.createStatement();

        st.executeUpdate("create table if not exists customers ("
                + "customer_id int primary key auto_increment,"
                + "name varchar(50) not null,"
                + "phone varchar(10) not null unique,"
                + "email varchar(100) not null)");

        st.executeUpdate("create table if not exists accounts ("
                + "account_no int primary key auto_increment,"
                + "customer_id int not null,"
                + "balance double not null default 0,"
                + "foreign key (customer_id) references customers(customer_id))"
                + " auto_increment=1001");

        st.executeUpdate("create table if not exists transactions ("
                + "txn_id int primary key auto_increment,"
                + "account_no int not null,"
                + "txn_type varchar(20) not null,"
                + "amount double not null,"
                + "txn_date timestamp default current_timestamp,"
                + "foreign key (account_no) references accounts(account_no))");

        st.close();
        System.out.println("Tables are ready");
    }
}