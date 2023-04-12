package com.example.tpairviafx;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Sale {
    private final int staffID;

    private static final int  counter = 0;

    private int price; // set in dollars
    private String date;

    public void setSaleID(int saleID) {
        this.saleID = saleID;
    }

    public int getSaleID() {
        return saleID;
    }

    private int saleID;
    // need array for blankIDs
    //need array for flights.
    private final String localCurrency;
    private Blank blank;
    private int exchangeRate;

    private final Customer customer; // customer object  // need a second constructor for non-member sales

    private final double commisionSum; //calculated in the constructor
    private int type; //interline or domestic 0 or 1.
    private final double airportTax;
    private boolean latePayment; // date string if allowed and date represents the deadline, null if no late payment is issued
    private double discount; // percentage dependant on the customer
    private final ArrayList<Blank> blanks;
    private int priceUSD;
    private final String paymentType;
    private double initialPriceSum;

    public Sale(int staffID, String date, Customer customer,String localCurrency, ArrayList<Blank> blanks, String paymentType){
        this.saleID = 1;
        this.blanks = blanks;
        this.localCurrency = localCurrency;
        this.staffID = staffID;
        this.date = date;
        this.customer = customer;
//        this.latePayment = latePayment; // In customer object, setlatepayment options. customer.getLatePayment
//        this.discount = discount; // customer.getDiscount()
        customer.getQueryDiscount();
        takeDiscountOff(blanks, customer);
        initialPriceSum = calcSum(blanks);
        commisionSum = calcCommissionSum(blanks);
        airportTax = Application.taxRate * initialPriceSum;
        this.paymentType = paymentType;
//        getSaleID(this);

    }

    public void uploadToDatabase(){

    }


    public double calcSum(ArrayList<Blank> blanks){
        double priceSum =0;
        for(Blank x : blanks){
            priceSum += x.getPriceGBP();
        }
        return priceSum;
    }
    public void takeDiscountOff(ArrayList<Blank> blanks, Customer customer){
        for(Blank x : blanks){
            x.setPriceGBP((int) (x.getPriceGBP()*(1- customer.getQueryDiscount())));
            System.out.println(x.getPriceGBP());
            System.out.println(customer.getQueryDiscount());
        }
    }
    public double  calcCommissionSum(ArrayList<Blank> blanks){
        double sum = 0;
        for(Blank x : blanks){
            sum += (x.getPriceGBP()*(1-Application.taxRate))*x.getCommissionRate();
        }
        System.out.println(sum);
        return sum;
    }

    public void printSale(){
        System.out.println("Staff ID: "+ staffID + " Total Price in USD: "+priceUSD+" Date: "+date+" saleID: "+saleID+
                " Customer name : "+customer.getName()+" CustomerID: "+customer.getCustomerID()+
                " Commission sum: : "+commisionSum+"BlankID: " +printBlanks());
    }
    public void pushToDatabase() throws SQLException {
        String sql = "INSERT INTO sale (saleID, staffID, price,currency, date, customerID, paymentType,cardNumber," +
        "latePayment, commissionSum, taxSum) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        try {
            DBConnect db = new DBConnect();
            db.connect();
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            // Set the values of the parameters in the prepared statement
            stmt.setInt(1, saleID);
            stmt.setInt(2, staffID);
            stmt.setDouble(3, initialPriceSum);
            stmt.setString(4, localCurrency);
            stmt.setString(5, date);
            stmt.setInt(6, customer.getCustomerID());
            stmt.setString(7, paymentType);
            stmt.setString(8, customer.getCardNumber());
            stmt.setString(9, "");
            stmt.setDouble(10, commisionSum);
            stmt.setDouble(11, (initialPriceSum * 0.2));
//            System.out.println(stmt.executeUpdate());

            int rowsInserted = stmt.executeUpdate();
            System.out.println(rowsInserted + " rows inserted.");
            System.out.println(sql);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    /** */
    public void pushSaleToSoldBlanks(String description){
        System.out.println("Pishintdasletoolsoldblanks");
        String sql = "INSERT INTO soldBlanks (saleID, blankID) VALUES (?,?)";
        String sqlformco = "INSERT INTO soldBlanks (saleID, blankID,description) VALUES (?,?,?)";
        try {
            DBConnect db = new DBConnect();
            db.connect();
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);
            PreparedStatement pstmt = db.getConnection().prepareStatement(sqlformco);

            System.out.println(this.blanks.size());
            for (Blank x : this.blanks) {
                if(!(x.getBlankID()>45000000000L)){
                // Set the values of the parameters in the prepared statement
                stmt.setInt(1, saleID);
                stmt.setLong(2, x.getBlankID());
                int rowsInserted = stmt.executeUpdate();
                System.out.println(rowsInserted + " rows inserted.");
                System.out.println(sql);
            }else{
                    pstmt.setInt(1, saleID);
                    pstmt.setLong(2, x.getBlankID());
                    pstmt.setString(3,description);
                    int rowsInserted = pstmt.executeUpdate();
                    System.out.println(rowsInserted + " rows inserted.");
                    System.out.println(sql);

                }
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }


        public int selectMaxSaleID() {
        String sql = "select max(saleID) from sale"; // "select max(saleID) as max_id from sale"
        DBConnect dbConnect = new DBConnect();
        int maxValue = 0;
        try {
            dbConnect.connect();

            ResultSet rs = dbConnect.executeQuery("SELECT MAX(saleID) FROM sale");
            rs.next();
            maxValue = rs.getInt(1);
            System.out.println("Max value is: " + maxValue);
            dbConnect.closeConnection();


        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return maxValue;

    }



    public String printBlanks(){
        String blanksS ="";
        for(Blank x : this.blanks){
            blanksS += x.getBlankID() + ",";
        }
        return blanksS;
    }


}
