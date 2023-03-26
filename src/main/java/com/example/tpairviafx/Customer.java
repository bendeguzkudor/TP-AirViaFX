package com.example.tpairviafx;

public class Customer {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    private int customerID;

    public Customer(String name, int customerID){
        this.name = name;
        this.customerID = customerID; // 0 if not regular customer, something if it is a recorded customer
    }
    public String getName(){
        return this.name;
    }
}
