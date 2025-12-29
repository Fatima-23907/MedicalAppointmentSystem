package controller;

import Service.CppIntegrationService;
import model.Patient;
import javax.swing.*;
import java.util.List;

public class PatientController {

    public static boolean validatePatientData(String name, String ageStr, String disease, String phone) {
        // Name validation
        if (!Patient.isValidName(name)) {
            JOptionPane.showMessageDialog(null,
                    "Invalid name! Only alphabets and spaces allowed.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Age validation
        try {
            int age = Integer.parseInt(ageStr);
            if (!Patient.isValidAge(age)) {
                JOptionPane.showMessageDialog(null,
                        "Invalid age! Must be between 1 and 119.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Invalid age! Must be a number.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Disease validation
        if (disease == null || disease.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Disease field cannot be empty!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Phone validation
        if (!Patient.isValidPhone(phone)) {
            JOptionPane.showMessageDialog(null,
                    "Invalid phone number! Must be 11 digits.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public static void addPatient(int id, String name, int age, String disease, String phone) {
        String result = CppIntegrationService.addPatient(id, name, age, disease, phone);

        if (result.contains("SUCCESS")) {
            JOptionPane.showMessageDialog(null,
                    "Patient added successfully!\n" + result,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            System.out.println(result);
            JOptionPane.showMessageDialog(null,
                    "Failed to add patient:\n" + result,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        System.out.println("program reached here!");
    }

    public static String searchPatient(int id) {
        String result = CppIntegrationService.searchPatient(id);

        if (result.contains("NOT_FOUND")) {
            return "Patient not found with ID: " + id;
        } else if (result.contains("FOUND")) {
            String[] lines = result.split("\n");
            if (lines.length > 1) {
                return "Patient Found:\n" + formatPatientData(lines[1]);
            }
        }
        return result;
    }

    public static List<String> getAllPatientsSorted() {
        return CppIntegrationService.getAllPatientsSorted();
    }

    private static String formatPatientData(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length >= 5) {
            return String.format(
                    "ID: %s\nName: %s\nAge: %s\nDisease: %s\nPhone: %s",
                    parts[0], parts[1], parts[2], parts[3], parts[4]
            );
        }
        return csvLine;
    }
}