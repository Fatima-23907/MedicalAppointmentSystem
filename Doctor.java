package model;

public class Doctor {
    private int id;
    private String name;
    private String specialization;
    private int appointmentCount;

    // Constructor
    public Doctor(int id, String name, String specialization, int appointmentCount) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.appointmentCount = appointmentCount;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public int getAppointmentCount() { return appointmentCount; }
    public void setAppointmentCount(int appointmentCount) { this.appointmentCount = appointmentCount; }

    @Override
    public String toString() {
        return "Dr. " + name + " (" + specialization + ") - Load: " + appointmentCount;
    }
}