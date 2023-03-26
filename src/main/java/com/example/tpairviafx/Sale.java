package com.example.tpairviafx;

import java.util.ArrayList;

public class Sale {
    private int userID;
    private int price;
    private ArrayList flights;
    private String date;
    private int saleID;
    private Customer customer;
    private double commisionRate;
    private double commisionSum;
    private int type; //interline or domestic 0 or 1.
    private int airportTax;
    private boolean latePayment;
    private double discount; // percentage

    public Sale(int userID, int price,String date, int saleID, Customer customer, double commisionRate, int type, boolean latePayment, double discount){
        flights = new ArrayList<FlightModel>();
        commisionSum = commisionRate * price;
        this.userID = userID;
        this.price = price;
        this.date = date;
        this.saleID = saleID;
        this.customer = customer;
        this.commisionRate = commisionRate;
        this.type = type;
        this.latePayment = latePayment;
        this.discount = discount;
    }
    public void uploadToDatabase(){

    }
    public void printSale(){
        System.out.println("User ID: "+ userID+ " Price: "+price+" Date: "+date+" saleID: "+saleID+
                " Customer name : "+customer.getName()+" CustomerID: "+customer.getCustomerID()+
                " Commission Rate: : "+commisionRate+" Type: "+type+" Latepayment : "+latePayment+" Discount: "+discount);
    }

}
