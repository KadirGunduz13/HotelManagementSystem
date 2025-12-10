package com.hotel.models;

public class FamilyRoom extends Room {
    public FamilyRoom(int id, String roomNumber, double price, String status) {
        // Aile odası varsayılan kapasitesi: 5 olsun
        super(id, roomNumber, price, 5, status);
    }

    @Override
    public String getType() {
        return "FAMILY";
    }
}