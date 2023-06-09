package com.example.tpairviafx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/** Class to model blank as an object
 * @author  Bendeguz Kudor*/
public class Blank {


    public Blank() {
        getCommissionRates();

    }

    public long getBlankID() {
        return blankID;
    }

    private long blankID;

    private String sql;
    public int noOfFlights;
    private int staffID;
    private int localCurrency;
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

    public double getCommissionAmount() {
        return commissionAmount;
    }

    private double commissionAmount;

    public int getPriceGBP() {
        return priceGBP;
    }

    public void setPriceGBP(int priceGBP) {
        this.priceGBP = priceGBP;
    }

    private int priceGBP;

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

    public double getInterlineCommissionRate() {
        return interlineCommissionRate;
    }

    public double getDomesticCommissionRate() {
        return domesticCommissionRate;
    }

    public double interlineCommissionRate;
    public double domesticCommissionRate;
    private String flightType;

    public double getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(double commissionRate) {
        this.commissionRate = commissionRate;
    }

    private double commissionRate;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;


    /**

     Represents a Blank object used for ticket bookings.
     @param blankType the type of blank domestic, mco or interline
     @param flights an ArrayList of FlightModel objects representing the flights  on this blank
     @throws SQLException if there is an error accessing the database
     */
    public Blank(int staffID, String blankType, ArrayList<FlightModel> flights) throws SQLException {
        this.description = "";
        this.flights = flights;
        this.staffID = staffID;
        this.blankType = blankType;
//        this.localCurrency = localCurrency;// for each loop in the other class to summarize dollar
        noOfFlights = flights.size();
//        retrieveBlankID(blankType);
        calcSum(flights);
        getCommissionRates();
        calculateCommission(this.blankType);


    }
    /** Constructor for blank model to set in tableview*/
    public Blank(Long blankID,int staffID,  String dateAssigned,int sold, String dateAdded ){
        this.blankID = blankID;
        this.staffID = staffID;
        this.dateAssigned = dateAssigned;
        this.sold = sold;
        this.dateAdded = dateAdded;
        this.description = "";

    }
    /** Constructor for MCO Blanks*/
    public Blank(Long blankID, int staffID, String type,String flightType, String description){
        this.priceGBP = 30;
        this.blankID = blankID;
        this.staffID = staffID;
        this.blankType = type;
        this.flightType = flightType;
        this.description = description;
        getCommissionRates();
        calculateCommission(flightType);

    }
    public void setBlankID(String flightType) throws SQLException {
        retrieveBlankID(flightType);


    }

    /**

     Retrieves the interline and domestic commission rates from the database and sets them as instance variables.
     */
    public void getCommissionRates(){
        String sql = "Select * from commission_rates";
        DBConnect dbConnect = new DBConnect();
        int maxValue = 0;
        try {
            dbConnect.connect();

            ResultSet rs = dbConnect.executeQuery(sql);
            while(rs.next()) {
                interlineCommissionRate = rs.getDouble(1);
                domesticCommissionRate = rs.getDouble(2);

            }
            dbConnect.closeConnection();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }


    }
//    public double findCommissionRate(String type){
//        String blankID =Long.toString(this.blankID);
//        double commissionrate;
//        if(type == "Interline"){
//            commissionrate =  interlineCommissionRate;
//        }else if (type == "Domestic"){
//            commissionrate =  domesticCommissionRate;
//        }
//    }
    /** This is a method to set each blanks commission rate */
    public void calculateCommission(String type){
        if (type.equals("Interline")){
//            this.commissionAmount = (priceUSD*0.8) * interlineCommissionRate;
            setCommissionRate(interlineCommissionRate);
        }else if(type.equals("Domestic")){
//            this.commissionAmount = (priceUSD*0.8) * domesticCommissionRate;
            setCommissionRate(domesticCommissionRate);
        }

    }
    /** This method that gets the next 444 (manual ticketing) blank for the travel advisor that is operating currently*/
    public static Long getManualTicketingBlank(int staffID) throws SQLException {
        long blankID = 0L;
        String sql = "SELECT MIN(blankID) FROM blanks WHERE SUBSTR(blankID, 1, 3) = '440' AND staffID = "+staffID+" AND sold != 1;";
        DBConnect db = new DBConnect();
        ResultSet rs = null;
        try {
            db.connect();
            rs = db.executeQuery(sql);
            System.out.println(sql);
            if (rs.next()) {
                blankID = rs.getLong(1);
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
        return blankID;

    }
    /** This method calculates the total price of each blank by iterating through the arraylist of flights and getting the prices*/
    public void calcSum(ArrayList<FlightModel> flights){
        for(FlightModel x : flights){
            this.priceGBP += x.getPrice();
        }


        /** This method prints out the details held by each blank object, used for testing when implementing features*/
    }    public void printBlankDetails(){
        for(FlightModel x : flights){
            x.printFlightDetails();
        }
        System.out.println("StaffID:  "+this.staffID+
                    "  blankType:  "+this.blankType+
                    "  blankID:  "+this.blankID+
                    "  Local Currency:  "+this.localCurrency+
//                    "  paymentType:  "+paymentTypeT(this.paymentType)+
                    "  CommissionRate : "+ commisionRate+
                    "  Customer:  "+this.customer);
    }
//    public void finalizeBlank(int userID, String flightType, long blankID, ){


//    }
//    */
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


    /** This method retrieves the next available blank for the current selection of tickets
     * takes flight as an argument which is to determine the appropriate blank, for the type and amount of flighst
     * returns a blankID long*/
    public  Long retrieveBlankID(String flightType) throws SQLException {
        Long blankID = 0L;
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
                }else if(noOfFlights == 1){
                    sql = "SELECT blankID FROM blanks WHERE SUBSTR(blankID, 1, 3) = '101' AND staffID ='"+ staffID +"'AND sold != 1";
                }//Added break after demo, this is why it was not working for domestic flights in the demo. fixed when i got home
//                break;
            case "MCO":
                sql = "SELECT blankID FROM blanks WHERE (SUBSTR(blankID, 1, 3) = '451' or SUBSTR(blankID, 1, 3) = '452') AND staffID ='"+ staffID +"'AND sold != 1";
                break;
        }
        DBConnect db = new DBConnect();
        try {
            db.connect();
            rs = db.executeQuery(sql);
            System.out.println(sql);
            if (rs.next()) {
                blankID = rs.getLong("blankID");
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
        return blankID;
    }


    public static void main(String[] args) throws SQLException {
        Blank blank = new Blank();
//        blank.getCommissionRates();
//        System.out.println(blank.findCommissionRate("Interline"));
    }

    /** Marks the blank used sets the date used to current date */
    public void markBlankAsUsed(Blank blank) throws SQLException {

        blankID = blank.getBlankID();
        String sql = "UPDATE blanks SET sold = 1, dateUsed = '"+ Application.getDate() +"' WHERE blankID = '"+blankID+"'";
        DBConnect db = new DBConnect();
        db.connect();
        int effected = db.executeUpdate(sql);
        System.out.println(sql);
        if (effected !=0) {
            System.out.println("Blank Status Updated, marked blank '"+blankID+"' as sold ");
            System.out.println(effected);

            }







}
}



