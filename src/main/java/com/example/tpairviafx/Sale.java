package com.example.tpairviafx;

import java.util.ArrayList;

public class Sale {
    private int staffID;
    private Blank blank;
    private int price;
    private ArrayList flights;
    private String date;
    private int saleID;
    private long blankID;
    private Customer customer;
    private double commisionRate;
    private double commisionSum;
    private int type; //interline or domestic 0 or 1.
    private int airportTax;
    private boolean latePayment;
    private double discount; // percentage

    public Sale(int staffID, int price, String date, int saleID, Customer customer, double commisionRate, int type, boolean latePayment, double discount, long blankID){
        flights = new ArrayList<FlightModel>();
        commisionSum = commisionRate * price;
        this.staffID = staffID;
        this.price = price;
        this.date = date;
        this.saleID = saleID;
        this.customer = customer;
        this.commisionRate = commisionRate;
        this.type = type;
        this.latePayment = latePayment;
        this.discount = discount;
        this.blankID = blankID;
    }
    public void uploadToDatabase(){

    }
    public void printSale(){
        System.out.println("Staff ID: "+ staffID + " Price: "+price+" Date: "+date+" saleID: "+saleID+
                " Customer name : "+customer.getName()+" CustomerID: "+customer.getCustomerID()+
                " Commission Rate: : "+commisionRate+" Type: "+type+" Latepayment : "+latePayment+" Discount: "+discount+"BlankID: " +blankID);
    }

}
