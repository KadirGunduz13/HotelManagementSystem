package com.hotel.patterns;

public class BaseReservationCost implements ICost {
    private double dailyPrice;
    private long days;

    public BaseReservationCost(double dailyPrice, long days) {
        this.dailyPrice = dailyPrice;
        this.days = days;
    }

    @Override
    public double getCost() {
        return dailyPrice * days;
    }
}