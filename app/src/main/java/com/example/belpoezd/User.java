package com.example.belpoezd;

public class User {
    private String email;
    private String lastName;
    private String firstName;
    private String patronymic;
    private String passportNumber;
    private String passportSeries;
    private String uid;  // Добавлено поле для хранения идентификатора пользователя

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String lastName, String firstName, String patronymic, String passportNumber, String passportSeries, String uid) {
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.passportNumber = passportNumber;
        this.passportSeries = passportSeries;
        this.uid = uid;
    }

    public User(String lastName, String firstName, String patronymic, String passportNumber, String passportSeries, String email) {
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.passportNumber = passportNumber;
        this.passportSeries = passportSeries;
        this.uid = uid;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public String getPassportSeries() {
        return passportSeries;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getUid() {
        return uid;
    }
}
