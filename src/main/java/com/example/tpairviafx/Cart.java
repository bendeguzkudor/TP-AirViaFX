package com.example.tpairviafx;

import java.util.ArrayList;

public class Cart {
    private int sum;
    public ArrayList<FlightModel> getFlights() {
        return flights;
    }

    private ArrayList<FlightModel> flights;

    public Cart(){
        flights = new ArrayList<>();

    }
    public void addFlightToCart(FlightModel flight){
        flights.add(flight);
    }
    public void printCart(){
        for(FlightModel x : flights){
            x.printFlightDetails();
        }

    }
    public int sumCart(){
        int sum = 0;
        for(FlightModel x: flights){
            int price = x.getPrice();
            sum += price;
        }
        return sum;
    }
}
