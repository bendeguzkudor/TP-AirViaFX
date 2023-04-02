package com.example.tpairviafx;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    public double getFixedDiscount() {
        return fixedDiscount;
    }

    public void setFixedDiscount(double fixedDiscount) {
        this.fixedDiscount = fixedDiscount;
    }

    public double getfLexibleDiscount() {
        return fLexibleDiscount;
    }

    public void setfLexibleDiscount(double fLexibleDiscount) {
        this.fLexibleDiscount = fLexibleDiscount;
    }

    private double fixedDiscount;
    private double fLexibleDiscount;

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    private int discount;

    public Customer(String firstName,String lastName, int customerID, double fixedDiscount, double fLexibleDiscount, int discount ){
        latePaymentLimit = 30;
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerID = customerID+1;
        this.fixedDiscount = fixedDiscount;
        this.fLexibleDiscount = fLexibleDiscount;

    }
    public static void recordCustomer(Customer customer){
            String sql = "INSERT INTO customer (customerID, firstName, lastName, flexibleDiscount, fixedDiscount, discount)\n" +
                    "VALUES (?,?,?,?,?,?)";
            try {
                DBConnect db = new DBConnect();
                db.connect();
                PreparedStatement stmt = db.connection.prepareStatement(sql);
                // Set the values of the parameters in the prepared statement
                stmt.setInt(1, customer.getCustomerID());
                stmt.setString(2, customer.getName());
                stmt.setString(3, customer.getLastName());
                stmt.setDouble(4, customer.getfLexibleDiscount());
                stmt.setDouble(5, customer.getFixedDiscount());
                stmt.setInt(6, customer.getDiscount());
//            System.out.println(stmt.executeUpdate());
                int rowsInserted = stmt.executeUpdate();
                System.out.println(rowsInserted + " rows inserted.");
                System.out.println(sql);
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }

    public String getName(){
        return this.firstName;
    }
}
