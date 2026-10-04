package hospital;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TableSetup {

    public static void createTables(Connection con) throws SQLException {
        Statement st = con.createStatement();

        st.executeUpdate("create table if not exists patients ("
                + "patient_id int primary key auto_increment,"
                + "name varchar(50) not null,"
                + "age int not null,"
                + "gender varchar(10) not null,"
                + "phone varchar(10) not null unique)");

        st.executeUpdate("create table if not exists doctors ("
                + "doctor_id int primary key auto_increment,"
                + "name varchar(50) not null,"
                + "specialization varchar(50) not null,"
                + "phone varchar(10) not null unique)");

        st.executeUpdate("create table if not exists appointments ("
                + "appointment_id int primary key auto_increment,"
                + "patient_id int not null,"
                + "doctor_id int not null,"
                + "appointment_date date not null,"
                + "status varchar(20) default 'BOOKED',"
                + "foreign key (patient_id) references patients(patient_id),"
                + "foreign key (doctor_id) references doctors(doctor_id))");

        st.executeUpdate("create table if not exists prescriptions ("
                + "prescription_id int primary key auto_increment,"
                + "appointment_id int not null,"
                + "medicine varchar(100) not null,"
                + "dosage varchar(50) not null,"
                + "notes varchar(200),"
                + "foreign key (appointment_id) references appointments(appointment_id))");

        st.close();
        System.out.println("Tables are ready");
    }
}