package com.hotel.services;

import com.hotel.models.*;

public class RoomFactory {

    public static Room createRoom(String type, int capacity, String roomNumber, double price, String status) {
        Room room = null;

        // Artık kapasiteyi de parametre olarak gönderiyoruz
        switch (type.toUpperCase()) {
            case "STANDARD":
                room = new StandardRoom(0, roomNumber, price, capacity, status);
                break;
            case "SUITE":
                room = new SuiteRoom(0, roomNumber, price, capacity, status);
                break;
            case "FAMILY":
                room = new FamilyRoom(0, roomNumber, price, capacity, status);
                break;
            default:
                room = new StandardRoom(0, roomNumber, price, capacity, status);
                break;
        }

        return room;
    }
}