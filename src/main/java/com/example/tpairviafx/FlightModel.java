package com.example.tpairviafx;

import java.sql.ResultSet;
import java.sql.SQLException;


/** Class to model flights
 * @author  Bendeguz Kudor*/
public class FlightModel {

    private Integer flightNumber;
    private ResultSet rs;
    private String arrival;
    private String departure;
    private String date;
    private String time;
    private Integer price;

    public String getFlightType() {
        return flightType;
    }

    private String flightType;


    /**

     Constructs a FlightModel object with the given parameters.
     @param flightNumber the flight number of the flight
     @param departure the departure location of the flight
     @param arrival the arrival location of the flight
     @param date the date of the flight
     @param time the time of the flight
     @param price the price of the flight
     @throws SQLException if there is an error accessing the database
     */
    public FlightModel(Integer flightNumber, String departure, String arrival, String date, String time, Integer price) throws SQLException {
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.arrival = arrival;
        this.date = date;
        this.time = time;
        this.price = price;

    }
    /**
     Retrieves the flight type of this flight from the database and sets it to the flightType attribute.
     @throws SQLException if a database access error occurs
     */
    public void retrieveFlightType() throws SQLException {
        String ft ;
        DBConnect db = new DBConnect();
        try {
            db.connect();
            String sql = "SELECT type FROM flights WHERE flighNumber = '" + this.flightNumber + "'";
            rs = db.executeQuery(sql);
            if (rs.next()) {
                this.flightType=rs.getString("type");
                ft = rs.getString("type");
                System.out.println(rs.getString("type"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if(rs!= null){
            rs.close();
            }
            db.closeConnection();
        }
    }

    public int getFlightNumber() {
        return flightNumber;
    }

    /**Prints a flight objects details, used when testing */
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
