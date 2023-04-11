package com.example.tpairviafx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;

public class DBConnect {
    final String DRIVER = "com.mysql.jdbc.Driver";
    final String url = "jdbc:mysql://localhost:3306/AirVia";
    String userName = "root";
    String password = "rrrrrrrr";

    private Connection connection = null;
    private Statement statement = null;

    public DBConnect() {}

    public synchronized void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, userName, password);
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void closeConnection() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized Connection getConnection() {
        return connection;
    }

    public synchronized ResultSet executeQuery(String query) {
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public synchronized int executeUpdate(String query) {
        int rowsUpdated = 0;
        try {
            rowsUpdated = statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowsUpdated;
    }
    public synchronized void backup(String backupFilePath) {
        try {
            // Get the database name
            String dbName = "AirVia";

            // Set up the command to run mysqldump
            String[] command = new String[]{"mysqldump", "-u", "root", "-p","rrrrrrrr", dbName};

            // Run the command and redirect the output to the backup file
            Process process = Runtime.getRuntime().exec(command);
            InputStream inputStream = process.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(backupFilePath);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            fileOutputStream.close();

            // Wait for the process to finish
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


