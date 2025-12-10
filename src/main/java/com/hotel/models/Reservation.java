package com.hotel.models;

import java.sql.Date;

public class Reservation {
    private int id;
    private int customerId;
    private int roomId;
    private Date checkInDate;
    private Date checkOutDate;
    private double totalPrice;
    private String status; // PENDING, CHECKED_IN, CHECKED_OUT, CANCELLED

    // Ekstra Bilgiler (Tabloda göstermek için)
    private String customerName;
    private String roomNumber;

    public Reservation(int id, int customerId, int roomId, Date checkInDate, Date checkOutDate, double totalPrice, String status) {
        this.id = id;
        this.customerId = customerId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // --- Getter ve Setter Metotları ---
    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public int getRoomId() { return roomId; }
    public Date getCheckInDate() { return checkInDate; }
    public Date getCheckOutDate() { return checkOutDate; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }

    // Tabloda isim ve oda no göstermek için setter'lar
    public void setCustomerName(String name) { this.customerName = name; }
    public String getCustomerName() { return customerName; }

    public void setRoomNumber(String num) { this.roomNumber = num; }
    public String getRoomNumber() { return roomNumber; }
}