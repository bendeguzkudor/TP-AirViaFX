package com.example.tpairviafx;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/** Class to handle sales
 * @author  Bendeguz Kudor*/

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

    /**
     Initializes a new Sale object with the provided staffID, date, customer, local currency, list of blanks, and payment type.
     @param staffID the ID of the staff member who made the sale
     @param date the date the sale was made
     @param customer the customer who made the purchase
     @param localCurrency the currency used in the sale
     @param blanks an ArrayList of Blank objects representing the items sold in the sale
     @param paymentType the type of payment used in the sale
     */
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


    /**
     Calculates the total sum of prices in GBP for a given ArrayList of Blank objects.
     @param blanks An ArrayList of Blank objects for which the total price in GBP is to be calculated.
     @return The sum of prices in GBP for the given ArrayList of Blank objects.
     */
    public double calcSum(ArrayList<Blank> blanks){
        double priceSum =0;
        for(Blank x : blanks){
            priceSum += x.getPriceGBP();
        }
        return priceSum;
    }
    /**

     Applies the discount obtained from the Customer object to the price of the given list of Blanks.
     @param blanks the list of Blanks to apply the discount to
     @param customer the Customer object containing the discount information
     */
    public void takeDiscountOff(ArrayList<Blank> blanks, Customer customer){
        for(Blank x : blanks){
            x.setPriceGBP((int) (x.getPriceGBP()*(1- customer.getQueryDiscount())));
            System.out.println(x.getPriceGBP());
            System.out.println(customer.getQueryDiscount());
        }
    }
    /**

     Calculates the total commission sum for a list of blanks.
     @param blanks the list of blanks to calculate commission for
     @return the total commission sum for the given list of blanks
     */
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

    /**
     This method pushes the current sale object to the database by inserting its values into the "sale" table in the database.
     It uses a prepared statement to  and sets the values of the parameters in the statement based on the
     attributes of the current sale object.
     @throws SQLException if a database access error occurs or this method is called on a closed connection.
     */

    public void pushToDatabase() throws SQLException {
        String sql = "INSERT INTO sale (saleID, staffID, price,currency, date, customerID, paymentType,cardNumber," +
        "latePayment, commissionSum, taxSum, conversionRate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        Currency currency = new Currency();

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
            stmt.setDouble(12, currency.getConversion(1));
//            System.out.println(stmt.executeUpdate());

            int rowsInserted = stmt.executeUpdate();
            System.out.println(rowsInserted + " rows inserted.");
            System.out.println(sql);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**

     Inserts sale information into the soldBlanks table in the database, including the sale ID, blank ID, and date used.
     If the blank ID is greater than 450,which means it is an MCO blank it inserts the description of the blank as well.
     @param description a String for the MCO blank description, otherwise null
     */
    public void pushSaleToSoldBlanks(String description){
        date = Application.getDate();
        System.out.println("Pishintdasletoolsoldblanks");
        String sql = "INSERT INTO soldBlanks (saleID, blankID,dateUsed) VALUES (?,?,?)";
        String sqlformco = "INSERT INTO soldBlanks (saleID, blankID,dateUsed,description) VALUES (?,?,?,?)";
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
                stmt.setString(3,date);
                int rowsInserted = stmt.executeUpdate();
                System.out.println(rowsInserted + " rows inserted.");
                System.out.println(sql);
            }else{
                    pstmt.setInt(1, saleID);
                    pstmt.setLong(2, x.getBlankID());
                    stmt.setString(3,date);
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


    /**
     Selects the maximum sale ID from the sale table in the database.
     @return the maximum sale ID as an integer.
     @throws SQLException if there is an error in the SQL query.
     */
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
