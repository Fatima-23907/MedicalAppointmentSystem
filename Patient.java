package model;

/**
 * Model class representing a Patient entity
 * Used across GUI, file handling and business logic layers
 */
public class Patient {

    private int id;
    private String name;
    private int age;
    private String disease;
    private String phone;

    /* ==============================
       CONSTRUCTOR
       ============================== */

    public Patient(int id, String name, int age,
                   String disease, String phone) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.disease = disease;
        this.phone = phone;
    }

    /* ==============================
       GETTERS & SETTERS
       ============================== */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /* ==============================
       VALIDATION UTILITIES
       ============================== */

    public static boolean isValidPhone(String phone) {
        // Pakistani phone number (11 digits)
        return phone != null && phone.matches("\\d{11}");
    }

    public static boolean isValidAge(int age) {
        return age > 0 && age < 120;
    }

    public static boolean isValidName(String name) {
        return name != null &&
               !name.trim().isEmpty() &&
               name.matches("[a-zA-Z\\s]+");
    }

    /* ==============================
       OBJECT REPRESENTATION
       ============================== */

    @Override
    public String toString() {
        return String.format(
            "Patient{id=%d, name='%s', age=%d, disease='%s', phone='%s'}",
            id, name, age, disease, phone
        );
    }
}
