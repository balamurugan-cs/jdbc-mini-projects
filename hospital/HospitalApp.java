package hospital;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class HospitalApp {

    static final String URL = "jdbc:mysql://localhost:3306/hospital_db";
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
                System.out.println("===== HOSPITAL SYSTEM =====");
                System.out.println("1. Register Patient");
                System.out.println("2. View All Patients");
                System.out.println("3. Update Patient Phone");
                System.out.println("4. Delete Patient");
                System.out.println("5. Add Doctor");
                System.out.println("6. View All Doctors");
                System.out.println("7. Book Appointment");
                System.out.println("8. Add Prescription");
                System.out.println("9. Appointment Details");
                System.out.println("10. Prescription Details");
                System.out.println("11. Exit");
                System.out.print("Enter your choice: ");

                String input = sc.nextLine().trim();
                if (!Validator.isNumber(input)) {
                    System.out.println("Enter a number from 1 to 11");
                    ch = 0;
                    continue;
                }
                ch = Integer.parseInt(input);

                switch (ch) {
                    case 1:
                        HospitalOperations.registerPatient(con, sc);
                        break;
                    case 2:
                        HospitalOperations.viewPatients(con);
                        break;
                    case 3:
                        HospitalOperations.updatePatientPhone(con, sc);
                        break;
                    case 4:
                        HospitalOperations.deletePatient(con, sc);
                        break;
                    case 5:
                        HospitalOperations.addDoctor(con, sc);
                        break;
                    case 6:
                        HospitalOperations.viewDoctors(con);
                        break;
                    case 7:
                        HospitalOperations.bookAppointment(con, sc);
                        break;
                    case 8:
                        HospitalOperations.addPrescription(con, sc);
                        break;
                    case 9:
                        HospitalQueries.appointmentDetails(con);
                        break;
                    case 10:
                        HospitalQueries.prescriptionDetails(con);
                        break;
                    case 11:
                        System.out.println("Thank you. Exited.");
                        break;
                    default:
                        System.out.println("Invalid choice");
                }
            } while (ch != 11);

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