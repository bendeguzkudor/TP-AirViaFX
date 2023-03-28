package com.example.tpairviafx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Blank {
    public long getBlankID() {
        return blankID;
    }

    private long blankID;

    private String sql;
    public int noOfFlights;
    private int staffID;
    private int localCurrency;
    private int price;
    private int taxLocal;
    private int taxOther;
    private int paymentType; // 0 for card, 1 for cash
    private double commisionRate;
    private String customer;

    private ResultSet rs;

    public ArrayList<FlightModel> getFlights() {
        return flights;
    }

    public void setFlights(ArrayList<FlightModel> flights) {
        this.flights = flights;
    }

    private ArrayList<FlightModel> flights;

    public String getBlankType() {
        return blankType;
    }

    public void setBlankType(String blankType) {
        this.blankType = blankType;
    }

    private String blankType;

    public Blank(int staffID, String blankType, int localCurrency, int taxLocal,
                 int taxOther, int paymentType, double commisionRate, String customer) throws SQLException {

        flights = new ArrayList();
        this.staffID = staffID;
        this.blankType = blankType;
        this.localCurrency = localCurrency;// for each loop in the other class to summarize dollar
                                            // price then convert it into local currency then feed it to the constructor

        this.taxLocal = taxLocal;
        this.taxOther = taxOther;
        this.paymentType = paymentType;
        this.commisionRate = commisionRate;
        this.customer = customer;


//        flights.get(0).getDeparture();
        retrieveBlankID(blankType);

    }
    public void setBlankID(String flightType) throws SQLException {
        retrieveBlankID(flightType);

    }
    public void printBlankDetails(){
        for(FlightModel x : flights){
            x.printFlightDetails();
        }
        System.out.println("StaffID:  "+this.staffID+
                    "  blankType:  "+this.blankType+
                    "  blankID:  "+this.blankID+
                    "  Local Currency:  "+this.localCurrency+
                    "  Tax Local:  "+this.taxLocal+
                    "  Tax Other:  "+this.taxOther+
                    "  paymentType:  "+paymentTypeT(this.paymentType)+
                    "  CommissionRate : "+this.commisionRate+
                    "  Customer:  "+this.customer);
    }
//    public void finalizeBlank(int userID, String flightType, long blankID, ){


//    }
    public void addFlightToBlank(FlightModel flight) {
        if(flights.size() < 4){

            flights.add(flight);
            noOfFlights++;
        }else{
            //tooManyLegsError(); // throw an error when trying to add more flight legs than 4;
        }
    }
    public String paymentTypeT(int i){
        String s = "";
        switch (i){
            case 1:
                s= "Cash";
                break;
            case 0:
                s= "Card";
                break;
        }
        return s;
    }



    public void retrieveBlankID(String flightType) throws SQLException {
        switch (flightType){
            case "Interline":
                if(noOfFlights > 2){
                    sql = "SELECT blankID FROM blanks WHERE SUBSTR(blankID, 1, 3) = '440' AND staffID ='"+ staffID +"'";
                }else if (noOfFlights <= 2){
                    sql = "SELECT blankID FROM blanks WHERE SUBSTR(blankID, 1, 3) = '420' AND staffID ='"+ staffID +"'";
                }
                break;
            case "Domestic":
                if(noOfFlights == 2){
                    sql = "SELECT blankID FROM blanks WHERE SUBSTR(blankID, 1, 3) = '201' AND staffID ='"+ staffID +"'";
                }else{
                    sql = "SELECT blankID FROM blanks WHERE SUBSTR(blankID, 1, 3) = '101' AND staffID ='"+ staffID +"'";
                }
        }
        DBConnect db = new DBConnect();
        try {
            db.connect();
            rs = db.statement.executeQuery(sql);
            System.out.println(sql);
            if (rs.next()) {
                this.blankID = rs.getLong("blankID");
//                System.out.println(rs.getInt("blankID"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            if(rs!= null){
                rs.close();
            }
            db.closeConnection();
        }
    }

    public static void main(String[] args) throws SQLException {
//        Blank blank = new Blank(888, 1, "Interline");
//        System.out.println(blank.blankID);


    }

}

