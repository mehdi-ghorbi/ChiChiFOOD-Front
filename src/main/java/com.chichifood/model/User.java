package com.chichifood.model;

public class User {
    private String fullName;
    private String phone;
    private String email;
    private String password;
    private String role;
    private String address;
    private String bankName;
    private String accountNumber;

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public User(String fullName, String phone, String email, String password, String role,
                String address, String bankName, String accountNumber) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.role = role;
        this.address = address;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
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

