package com.example.tpairviafx;

public class Payment {
    private final String paymentType;
    private final String cardNumber;
    private final double exchangeRate;
    private final int localCurrencyAmount;

    public Payment(String paymentType, String cardNumber, double exchangeRate, int localCurrencyAmount) {
        this.paymentType = paymentType;
        this.cardNumber = cardNumber;
        this.exchangeRate = exchangeRate;
        this.localCurrencyAmount = localCurrencyAmount;

    }
}
