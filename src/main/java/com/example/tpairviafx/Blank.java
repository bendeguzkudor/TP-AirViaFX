package com.example.tpairviafx;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Blank {
    public long getBlankID() {
        return blankID;
    }

    private long blankID;
    private String sql;
    private int noOfFlights;
    private int staffID;
    private ResultSet rs;

    public Blank(int staffID, int noOfFlights, String flightType) throws SQLException {
        this.noOfFlights = noOfFlights;
        this.staffID = staffID;
        retrieveBlankID(flightType);

    }



    public void retrieveBlankID(String flightType) throws SQLException {
        switch (flightType){
            case "Interline":
                if(noOfFlights > 2){
                    sql = "SELECT blankID FROM blanks WHERE SUBSTR(blankID, 1, 3) = '440' AND staffID ='"+ staffID +"'";
                }else if (noOfFlights <= 2){
                    sql = "SELECT blankID FROM blanks WHERE SUBSTR(blankID, 1, 3) = '420' AND staffID ='"+ staffID +"'";
                }
                break;
            case "Domestic":
                if(noOfFlights == 2){
                    sql = "SELECT blankID FROM blanks WHERE SUBSTR(blankID, 1, 3) = '201' AND staffID ='"+ staffID +"'";
                }else{
                    sql = "SELECT blankID FROM blanks WHERE SUBSTR(blankID, 1, 3) = '101' AND staffID ='"+ staffID +"'";
                }
        }
        DBConnect db = new DBConnect();
        try {
            db.connect();
            rs = db.statement.executeQuery(sql);
            System.out.println(sql);
            if (rs.next()) {
                this.blankID = rs.getLong("blankID");
//                System.out.println(rs.getInt("blankID"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            if(rs!= null){
                rs.close();
            }
            db.closeConnection();
        }
    }

    public static void main(String[] args) throws SQLException {
        Blank blank = new Blank(888, 1, "Interline");
        System.out.println(blank.blankID);


    }

}

