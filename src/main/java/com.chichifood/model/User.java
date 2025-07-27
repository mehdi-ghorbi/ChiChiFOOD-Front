package com.chichifood.model;

public class User {
    private String fullName;
    private String phone;
    private int id;
    private String email;
    private String password;
    private String role;
    private String address;
    private String bankName;
    private String photo;
    private String accountNumber;
    private int isUserConfirmed;

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }


    public User(String fullName, String phone, String email, String role,
                String address, String bankName, String accountNumber, String photo, String password) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.address = address;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.photo = photo;
        this.password = password;

    }
    public User(String fullName, String phone, String email, String role,
                String address, String bankName, String accountNumber, String photo) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.address = address;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.photo = photo;
    }

    public User(int id, String fullName, int isUserConfirmed) {
        this.fullName = fullName;
        this.isUserConfirmed = isUserConfirmed;
        this.id = id;

    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getAddress() {
        return address;
    }

    public String getBankName() {
        return bankName;
    }

    public int getId() {
        return id;
    }
    public int getIsUserConfirmed() {
        return isUserConfirmed;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIsUserConfirmed(int isUserConfirmed) {
        this.isUserConfirmed = isUserConfirmed;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    // --- Setter ها (در صورت نیاز)
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}

