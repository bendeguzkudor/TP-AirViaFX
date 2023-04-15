package com.example.tpairviafx;


/** Class to represent sales in the tableview*/
public class SaleForm {
    private int staffID;
    private int saleID;
    private int price;
    private int customerID;

    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    public int getSaleID() {
        return saleID;
    }

    public void setSaleID(int saleID) {
        this.saleID = saleID;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    private String firstname;
    private String lastname;

    /**

     SaleForm represents a form for displaying the details of a sale.
     It contains information staff ID, sale ID, customer ID, price, firstname and lastname.
     */
    public SaleForm(int staffID, int saleID, int price, int customerID, String firstname, String lastname){
        this.staffID = staffID;
        this.saleID = saleID;
        this.customerID = customerID;
        this.price = price;
        this.firstname = firstname;
        this.lastname = lastname;

    }
}
