package com.example.tpairviafx;


import java.util.HashMap;
import java.util.Map;

public class Currency {

    private static final Map<String, Integer> exchangeRates = null;



    private static final Map<String, Integer> MY_MAP;

    static {
        MY_MAP = new HashMap<>();
        MY_MAP.put("one", 1);
        MY_MAP.put("two", 2);
        MY_MAP.put("three", 3);
        MY_MAP.put("four", 4);
    }

    public static void main(String[] args) {
        double amount = 100.0;
        int fromCurrency = 0; // USD
        int toCurrency = 1; // EUR
//        double convertedAmount = convert(amount, fromCurrency, toCurrency);
//        System.out.println(amount + " USD = " + convertedAmount + " EUR");
    }
}



