package com.example.tpairviafx;

public class Payment {
    private String paymentType;
    private String cardNumber;
    private double exchangeRate;
    private int localCurrencyAmount;

    public Payment(String paymentType, String cardNumber, double exchangeRate, int localCurrencyAmount) {
        this.paymentType = paymentType;
        this.cardNumber = cardNumber;
        this.exchangeRate = exchangeRate;
        this.localCurrencyAmount = localCurrencyAmount;

    }
}
