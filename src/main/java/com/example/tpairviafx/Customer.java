package com.example.tpairviafx;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/** Class to model customers
 * @author  Bendeguz Kudor*/
public class Customer {
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    private String firstName;

    public int getLatePaymentLimit() {
        return latePaymentLimit;
    }

    public void setLatePaymentLimit(int latePaymentLimit) {
        this.latePaymentLimit = latePaymentLimit;
    }

    private int latePaymentLimit ;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    private String lastName;

    public void setName(String name) {
        this.firstName = name;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    private int customerID;


    public double getfLexibleDiscount() {
        return fLexibleDiscount;
    }

    public void setfLexibleDiscount(double fLexibleDiscount) {
        this.fLexibleDiscount = fLexibleDiscount;
    }

    private double fLexibleDiscount;

    public double getQueryDiscount() {
        return queryDiscount;
    }

    public void setQueryDiscount(int discount) {
        this.queryDiscount = discount;
    }

    private double queryDiscount;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    private String cardNumber;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public double getFixeddiscount() {
        return fixeddiscount;
    }

    public void setFixeddiscount(double fixeddiscount) {
        this.fixeddiscount = fixeddiscount;
    }

    public double getBelow1000() {
        return below1000;
    }

    public void setBelow1000(double below1000) {
        this.below1000 = below1000;
    }

    public double getBetween1and2000() {
        return between1and2000;
    }

    public void setBetween1and2000(double between1and2000) {
        this.between1and2000 = between1and2000;
    }

    public double getMorethan2000() {
        return morethan2000;
    }

    public void setMorethan2000(double morethan2000) {
        this.morethan2000 = morethan2000;
    }

    private double fixeddiscount, below1000, between1and2000, morethan2000;

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String phone;


    /**

     Creates a new Customer object with the given first name, last name, email, phone, fixed discount, discount for purchases below $1000,
     discount for purchases between $1000 and $2000, and discount for purchases above $2000.
     @param firstName the first name of the customer
     @param lastName the last name of the customer
     @param email the email address of the customer
     @param phone the phone number of the customer
     @param fixeddiscount the fixed discount percentage for the customer
     @param below1000 the discount percentage for purchases below $1000 for the customer
     @param between1and2000 the discount percentage for purchases between $1000 and $2000 for the customer
     @param morethan2000 the discount percentage for purchases above $2000 for the customer
     */
    public Customer(String firstName,String lastName,String email,String phone,double fixeddiscount, double below1000, double between1and2000, double morethan2000){
        latePaymentLimit = 30;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.fixeddiscount = fixeddiscount;
        this.below1000 = below1000;
        this.between1and2000 = between1and2000;
        this.morethan2000 = morethan2000;
        this.queryDiscount = queryDiscount();
        System.out.println(queryDiscount());


    }
    /** Constructor used for modeling the customer in a TableView
     * @param firstName the first name of the customer
     * @param lastName the last name of the customer
     * @param customerID id of the customer*/
    public Customer(String firstName,String lastName, int customerID ){
        latePaymentLimit = 30;
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerID = customerID;
        this.queryDiscount = queryDiscount();
    }

    /** Constructor used when modelling the customer discount editing interface
     * @param firstName the first name of the customer
     * @param lastName the last name of the customer
     * @param customerID the unique identifier for the customer
     * @param fixeddiscount the fixed discount amount for the customer
     * @param below1000 the discount percentage for purchases below $1000 for the customer
     * @param between1and2000 the discount percentage for purchases between $1000 and $2000 for the customer
     * @param morethan2000 the discount percentage for purchases over $2000 for the customer */
    public Customer(String firstName,String lastName, int customerID, double fixeddiscount, double below1000, double between1and2000, double morethan2000 ){
        latePaymentLimit = 30;
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerID = customerID;
        this.fixeddiscount = fixeddiscount;
        this.below1000 = below1000;
        this.between1and2000 = between1and2000;
        this.morethan2000 = morethan2000;
    }

    /**

     This method is used to record a new customer by inserting their information into the customer table in the database.
     @param customer a Customer object containing the customer's information to be recorded
     */
    public static void recordCustomer(Customer customer){

        DBConnect db = new DBConnect();
        try{
        String sql = "INSERT INTO customer (firstName, lastName, email, phone, fixeddiscount, below1000, between1and2000, morethan2000) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
        db.connect();
        PreparedStatement pstmt = db.getConnection().prepareStatement(sql);
        System.out.println(pstmt.toString());
        pstmt.setString(1, customer.getFirstName());
        pstmt.setString(2, customer.getLastName());
        pstmt.setString(3, customer.getEmail());
        pstmt.setString(4, customer.getPhone());
        pstmt.setDouble(5, customer.getFixeddiscount());
        pstmt.setDouble(6, customer.getBelow1000());
        pstmt.setDouble(7, customer.getBetween1and2000());
        pstmt.setDouble(8, customer.getMorethan2000());
        pstmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public String getName(){
        return this.firstName;
    }

    public static void main(String[] args) {
        long start = 10100000001L;
        long end = 10100000030L;

        for (long i = start; i <= end; i++) {
            System.out.println(i);
        }
    }

    /**
     Queries the discount for the customer from the database based on their customer ID.
     @return the discount value retrieved from the database
     */
    public double queryDiscount(){
        String sql = "select fixeddiscount, below1000, between1and2000,morethan2000\n" +
                "from customer where  id = "+ customerID +"; ";
        double discount = 0;
        DBConnect db = new DBConnect();

        try{
            db.connect();
            ResultSet rs = db.executeQuery(sql);
            if (rs.next()){
                if (rs.getDouble(1) != 0){
                    discount = rs.getDouble(1);
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }


        return discount;
    }

}
