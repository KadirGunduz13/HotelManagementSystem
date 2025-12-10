package com.hotel.models;

public class Staff extends User {
    // Constructor güncellendi
    public Staff(int id, String tcNo, String username, String password, String fullName, String phone, String email) {
        super(id, tcNo, username, password, fullName, phone, email, "ADMIN");
    }

    @Override
    public void showMenu() {
        System.out.println("=== Personel Yönetim Paneli ===");
    }
}