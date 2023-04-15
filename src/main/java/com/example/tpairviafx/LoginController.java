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

/**Controller class for login screen
 * @author  Bendeguz Kudor */
public class LoginController {
    private String username;
    private ResultSet rs;

    public TravelAdvisorController getTravelAdvisorController() {
        return travelAdvisorController;
    }

    public void setTravelAdvisorController(TravelAdvisorController travelAdvisorController) {
        LoginController.travelAdvisorController = travelAdvisorController;
    }

    public static TravelAdvisorController travelAdvisorController;
    public static OfficeManagerController officeManagerController;

    public static AdminController adminController;

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



    /** Method that gets called when pressing cancel button,
     * closes the current stage */
    @FXML
    protected void cancelButtonOnAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


       /** This method gets called when the user presses to login button on screen
        * calls
        * @method authenticateLogin
        * method to search for the username and password entered to find a match in the database
        * if a match is found the system proceeds wiht the login
        * determines the correct stage to load dependent on the staff role
        * @throws SQLException if an SQL exception occurs
        * @throws IOException if an I/O exception occurs*/
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
    /**Loads the travel adivsor stage and controller, passes the staffID of the user that has logged in to the controller class,
     * and calls the
     * displayNameAndRole to display the currently logged in staffs details
     * @throws IOException if the fxml file cannot be loaded*/
    public void loadTravelAdvisor() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        fxmlLoader = new FXMLLoader(Application.class.getResource("TravelAdvisor.fxml"));
        scene = new Scene(fxmlLoader.load(), 1000, 800);
        stage.setScene(scene);
        travelAdvisorController = fxmlLoader.getController();
        travelAdvisorController.displayNameAndRole(username, role);
        travelAdvisorController.setStaffID(staffID);
        travelAdvisorController.setStage(stage);
        stage.show();

    }
    /**Loads the office manager stage and controller, passes the staffID of the user that has logged in to the controller class*/
    public void loadOfficeManager() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        fxmlLoader = new FXMLLoader(Application.class.getResource("OfficeManager.fxml"));
        scene = new Scene(fxmlLoader.load(), 1000, 800);
        stage.setScene(scene);
        officeManagerController = fxmlLoader.getController();
//        officeManagerController.displayNameAndRole(username, role);
        officeManagerController.setStaffID(staffID);
        officeManagerController.setStage(stage);
        stage.show();

    }
    /**Loads the admin  stage and controller, passes the staffID of the user that has logged in to the controller class*/
    public void loadAdmin() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        fxmlLoader = new FXMLLoader(Application.class.getResource("Admin.fxml"));
        scene = new Scene(fxmlLoader.load(), 850, 700);
        adminController = fxmlLoader.getController();
        adminController.setStage(stage);
        stage.setScene(scene);
        stage.show();

    }
    /**

     Authenticates the login of a user by checking the username and password against the staff table in the database.
     If the username and password are valid, sets the instance variables role, staffID, and username accordingly.
     @param username The username to authenticate.
     @param password The password to authenticate.
     @return True if the authentication is successful, false otherwise.
     */
    public boolean authenticateLogin(String username, String password) throws SQLException {

        String sql = "SELECT * FROM staff WHERE username='" + username + "' AND password='" + password + "'";
        DBConnect db = new DBConnect();
        try{
            db.connect();
            rs = db.executeQuery(sql);
            if(rs.next()){
                role = rs.getInt("role");
                staffID = rs.getInt("staffID");
                System.out.println("succesful login");
                this.username = rs.getString("name");
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