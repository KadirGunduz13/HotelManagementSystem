package com.hotel.patterns;

public class BreakfastDecorator implements ICost {
    private ICost wrappedObj;
    private long days;
    private final double BREAKFAST_PRICE = 500.0;

    public BreakfastDecorator(ICost wrappedObj, long days) {
        this.wrappedObj = wrappedObj;
        this.days = days;
    }

    @Override
    public double getCost() {
        return wrappedObj.getCost() + (days * BREAKFAST_PRICE);
    }
}