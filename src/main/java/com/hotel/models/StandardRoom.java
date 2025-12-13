package com.hotel.models;

public class StandardRoom extends Room {
    public StandardRoom(int id, String roomNumber, double price, int capacity, String status) {
        super(id, roomNumber, price, capacity, status);
    }

    @Override
    public String getType() {
        return "STANDARD";
    }
}