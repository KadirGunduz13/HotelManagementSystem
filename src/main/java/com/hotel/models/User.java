package com.hotel.models;

public abstract class User {
    protected int id;
    protected String tcNo;
    protected String username;
    protected String password;
    protected String fullName;
    protected String phone; // YENİ
    protected String email; // YENİ
    protected String role;

    // Constructor güncellendi: phone ve email eklendi
    public User(int id, String tcNo, String username, String password, String fullName, String phone, String email, String role) {
        this.id = id;
        this.tcNo = tcNo;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }

    public abstract void showMenu();

    // Getter Metotları
    public int getId() { return id; }
    public String getTcNo() { return tcNo; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; } // YENİ
    public String getEmail() { return email; } // YENİ
    public String getRole() { return role; }
}