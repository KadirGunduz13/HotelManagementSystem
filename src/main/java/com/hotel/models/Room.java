package com.hotel.models;

public abstract class Room {
    protected int id;
    protected String roomNumber;
    protected double price;
    protected int capacity;
    protected String status;

    public Room(int id, String roomNumber, double price, int capacity, String status) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.price = price;
        this.capacity = capacity;
        this.status = status;
    }

    public String getInfo() {
        return String.format("Oda: %s | Tip: %s | Kapasite: %d | Fiyat: %.2f TL",
                roomNumber, this.getClass().getSimpleName(), capacity, price);
    }

    public abstract String getType();

    public int getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public double getPrice() { return price; }
    public int getCapacity() { return capacity; }

    public String getStatus() { return status; }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public void setId(int id) {
        this.id = id;
    }
}