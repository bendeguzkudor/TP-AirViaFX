package com.example.tpairviafx;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.sql.*;

public class DBConnect {
//    final String DRIVER = "com.mysql.jdbc.Driver";
//    final String url = "jdbc:mysql://localhost:3306/AirVia";
//    String userName = "root";
//    String password = "rrrrrrrr";

    private String url2 = "jdbc:mysql://smcse-stuproj00.city.ac.uk:3306/in2018g05";
    String userName2 = "in2018g05_a";
    String password2 = "lPP1uVU0";

    private Connection connection = null;
    private Statement statement = null;

    public DBConnect() {}

    public synchronized void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url2, userName2, password2);
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DBConnect db = new DBConnect();
        db.connect();
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

    public void backupDatabase10(String host, int port, String user, String password, String databaseName, String backupFilePath) throws IOException, InterruptedException {
        String[] command = {
                "/usr/local/mysql-8.0.32-macos13-x86_64/bin/mysqldump",
                "--skip-column-statistics",
                "-h", host,
                "-P", Integer.toString(port),
                "-u", user,
                "-p" + password,
                databaseName
        };
        Process processCommand = new ProcessBuilder(command).start();
        InputStream istream = processCommand.getInputStream();
        FileOutputStream ostream = new FileOutputStream(backupFilePath);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = istream.read(buffer)) != -1) {
            ostream.write(buffer, 0, bytesRead);
        }
        ostream.close();
        int exitCode = processCommand.waitFor();
        if (exitCode != 0) {
            System.err.println("Database backup failed with exit code: " + exitCode);
        } else {
            System.out.println("Database backup successful");
        }
    }
}



