package com.example.tpairviafx;

import java.sql.SQLException;
import java.util.ArrayList;

public class Cart {
    public int count = 0;
    private ArrayList blanks;
    private int cartType; //Interline
                                    // 444 – used for international destinations automatic ticketing with up to 4 flight coupons; each coupon is used for a particular leg of the journey,
                                    //440 – the same as 444 but for manual processing (the flying details are filled in manually by a travel advisor),
                                    //420 – as 444 but with only 2 flight coupons.
                          //Domestic:
                                    //201 – a blank with two coupons, and
                                    //101 – a blank with only 1 coupon.
    private int sum;
    public ArrayList<FlightModel> getFlights() {
        return flights;
    }

    private final ArrayList<FlightModel> flights;

    public Cart(){
//        blanks = new ArrayList();
        flights = new ArrayList<>();

    }

    public void addFlightToCart(FlightModel flight) throws SQLException {
        if(flights.size() < 4){
        flights.add(flight);
        count++;
        flight.retrieveFlightType();
        }else{
            //tooManyLegsError(); // throw an error when trying to add more flight legs than 4;
        }
    }
    public void printCart(){
        for(FlightModel x : flights){
            x.printFlightDetails();
        }

    }
    public void addBlanks(Blank _blank){
        this.blanks.add(_blank);
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
