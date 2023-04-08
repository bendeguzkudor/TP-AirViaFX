package com.example.tpairviafx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.example.tpairviafx.Application.date;

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
    private int paymentType; // 0 for card, 1 for cash
    private static double commisionRate;
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

    public double getCommissionSum() {
        return commissionSum;
    }

    private double commissionSum;

    public int getPriceUSD() {
        return priceUSD;
    }

    private int priceUSD;

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    private int sold;
    public void setBlankID(long blankID) {
        this.blankID = blankID;
    }

    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    public String getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(String dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    private String dateAssigned;
    private String dateAdded;


    public Blank(int staffID, String blankType, ArrayList<FlightModel> flights) throws SQLException {

        this.flights = flights;
        this.staffID = staffID;
        this.blankType = blankType;
//        this.localCurrency = localCurrency;// for each loop in the other class to summarize dollar
        noOfFlights = flights.size();
        retrieveBlankID(blankType);
        System.out.println(noOfFlights);
        calcSum(flights);
        System.out.println(priceUSD);
        commisionRate = findCommissionRate(this.blankType);
        calculateCommission();





//        this.taxLocal = taxLocal;
//        this.taxOther = taxOther;
//        this.paymentType = paymentType;
//        this.commisionRate = commisionRate;
//        this.customer = customer;

//        flights.get(0).getDeparture();

    }
    public Blank(Long blankID,int staffID,  String dateAssigned,int sold, String dateAdded ){
        this.blankID = blankID;
        this.staffID = staffID;
        this.dateAssigned = dateAssigned;
        this.sold = sold;
        this.dateAdded = dateAdded;



    }    public void setBlankID(String flightType) throws SQLException {
        retrieveBlankID(flightType);


    }
    public double findCommissionRate(String type){
        String blankid =Long.toString(blankID);
        if(blankid.charAt(0)== 4){
            return Application.interlineCommissionRate;
        }else{
            return Application.domesticCommisionRate;

        }
    }
    public void calculateCommission(){
        this.commissionSum = priceUSD * commisionRate;

    }
    public void calcSum(ArrayList<FlightModel> flights){
        for(FlightModel x : flights){
            this.priceUSD += x.getPrice();
        }


    }    public void printBlankDetails(){
        for(FlightModel x : flights){
            x.printFlightDetails();
        }
        System.out.println("StaffID:  "+this.staffID+
                    "  blankType:  "+this.blankType+
                    "  blankID:  "+this.blankID+
                    "  Local Currency:  "+this.localCurrency+
                    "  paymentType:  "+paymentTypeT(this.paymentType)+
                    "  CommissionRate : "+ commisionRate+
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
                    sql = "SELECT blankID FROM blanks WHERE SUBSTR(blankID, 1, 3) = '444' AND staffID ='"+ staffID +"' AND sold != 1";
                }else if (noOfFlights <= 2){
                    sql = "SELECT blankID FROM blanks WHERE SUBSTR(blankID, 1, 3) = '420' AND staffID ='"+ staffID +"'AND sold != 1";
                }
                break;
            case "Domestic":
                if(noOfFlights == 2){
                    sql = "SELECT blankID FROM blanks WHERE SUBSTR(blankID, 1, 3) = '201' AND staffID ='"+ staffID +"'AND sold != 1";
                }else{
                    sql = "SELECT blankID FROM blanks WHERE SUBSTR(blankID, 1, 3) = '101' AND staffID ='"+ staffID +"'AND sold != 1";
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

            }else{
                System.out.println("No more available blanks that would suit this ticketing");
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
        long blankID = 44400000000L;
        int amount = 10;
        for (int i = 1; i <= amount; i++){
            System.out.println(blankID+i);
        }
//        Blank blank = new Blank(888, 1, "Interline");
//        System.out.println(blank.blankID);
    }
    public void markBlankAsUsed(Blank blank) throws SQLException {

        blankID = blank.getBlankID();
        String sql = "UPDATE blanks SET sold = 1, dateUsed = '"+ Application.getDate() +"' WHERE blankID = '"+blankID+"'";
        DBConnect db = new DBConnect();
        try {
            db.connect();
            int effected = db.statement.executeUpdate(sql);
            System.out.println(sql);
            if (effected !=0) {
                System.out.println("Blank Status Updated, marked blank '"+blankID+"' as sold ");
                System.out.println(effected);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            db.closeConnection();
        }
    }




}



