package com.hotel.models;

public class SuiteRoom extends Room {
    public SuiteRoom(int id, String roomNumber, double price, int capacity, String status) {
        // Sabit 4 yerine 'capacity' kullanıyoruz
        super(id, roomNumber, price, capacity, status);
    }

    @Override
    public String getType() {
        return "SUITE";
    }
}