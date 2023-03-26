package com.example.tpairviafx;

public class FlightModel {

    private Integer flightNumber;
    private String arrival;
    private String departure;
    private String date;
    private String time;
    private Integer price;

    public FlightModel(Integer flightNumber, String departure, String arrival, String date, String time, Integer price){
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.arrival = arrival;
        this.date = date;
        this.time = time;
        this.price = price;
    }

    public int getFlightNumber() {
        return flightNumber;
    }
    public void printFlightDetails(){

        System.out.println("Departure: "+ departure+ " Arrival: " + arrival + " Price: "+ price);
    }

    public void setFlightNumber(Integer flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String arrival) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
