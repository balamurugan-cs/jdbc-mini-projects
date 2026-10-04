package hospital;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class HospitalQueries {

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

   
    public static void appointmentDetails(Connection con) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "select ap.appointment_id, p.name as patient, d.name as doctor, "
                            + "d.specialization, ap.appointment_date, ap.status "
                            + "from appointments ap "
                            + "join patients p on ap.patient_id = p.patient_id "
                            + "join doctors d on ap.doctor_id = d.doctor_id "
                            + "order by ap.appointment_date");
            display(rs);
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void prescriptionDetails(Connection con) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "select pr.prescription_id, p.name as patient, d.name as doctor, "
                            + "ap.appointment_date, pr.medicine, pr.dosage, pr.notes "
                            + "from prescriptions pr "
                            + "join appointments ap on pr.appointment_id = ap.appointment_id "
                            + "join patients p on ap.patient_id = p.patient_id "
                            + "join doctors d on ap.doctor_id = d.doctor_id");
            display(rs);
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}