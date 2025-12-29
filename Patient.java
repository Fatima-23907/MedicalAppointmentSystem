package model;

public class Patient {
    private int id;
    private String name;
    private int age;
    private String disease;
    private String phone;

    // Constructor
    public Patient(int id, String name, int age, String disease, String phone) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.disease = disease;
        this.phone = phone;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", disease='" + disease + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    // Validation methods
    public static boolean isValidPhone(String phone) {
        return phone.matches("^\\d{11}$"); // 11 digit Pakistani phone number
    }

    public static boolean isValidAge(int age) {
        return age > 0 && age < 120;
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.matches("^[a-zA-Z\\s]+$");
    }
}