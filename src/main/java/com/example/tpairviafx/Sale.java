package com.example.tpairviafx;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Sale {
    private int staffID;
    private static int  counter = 0;

    private int price; // set in dollars
    private String date;
    private int saleID;
    // need array for blankIDs
    //need array for flights.
    private int localCurrency;
    private int exchangeRate;
    private long blankID;

    private Customer customer; // customer object  // need a second constructor for non-member sales
    private double commisionRate; // rate set by the airline , set by the admin?
    private double commisionSum; //calculated in the constructor
    private int type; //interline or domestic 0 or 1.
    private int airportTax;
    private boolean latePayment; // date string if allowed and date represents the deadline, null if no late payment is issued
    private double discount; // percentage dependant on the customer
    private ArrayList<Blank> blanks;

    public Sale(int staffID, int price, String date, Customer customer, double commisionRate, int type, boolean latePayment, double discount, long blankID, ArrayList<Blank> blanks){
        this.saleID = ++counter;
        this.blanks = blanks;
        commisionSum = commisionRate * price;
        this.staffID = staffID;
        this.price = price;
        this.date = date;
        this.customer = customer;
        this.commisionRate = commisionRate;
        this.type = type;
        this.latePayment = latePayment;
        this.discount = discount;
        this.blankID = blankID;
    }
    public void uploadToDatabase(){

    }
    public void addBlanks(Blank _blank){
        this.blanks.add(_blank);
    }
    public void printSale(){
        System.out.println("Staff ID: "+ staffID + " Price: "+price+" Date: "+date+" saleID: "+saleID+
                " Customer name : "+customer.getName()+" CustomerID: "+customer.getCustomerID()+
                " Commission Rate: : "+commisionRate+" Type: "+type+" Latepayment : "+latePayment+" Discount: "+discount+"BlankID: " +blankID);
    }
    public void pushToDatabase(){

    }

}
