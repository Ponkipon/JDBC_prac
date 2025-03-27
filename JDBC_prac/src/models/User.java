package models; 

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    // that's all you need to create the object. gotta make another constructor if you don't need smth hehehe
    public User(int id, String firstName, String lastName, String email, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    // Getters
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

//    // Setters, useless really.
//    public void setFirstName(String firstName) { this.firstName = firstName; }
//    public void setLastName(String lastName) { this.lastName = lastName; }
//    public void setEmail(String email) { this.email = email; }
//    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + email + ")";
    }
}
