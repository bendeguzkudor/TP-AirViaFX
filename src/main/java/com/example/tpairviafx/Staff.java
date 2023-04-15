package com.example.tpairviafx;

/**Class to hold staff information  */
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

    /**
     Constructs a Staff object with the given name and staff ID.
     @param name the name of the staff member
     @param staffID the staff ID of the staff member
     */
    public Staff(String name, int staffID){
        this.name = name;
        this.staffID = staffID;

    }
}
