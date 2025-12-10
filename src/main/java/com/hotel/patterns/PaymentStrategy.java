package com.hotel.patterns;

public interface PaymentStrategy {
    boolean pay(double amount);
}