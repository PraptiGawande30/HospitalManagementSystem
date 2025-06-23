package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem
{
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "sa123";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Scanner scanner = new Scanner(System.in);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection,scanner);
            while(true)
            {
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1.Add Patient");
                System.out.println("2.view Patients");
                System.out.println("3.view doctor");
                System.out.println("4.Book Appointment");
                System.out.println("5.Exit");
                System.out.println("Enter your choice");
                int choice = scanner.nextInt();

                switch(choice){
                    case 1:
                        patient.addPatient();
                        System.out.println();
                    case 2:
                        patient.viewPatients();
                        System.out.println();
                    case 3:
                        doctor.viewDoctor();
                        System.out.println();
                    case 4:
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                    case 5:
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice");
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner){
        System.out.println("Enter patient id");
        int patientId = scanner.nextInt();
        System.out.println("Enter doctor id");
        int doctorId = scanner.nextInt();
        System.out.println("Enter appointment date (yyyy-mm-dd)");
        String appointmentDate = scanner.next();

        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            if(checkDoctorAvailability(doctorId, appointmentDate, connection)){
                String appointmentQuery = "INSERT INTO appointment(patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0){
                        System.out.println("Appointment booked");
                    } else {
                        System.out.println("Failed to book appointment");
                    }
                } catch(SQLException e) {
                    System.err.println(e.getMessage());
                }
            } else {
                System.out.println("doctor is not available");
            }
        } else {
            System.out.println("either doctor or patient doesn't exist!!!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId , String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointment WHERE doctor_id = ? AND appointment_date = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt(1) == 0;
            }
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
}
