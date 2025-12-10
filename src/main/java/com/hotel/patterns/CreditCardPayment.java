package com.hotel.patterns;

public class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    private String cvv;

    public CreditCardPayment(String cardNumber, String cvv) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("Kredi Kartı (" + cardNumber + ") ile " + amount + " TL ödeme alındı.");
        return true; // Ödeme başarılı simülasyonu
    }
}