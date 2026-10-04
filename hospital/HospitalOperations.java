package hospital;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

public class HospitalOperations {

    
    private static boolean exists(Connection con, String sql, int id) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        boolean found = rs.next();
        rs.close();
        ps.close();
        return found;
    }


    public static void registerPatient(Connection con, Scanner sc) {
        System.out.print("Enter patient name: ");
        String name = sc.nextLine().trim();
        if (!Validator.isValidName(name)) {
            System.out.println("Invalid name. Use letters only.");
            return;
        }

        System.out.print("Enter age: ");
        String age = sc.nextLine().trim();
        if (!Validator.isValidAge(age)) {
            System.out.println("Invalid age. Enter 1 to 120.");
            return;
        }

        System.out.print("Enter gender (Male/Female/Other): ");
        String gender = sc.nextLine().trim();
        if (!Validator.isValidGender(gender)) {
            System.out.println("Invalid gender.");
            return;
        }

        System.out.print("Enter phone (10 digits): ");
        String phone = sc.nextLine().trim();
        if (!Validator.isValidPhone(phone)) {
            System.out.println("Invalid phone. Enter exactly 10 digits.");
            return;
        }

        try {
            PreparedStatement ps = con.prepareStatement(
                    "insert into patients(name,age,gender,phone) values (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setInt(2, Integer.parseInt(age));
            ps.setString(3, gender);
            ps.setString(4, phone);
            int row = ps.executeUpdate();

            if (row > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                System.out.println("Patient registered successfully. Patient ID: "
                        + keys.getInt(1));
            } else {
                System.out.println("Registration failed");
            }
            ps.close();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("This phone number is already registered");
            } else {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

   
    public static void viewPatients(Connection con) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from patients");
            HospitalQueries.display(rs);
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public static void updatePatientPhone(Connection con, Scanner sc) {
        System.out.print("Enter patient ID: ");
        String id = sc.nextLine().trim();
        if (!Validator.isNumber(id)) {
            System.out.println("Invalid patient ID.");
            return;
        }

        System.out.print("Enter new phone (10 digits): ");
        String phone = sc.nextLine().trim();
        if (!Validator.isValidPhone(phone)) {
            System.out.println("Invalid phone. Enter exactly 10 digits.");
            return;
        }

        try {
            PreparedStatement ps = con.prepareStatement(
                    "update patients set phone=? where patient_id=?");
            ps.setString(1, phone);
            ps.setInt(2, Integer.parseInt(id));
            int row = ps.executeUpdate();

            if (row > 0) {
                System.out.println("Patient phone updated");
            } else {
                System.out.println("Patient not found");
            }
            ps.close();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("This phone number is already used by another patient");
            } else {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

  
    public static void deletePatient(Connection con, Scanner sc) {
        System.out.print("Enter patient ID to delete: ");
        String id = sc.nextLine().trim();
        if (!Validator.isNumber(id)) {
            System.out.println("Invalid patient ID.");
            return;
        }

        try {
            PreparedStatement ps = con.prepareStatement(
                    "delete from patients where patient_id=?");
            ps.setInt(1, Integer.parseInt(id));
            int row = ps.executeUpdate();

            if (row > 0) {
                System.out.println("Patient deleted");
            } else {
                System.out.println("Patient not found");
            }
            ps.close();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                System.out.println("Cannot delete. This patient has appointments.");
            } else {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


    public static void addDoctor(Connection con, Scanner sc) {
        System.out.print("Enter doctor name: ");
        String name = sc.nextLine().trim();
        if (!Validator.isValidName(name)) {
            System.out.println("Invalid name. Use letters only.");
            return;
        }

        System.out.print("Enter specialization: ");
        String spec = sc.nextLine().trim();
        if (!Validator.isValidName(spec)) {
            System.out.println("Invalid specialization. Use letters only.");
            return;
        }

        System.out.print("Enter phone (10 digits): ");
        String phone = sc.nextLine().trim();
        if (!Validator.isValidPhone(phone)) {
            System.out.println("Invalid phone. Enter exactly 10 digits.");
            return;
        }

        try {
            PreparedStatement ps = con.prepareStatement(
                    "insert into doctors(name,specialization,phone) values (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, spec);
            ps.setString(3, phone);
            int row = ps.executeUpdate();

            if (row > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                System.out.println("Doctor added successfully. Doctor ID: "
                        + keys.getInt(1));
            } else {
                System.out.println("Adding doctor failed");
            }
            ps.close();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("This phone number is already registered");
            } else {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // ---------- 6. VIEW ALL DOCTORS ----------

    public static void viewDoctors(Connection con) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from doctors");
            HospitalQueries.display(rs);
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public static void bookAppointment(Connection con, Scanner sc) {
        System.out.print("Enter patient ID: ");
        String pid = sc.nextLine().trim();
        System.out.print("Enter doctor ID: ");
        String did = sc.nextLine().trim();

        if (!Validator.isNumber(pid) || !Validator.isNumber(did)) {
            System.out.println("Invalid ID.");
            return;
        }

        System.out.print("Enter appointment date (yyyy-mm-dd): ");
        String date = sc.nextLine().trim();
        if (!Validator.isValidDate(date)) {
            System.out.println("Invalid date. Use yyyy-mm-dd, today or later.");
            return;
        }

        try {
            int patientId = Integer.parseInt(pid);
            int doctorId = Integer.parseInt(did);

            if (!exists(con, "select 1 from patients where patient_id=?", patientId)) {
                System.out.println("Patient not found");
                return;
            }
            if (!exists(con, "select 1 from doctors where doctor_id=?", doctorId)) {
                System.out.println("Doctor not found");
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                    "insert into appointments(patient_id,doctor_id,appointment_date) values (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setDate(3, Date.valueOf(LocalDate.parse(date)));
            int row = ps.executeUpdate();

            if (row > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                System.out.println("Appointment booked. Appointment ID: " + keys.getInt(1));
            } else {
                System.out.println("Booking failed");
            }
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    
    public static void addPrescription(Connection con, Scanner sc) {
        System.out.print("Enter appointment ID: ");
        String aid = sc.nextLine().trim();
        if (!Validator.isNumber(aid)) {
            System.out.println("Invalid appointment ID.");
            return;
        }

        System.out.print("Enter medicine name: ");
        String medicine = sc.nextLine().trim();
        if (!Validator.isNotEmpty(medicine)) {
            System.out.println("Medicine name cannot be empty.");
            return;
        }

        System.out.print("Enter dosage (example: 1-0-1 after food): ");
        String dosage = sc.nextLine().trim();
        if (!Validator.isNotEmpty(dosage)) {
            System.out.println("Dosage cannot be empty.");
            return;
        }

        System.out.print("Enter notes (press Enter to skip): ");
        String notes = sc.nextLine().trim();
        if (!Validator.isNotEmpty(notes)) {
            notes = "-";
        }

        try {
            int appointmentId = Integer.parseInt(aid);

            if (!exists(con, "select 1 from appointments where appointment_id=?",
                    appointmentId)) {
                System.out.println("Appointment not found");
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                    "insert into prescriptions(appointment_id,medicine,dosage,notes) values (?,?,?,?)");
            ps.setInt(1, appointmentId);
            ps.setString(2, medicine);
            ps.setString(3, dosage);
            ps.setString(4, notes);
            int row = ps.executeUpdate();

            if (row > 0) {
                System.out.println("Prescription added successfully");
            } else {
                System.out.println("Adding prescription failed");
            }
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}