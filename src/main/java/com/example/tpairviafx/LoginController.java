package com.example.tpairviafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginController {
    private String username;
    private ResultSet rs;

    public TravelAdvisorController getTravelAdvisorController() {
        return travelAdvisorController;
    }

    public void setTravelAdvisorController(TravelAdvisorController travelAdvisorController) {
        this.travelAdvisorController = travelAdvisorController;
    }

    public static TravelAdvisorController travelAdvisorController;
    public static OfficeManagerController officeManagerController;

    private int role;
    private int staffID;
    @FXML
    private Button cancelButton;
    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginMessegeLabel;
    private Stage stage;
    private Scene scene;
    private FXMLLoader fxmlLoader;


    @FXML
    protected void cancelButtonOnAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void loginButtonOnAction() throws IOException, SQLException {

        if (usernameTextField.getText().isBlank() == false && passwordField.getText().isBlank() == false){
            if(authenticateLogin(usernameTextField.getText(), passwordField.getText())){
                loginMessegeLabel.setText("Succesfull Login");
                switch (role){

                    case 1:
                        loadTravelAdvisor();
                        break;
                    case 2:
                        loadOfficeManager();
                        break;
                    case 3:
                        loadAdmin();
                }
            }else {
                loginMessegeLabel.setText("Wrong Username or Password");
            }

        }else{
            loginMessegeLabel.setText("Please enter valid credentials");
        }

    }
    public void loadTravelAdvisor() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        fxmlLoader = new FXMLLoader(Application.class.getResource("TravelAdvisor.fxml"));
        scene = new Scene(fxmlLoader.load(), 1000, 800);
        stage.setScene(scene);
        travelAdvisorController = fxmlLoader.getController();
        travelAdvisorController.displayNameAndRole(username, role);
        travelAdvisorController.setStaffID(staffID);
        stage.show();

    }
    public void loadOfficeManager() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        fxmlLoader = new FXMLLoader(Application.class.getResource("OfficeManager.fxml"));
        scene = new Scene(fxmlLoader.load(), 1000, 800);
        stage.setScene(scene);
        officeManagerController = fxmlLoader.getController();
//        officeManagerController.displayNameAndRole(username, role);
        officeManagerController.setStaffID(staffID);
        stage.show();

    }
    public void loadAdmin() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        fxmlLoader = new FXMLLoader(Application.class.getResource("Admin.fxml"));
        scene = new Scene(fxmlLoader.load(), 1000, 800);
        stage.setScene(scene);
        stage.show();

    }
    public boolean authenticateLogin(String username, String password) throws SQLException {

        String sql = "SELECT * FROM staff WHERE username='" + username + "' AND password='" + password + "'";
        DBConnect db = new DBConnect();
        try{
            db.connect();
            rs = db.statement.executeQuery(sql);
            if(rs.next()){
                role = rs.getInt("role");
                staffID = rs.getInt("staffID");
                System.out.println("succesful login");
                this.username = rs.getString("username");
                return true;
            }
            else {
                System.out.println("wrong credentials");
                return false;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if(rs != null){
                rs.close();
            }

            db.closeConnection();
        }

    }
}