package com.hotel.models;

public abstract class User {
    protected int id;
    protected String tcNo;
    protected String username;
    protected String password;
    protected String fullName;
    protected String phone;
    protected String email;
    protected String role;

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

    public int getId() { return id; }
    public String getTcNo() { return tcNo; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}