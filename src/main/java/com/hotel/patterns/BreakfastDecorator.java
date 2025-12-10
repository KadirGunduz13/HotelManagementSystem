package com.hotel.patterns;

public class BreakfastDecorator implements ICost {
    private ICost wrappedObj; // Kaplanan obje
    private long days;
    private final double BREAKFAST_PRICE = 500.0; // Günlük kahvaltı ücreti

    public BreakfastDecorator(ICost wrappedObj, long days) {
        this.wrappedObj = wrappedObj;
        this.days = days;
    }

    @Override
    public double getCost() {
        // Mevcut fiyata + (Gün Sayısı * Kahvaltı Ücreti) ekle
        return wrappedObj.getCost() + (days * BREAKFAST_PRICE);
    }
}