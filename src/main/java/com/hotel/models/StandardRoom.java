package com.hotel.models;

public class StandardRoom extends Room {
    public StandardRoom(int id, String roomNumber, double price, String status) {
        // Standart oda varsayılan kapasitesi: 2
        super(id, roomNumber, price, 2, status);
    }

    @Override
    public String getType() {
        return "STANDARD";
    }
}