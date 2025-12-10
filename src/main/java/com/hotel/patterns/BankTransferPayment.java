package com.hotel.patterns;

public class BankTransferPayment implements PaymentStrategy {
    private String iban;

    public BankTransferPayment(String iban) {
        this.iban = iban;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("Banka Havalesi (" + iban + ") ile " + amount + " TL ödeme bekleniyor.");
        return true;
    }
}