package com.example.tpairviafx;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;


/** Controller class for the customer registration form
 * @author  Bendeguz Kudor*/
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




    /**

     Records a new customer with the details entered in the GUI text fields. The method creates a new Customer object with the
     details entered in the text fields and saves the object in the database using the Customer.recordCustomer() method.
     After successfully recording the customer, it repopulates the customer table view in the GUI and closes the current window.
     */
public void record(){

    Customer.recordCustomer(new Customer(firstNameTextField.getText(), lastNameTextField.getText(),emailTextField.getText(),mobileTextField.getText(),0.0,0.0,0.0,0.0));
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
                ResultSet rs = dbConnect.executeQuery("select max(customerID) from customer");
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
