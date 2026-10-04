package payroll;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TableSetup {

    public static void createTables(Connection con) throws SQLException {
        Statement st = con.createStatement();

        st.executeUpdate("create table if not exists employees ("
                + "emp_id int primary key auto_increment,"
                + "name varchar(50) not null,"
                + "email varchar(100) not null unique,"
                + "department varchar(50) not null)");

        st.executeUpdate("create table if not exists salaries ("
                + "salary_id int primary key auto_increment,"
                + "emp_id int not null unique,"
                + "basic_salary decimal(10,2) not null,"
                + "allowances decimal(10,2) not null default 0,"
                + "foreign key (emp_id) references employees(emp_id))");

        st.executeUpdate("create table if not exists payroll ("
                + "payroll_id int primary key auto_increment,"
                + "emp_id int not null,"
                + "month_year varchar(7) not null,"
                + "gross_salary decimal(10,2) not null,"
                + "tax_amount decimal(10,2) not null,"
                + "net_salary decimal(10,2) not null,"
                + "generated_on timestamp default current_timestamp,"
                + "unique (emp_id, month_year),"
                + "foreign key (emp_id) references employees(emp_id))");

        st.close();
        System.out.println("Tables are ready");
    }
}