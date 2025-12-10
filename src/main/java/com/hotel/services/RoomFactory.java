package com.hotel.services;

import com.hotel.models.*;

public class RoomFactory {

    public static Room createRoom(String type, int id, String roomNumber, double price, String status) {
        if (type == null) {
            return null;
        }

        switch (type.toUpperCase()) {
            case "STANDARD":
                return new StandardRoom(id, roomNumber, price, status);
            case "SUITE":
                return new SuiteRoom(id, roomNumber, price, status);
            case "FAMILY": // <--- YENİ EKLENEN KISIM
                return new FamilyRoom(id, roomNumber, price, status);
            default:
                throw new IllegalArgumentException("Bilinmeyen oda tipi: " + type);
        }
    }
}