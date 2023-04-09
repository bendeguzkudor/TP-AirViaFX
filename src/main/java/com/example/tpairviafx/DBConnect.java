package com.example.tpairviafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnect {
    final String DRIVER = "com.mysql.jdbc.Driver";
    final String url = "jdbc:mysql://localhost:3306/AirVia";
    String userName = "root";
    String password = "rrrrrrrr";

    public Connection getConnection() {
        return connection;
    }

    Connection connection = null;
    Statement statement = null;

    public DBConnect(){

    }
    public void connect(){
        try{
            connection = DriverManager.getConnection(url, userName, password);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            connection.close();
            statement.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

