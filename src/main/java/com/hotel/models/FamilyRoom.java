package com.hotel.models;

public class FamilyRoom extends Room {
    public FamilyRoom(int id, String roomNumber, double price, int capacity, String status) {
        super(id, roomNumber, price, capacity, status);
    }

    @Override
    public String getType() {
        return "FAMILY";
    }
}