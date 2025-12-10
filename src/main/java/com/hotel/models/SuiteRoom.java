package com.hotel.models;

public class SuiteRoom extends Room {
    public SuiteRoom(int id, String roomNumber, double price, String status) {
        // Suit oda varsayılan kapasitesi: 4
        super(id, roomNumber, price, 4, status);
    }

    @Override
    public String getType() {
        return "SUITE";
    }
}