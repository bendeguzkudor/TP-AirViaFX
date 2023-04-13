package com.example.tpairviafx;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.IOException;
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
        Restoredbfromsql("DB_Backup/backup91.sql");
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
    public static void Restoredbfromsql(String s) {
        try {
            /*NOTE: String s is the mysql file name including the .sql in its name*/
            /*NOTE: Getting path to the Jar file being executed*/
            /*NOTE: YourImplementingClass-> replace with the class executing the code*/
            // backup91.sql
            CodeSource codeSource = DBConnect.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            String jarDir = jarFile.getParentFile().getPath();

            /*NOTE: Creating Database Constraints*/
            String dbName = "in2018g05";
            String dbUser = "in2018g05_a";
            String dbPass = "lPP1uVU0";

            /*NOTE: Creating Path Constraints for restoring*/
            String restorePath = jarDir + "DB_Backup" + "/" + s;

            /*NOTE: Used to create a cmd command*/
            /*NOTE: Do not create a single large string, this will cause buffer locking, use string array*/
            String[] executeCmd = new String[]{"/usr/local/mysql-8.0.32-macos13-x86_64/bin/mysql", dbName, "-u" + dbUser, "-p" + dbPass, "-e", " source " + restorePath};

            /*NOTE: processComplete=0 if correctly executed, will contain other values if not*/
            Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
            int processComplete = runtimeProcess.waitFor();

            /*NOTE: processComplete=0 if correctly executed, will contain other values if not*/
            if (processComplete == 0) {
                System.out.println( "Successfully restored from SQL : " + s);
            } else {
                System.out.println("Error at restoring");
            }


        } catch (URISyntaxException | IOException | InterruptedException ex) {
            ex.printStackTrace();
        }

    }
    public void restoreDatabase(String host, String user, String password, String databaseName, String commandFilePath) throws IOException, InterruptedException {
        String[] command = { "/usr/local/mysql-8.0.32-macos13-x86_64/bin/mysql " +
                "-u in2018g05_a " +
                "-plPP1uVU0 " +
                "-h smcse-stuproj00.city.ac.uk in2018g05 " +
                "< /Users/bedikudor/Documents/tpairviafx/TP-AirViaFX/src/DB_Backup/backup91.sql"
        };
        String[] command2 = {
                "/usr/local/mysql-8.0.32-macos13-x86_64/bin/mysql",
                "-u", user,
                "-p" + password,
                "-h",host," ",databaseName,
                "<", commandFilePath
        };

                Process process = Runtime.getRuntime().exec(command2);
                Process processCommand = new ProcessBuilder(command2).start();
                int exitCode = processCommand.waitFor();
                if (exitCode != 0) {
                    System.err.println("Database command failed with exit code: " + exitCode);
                } else {
                    System.out.println("Database command successful");
                }
    }
}




