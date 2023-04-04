package com.example.tpairviafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private Button addButton;


    @FXML
    private TextField amountTextField;

    @FXML
    private ChoiceBox<String> choiceBox2;

    private String sql;
    private String sqlForMaxBlank;
    private Long blankID;
    private String date;
    private int staffID;
    private int amount;
    private String chosenBlank;
    private String [] blankTypes = {"444", "440","420","201","101"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        date = Application.getDate();

        choiceBox2.getItems().addAll(blankTypes);
        choiceBox2.setOnAction(this::getBlankChosen);


    }
    public void getBlankChosen(ActionEvent e){
        chosenBlank = choiceBox2.getValue();
    }


    public AdminController(){



    }
    public void addBlanks(){
        System.out.println("addblanks");
        System.out.println(blankID);
        Long rangeFrom = getMaxBlank();

        amount = Integer.parseInt(amountTextField.getText());
        try {
            for (int i = 1; i <= amount; i++) {
                sql = "INSERT INTO blanks (blankID, staffID, dateAssigned, sold) VALUES (?,?,?,?)";

            DBConnect db = new DBConnect();
            db.connect();
            PreparedStatement stmt = db.connection.prepareStatement(sql);

            // Set the values of the parameters in the prepared statement
            stmt.setLong(1, rangeFrom + i);
            stmt.setInt(2, 0);
            stmt.setString(3, date);
            stmt.setInt(4, 0);
                System.out.println(blankID);
            stmt.executeUpdate();
            System.out.println("ran");}

        }catch (SQLException e){
        e.printStackTrace();}
    }

    public static void main(String[] args) {

    }
    public Long getMaxBlank(){
        long maxBlankID = 0;
//        System.out.println(type);

        switch (chosenBlank){
            case "444":
                sqlForMaxBlank = "select max(blankID) from blanks where SUBSTR(blankID, 1, 3) = '444'";
                System.out.println(sql);
                break;
            case "440":
                sqlForMaxBlank = "select max(blankID) from blanks where SUBSTR(blankID, 1, 3) = '440'";
                System.out.println(sql);
                break;
            case "420":
                sqlForMaxBlank = "select max(blankID) from blanks where SUBSTR(blankID, 1, 3) = '420'";
                System.out.println(sql);
                break;
            case "201":
                sqlForMaxBlank = "select max(blankID) from blanks where SUBSTR(blankID, 1, 3) = '201'";
                System.out.println(sql);
                break;
            case "101":
                sqlForMaxBlank = "select max(blankID) from blanks where SUBSTR(blankID, 1, 3) = '101'";
                System.out.println(sql);
                break;

        }
        DBConnect db = new DBConnect();
        try {
            db.connect();
            Statement statement = db.statement;
            System.out.println(sqlForMaxBlank);
            ResultSet rs = statement.executeQuery(sqlForMaxBlank);
            rs.next();
            maxBlankID = rs.getLong(1);
            System.out.println("Max value is: " + maxBlankID);
            db.closeConnection();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println(maxBlankID);

        return maxBlankID;
    }
}
