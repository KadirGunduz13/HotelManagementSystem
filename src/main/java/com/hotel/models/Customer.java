package com.hotel.models;

public class Customer extends User {
    // Constructor güncellendi
    public Customer(int id, String tcNo, String username, String password, String fullName, String phone, String email) {
        super(id, tcNo, username, password, fullName, phone, email, "CUSTOMER");
    }

    @Override
    public void showMenu() {
        System.out.println("=== Müşteri Paneli ===");
    }
}