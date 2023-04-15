package com.example.tpairviafx;

/**This class is not used
 * @author  Bendeguz Kudor*/
public class Flight {

    private Integer flightNumber;
    private String arrival;
    private String departure;
    private String date;
    private String time;

    public Integer getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(Integer flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    private String price;

    public Flight(Integer flightNumber,String arrival,String departure,String date,String time,String price){
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.arrival = arrival;
        this.date = date;
        this.time = time;
        this.price = price;
    }
    public void printFlightDetails(){
        System.out.println("Departure"+ departure+ "Arrival" + arrival);
    }
}
