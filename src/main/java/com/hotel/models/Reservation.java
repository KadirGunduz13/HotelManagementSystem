package com.hotel.models;

import java.sql.Date;

public class Reservation {
    private int id;
    private int customerId;
    private int roomId;
    private Date checkInDate;
    private Date checkOutDate;
    private double totalPrice;
    private String status;

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

    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public int getRoomId() { return roomId; }
    public Date getCheckInDate() { return checkInDate; }
    public Date getCheckOutDate() { return checkOutDate; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
}