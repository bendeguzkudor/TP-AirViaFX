package com.example.tpairviafx;


import java.util.HashMap;
import java.util.Map;

import java.util.HashMap;
import java.util.Map;
    public class Currency {

        private Map<String, Double> conversionRates;

        public String[] getCurrencies() {
            return currencies;
        }

        public void setCurrencies(String[] currencies) {
            this.currencies = currencies;
        }

        private String [] currencies;

        public Currency() {
            conversionRates = new HashMap<>();
            conversionRates.put("USD", 1.0);
            conversionRates.put("EUR", 0.84);
            conversionRates.put("GBP", 0.72);
            conversionRates.put("JPY", 109.68);
            conversionRates.put("AUD", 1.31);
            conversionRates.put("CAD", 1.25);
            conversionRates.put("CHF", 0.92);
            conversionRates.put("CNY", 6.51);
            conversionRates.put("HKD", 7.76);
            conversionRates.put("INR", 73.58);
            conversionRates.put("KRW", 1130.78);
            conversionRates.put("MXN", 20.09);
            conversionRates.put("NZD", 1.39);
            conversionRates.put("SGD", 1.33);
            currencies = conversionRates.keySet().toArray(new String[0]);
        }

        public double convert(double amount, String fromCurrency, String toCurrency) {
            double fromRate = conversionRates.get(fromCurrency);
            double toRate = conversionRates.get(toCurrency);
            return amount * toRate / fromRate;
        }

        public static void main(String[] args) {
            Currency converter = new Currency();
            double amount = 100.0;
            String fromCurrency = "USD";
            String toCurrency = "EUR";
            double convertedAmount = converter.convert(amount, fromCurrency, toCurrency);
            System.out.printf("%.2f %s = %.2f %s", amount, fromCurrency, convertedAmount, toCurrency);
        }

    }



