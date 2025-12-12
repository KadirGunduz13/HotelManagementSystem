package com.hotel.models;

public class StandardRoom extends Room {
    // Constructor'a 'int capacity' eklendi
    public StandardRoom(int id, String roomNumber, double price, int capacity, String status) {
        // super(...) içine sabit 2 yerine gelen 'capacity' değişkenini koyduk
        super(id, roomNumber, price, capacity, status);
    }

    @Override
    public String getType() {
        return "STANDARD";
    }
}