package com.example.tpairviafx;

public class Staff {
    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    private int staffID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public Staff(String name, int staffID){
        this.name = name;
        this.staffID = staffID;

    }
}
