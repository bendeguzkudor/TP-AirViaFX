package com.example.tpairviafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerRegisterFormController {

    @FXML
    private RadioButton fixedDiscountRadioButton, flexibleDiscountRadioButton, regularRadioButton, valuedRadioButton;

    @FXML
    private ToggleGroup discount;
    @FXML
    private CheckBox valuedCheckBox;
    @FXML
    Button recordCustomerButton;

    @FXML
    private TextField discountTextField;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField mobileTextField;
    private int _discount;

    public Scene getPreviousScene() {
        return previousScene;
    }

    public void setPreviousScene(Scene previousScene) {
        this.previousScene = previousScene;
    }

    private Scene previousScene;





public void record(){
        if(fixedDiscountRadioButton.isSelected()){
            Customer.recordCustomer(new Customer(firstNameTextField.getText(), lastNameTextField.getText(),queryCustomerID(),Double.parseDouble(discountTextField.getText()),0,0));


        }else if(flexibleDiscountRadioButton.isSelected()) {
            Customer.recordCustomer(new Customer(firstNameTextField.getText(), lastNameTextField.getText(), queryCustomerID(), 0, Double.parseDouble(discountTextField.getText()), 1));
            _discount = 1;
        }else if(regularRadioButton.isSelected()) {
            flexibleDiscountRadioButton.setSelected(false);
            fixedDiscountRadioButton.setSelected(false);
            Customer.recordCustomer(new Customer(firstNameTextField.getText(), lastNameTextField.getText(),queryCustomerID(),0,0,-1));
        }
    // Pass the scene when clicking the record customer button and then , in this class get tht class from that scene and repopulate the list
    Stage stage = (Stage) recordCustomerButton.getScene().getWindow();
    LoginController.travelAdvisorController.populateCustomerTable();

    stage.close();

}
    public int queryCustomerID(){
            String sql = "select max(customerID) from customer"; // "select max(saleID) as max_id from sale"
            DBConnect dbConnect = new DBConnect();
            int maxValue = 0;
            try {
                dbConnect.connect();
                Statement statement = dbConnect.statement;
                ResultSet rs = statement.executeQuery("select max(customerID) from customer");
                rs.next();
                maxValue = rs.getInt(1);
                System.out.println("Max value is: " + maxValue);
                dbConnect.closeConnection();


            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
            return maxValue;

        }

    }
