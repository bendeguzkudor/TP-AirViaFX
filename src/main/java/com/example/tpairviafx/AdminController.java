package com.example.tpairviafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.xml.transform.Result;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.ResourceBundle;

/** Controller class for the admin stage */

public class AdminController implements Initializable {

    @FXML
    private Button addButton;


    @FXML
    private TextField amountTextField;
    @FXML
    private TextField commissionTextField;
    @FXML
    private TextField searchBlankIDTextField;

    @FXML
    private ChoiceBox<String> choiceBox2;

    @FXML
    private ChoiceBox<String> commissionChoiceBox;
    @FXML
    private TableColumn<Blank, Long> blankIDColumn;
    @FXML
    private TableColumn<Blank, Integer> assignedToColumn;
    @FXML
    private TableColumn<Blank, Integer> soldColumn;
    @FXML
    private TableColumn<Blank, String> dateAddedColumn;
    @FXML
    private TableColumn<Blank, String> dateAssignedColumn;
    @FXML
    private TableView blanksTableView;

    private String sql;
    private String sqlForMaxBlank;
    private Long blankID;
    private String date;
    private int staffID;
    private int amount;
    private String chosenBlank;
    private final String [] blankTypes = {"444", "440","420","201","101","451","452"};
    private ResultSet rs;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Stage stage;

    ObservableList<Blank> blankObservableList = FXCollections.observableArrayList();
    private ObservableList<Blank> selectedBlanksList;
    private String chosenCommission;



    /**
     Initializes the controller class.
     This method is automatically called by the FXMLLoader when the corresponding FXML file is loaded.
     It sets up the UI elements and populates the choice boxes.
     It also sets up event handlers for the choice boxes and populates the table with existing blank entries.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        date = Application.getDate();

        choiceBox2.getItems().addAll(blankTypes);
        choiceBox2.setOnAction(this::getBlankChosen);
        commissionChoiceBox.getItems().addAll("Interline","Domestic");
        commissionChoiceBox.setOnAction(this::getCommisionChosen);
        populateBlankTable();
//        dataBaseBackup();


    }
    public void getBlankChosen(ActionEvent e){
        chosenBlank = choiceBox2.getValue();
    }
    public void getCommisionChosen(ActionEvent e){
         chosenCommission= commissionChoiceBox.getValue();
    }


    public AdminController(){



    }
    /**

     Adds a specified amount of blanks to the database.
     The starting blank ID is set to be the maximum blank ID plus one.
     The staff ID and sold status are set to default values.
     The date added is set to the current date.
     @throws SQLException If there is an error executing the SQL statement.
     */
    public void addBlanks(){
        System.out.println("addblanks");
        System.out.println(blankID);
        Long rangeFrom = getMaxBlank();

        amount = Integer.parseInt(amountTextField.getText());
        try {
            for (int i = 1; i <= amount; i++) {
                sql = "INSERT INTO blanks (blankID, staffID, sold, dateAdded) VALUES (?,?,?,?)";

            DBConnect db = new DBConnect();
            db.connect();
            PreparedStatement stmt = db.getConnection().prepareStatement(sql);

            // Set the values of the parameters in the prepared statement
            stmt.setLong(1, rangeFrom + i);
            stmt.setInt(2, 0);
            stmt.setInt(3, 0);
            stmt.setString(4, date);
                System.out.println(blankID);
            stmt.executeUpdate();
            System.out.println("ran");}

        }catch (SQLException e){
        e.printStackTrace();}
        refreshTable();


    }


    public static void main(String[] args) throws IOException, InterruptedException {
        // ,/usr/local/mysql-8.0.32-macos13-x86_64/bin/mysqldump
//     dbConnect.backupDatabase10("smcse-stuproj00.city.ac.uk",3306,"in2018g05_a","lPP1uVU0","in2018g05","/Users/bedikudor/Documents/tpairviafx/TP-AirViaFX/src/DB_Backup/backup"+randomnumber+".sql");
    DBConnect dbConnect = new DBConnect();
    dbConnect.restoreDatabase("smcse-stuproj00.city.ac.uk in2018g05","in2018g05_a", "lPP1uVU0", "in2018g05","/Users/bedikudor/Documents/tpairviafx/TP-AirViaFX/src/DB_Backup/backup91.sql");
    }

    /** This method updates the commission rates for the system. Which is from GUI input*/
    public void setCommission(){
        String sql = "UPDATE commission_rates SET "+commissionChoiceBox.getValue().toLowerCase()+" = "+Double.parseDouble(commissionTextField.getText())+"";
        DBConnect db = new DBConnect();
            db.connect();
            int effected = db.executeUpdate(sql);

            System.out.println(effected);
            if (effected !=0) {
                System.out.println("Commission'"+commissionChoiceBox.getValue().toLowerCase()+" updated to"+Double.parseDouble(commissionTextField.getText())+" ");
                System.out.println(effected);
            }
            db.closeConnection();
    }
    /**This method refreshed the blanks table when a change has been made*/
    public void refreshTable(){
        blankObservableList.clear();
        populateBlankTable();
        blanksTableView.refresh();
    }

    /**

     Retrieves the maximum blank ID from the database based on the chosen blank type.
     Initializes the value to the starting value of the blank type if there are no existing blank IDs of that type in the database.
     @return A Long value representing the maximum blank ID.
     */


    public Long getMaxBlank(){
        long maxBlankID = 0;
        long initialize = 0;
//        System.out.println(type);

        switch (chosenBlank){
            case "444":
                sqlForMaxBlank = "select max(blankID) from blanks where SUBSTR(blankID, 1, 3) = '444'";
                System.out.println(sql);
                initialize = 44400000000L;
                break;
            case "440":
                sqlForMaxBlank = "select max(blankID) from blanks where SUBSTR(blankID, 1, 3) = '440'";
                System.out.println(sql);
                initialize = 44000000000L;
                break;
            case "420":
                sqlForMaxBlank = "select max(blankID) from blanks where SUBSTR(blankID, 1, 3) = '420'";
                System.out.println(sql);
                initialize = 42000000000L;
                break;
            case "201":
                sqlForMaxBlank = "select max(blankID) from blanks where SUBSTR(blankID, 1, 3) = '201'";
                System.out.println(sql);
                initialize = 20100000000L;
                break;
            case "101":
                sqlForMaxBlank = "select max(blankID) from blanks where SUBSTR(blankID, 1, 3) = '101'";
                System.out.println(sql);
                initialize = 10100000000L;
                break;
            case "451":
                sqlForMaxBlank = "select max(blankID) from blanks where SUBSTR(blankID, 1, 3) = '451'";
                System.out.println(sql);
                initialize = 45100000000L;
                break;
            case "452":
                sqlForMaxBlank = "select max(blankID) from blanks where SUBSTR(blankID, 1, 3) = '452'";
                System.out.println(sql);
                initialize = 45200000000L;
                break;
        }
        DBConnect db = new DBConnect();
        try {
            db.connect();
            ResultSet rs = db.executeQuery(sqlForMaxBlank);
            rs.next();

            maxBlankID = rs.getLong(1);
            System.out.println("Max value is: " + maxBlankID);
            db.closeConnection();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println(maxBlankID);
        if(maxBlankID == 0){
            return initialize;
        }
        return maxBlankID;

    }

    /**

     Populates the TableView with Blank objects queried from the database. Also sets up filtering and sorting for the TableView.
     Sets up the PropertyValueFactory for each column to map to the corresponding Blank object property.
     Adds a listener to the searchBlankIDTextField to filter the data in the TableView based on the entered text.
     Finally, sets the items of the TableView to the sorted data and assigns the selected items to the selectedBlanksList.
     @throws RuntimeException if there is an SQL exception when executing the query or when closing the database connection
     */
    public void populateBlankTable(){
        DBConnect db = new DBConnect();
//        ResultSet rs;
        String sql = "SELECT * FROM blanks"; //query for dynamic search
//        String sql2 = "SELECT * FROM flights WHERE departure = \"London\" AND arrival = \"budapest\" AND date = \"23-05-10\"";
        try{
            db.connect();
            rs = db.executeQuery(sql);
            while(rs.next()){
                Long queryBlankID = rs.getLong("blankID");
                Integer queryStaffID = rs.getInt("staffID");
                String querydateAssinged = rs.getString("dateAssigned");
                Integer querySold = rs.getInt("sold");
                String querydateAdded = rs.getString("dateAdded");


                //Populate the list
                blankObservableList.add(new Blank(queryBlankID,queryStaffID,
                        querydateAssinged,querySold,querydateAdded));
            }

            blankIDColumn.setCellValueFactory(new PropertyValueFactory<>("blankID"));
            assignedToColumn.setCellValueFactory(new PropertyValueFactory<>("staffID"));
            dateAssignedColumn.setCellValueFactory(new PropertyValueFactory<>("dateAssigned"));
            soldColumn.setCellValueFactory(new PropertyValueFactory<>("sold"));
            dateAddedColumn.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));

            blanksTableView .setItems(blankObservableList);

            FilteredList<Blank> filteredData = new FilteredList<>(blankObservableList, b->true);
            searchBlankIDTextField.textProperty().addListener((observable, oldValue, newValue)->{
                filteredData.setPredicate(blankSearchModel -> {
                    if(newValue.isEmpty() || newValue.isBlank() || newValue == null){
                        return true;
                    //NEEDS FIXING//
                    }
                    return blankSearchModel.getBlankID() > -1;

                });

            });
            SortedList<Blank> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(blanksTableView.comparatorProperty());
            blanksTableView.setItems(sortedData);
            selectedBlanksList = blanksTableView.getSelectionModel().getSelectedItems();
            System.out.println("selected flight");

        } catch (SQLException e){
            throw new RuntimeException(e);
        }finally {
            db.closeConnection();
        }


    }
    /** Method for logging out , switches the stage to login screen */
    public void logOut() throws IOException {
        Application.logOut(stage);
    }
    /**

     Removes a selected blank from the database and refreshes the table.
     @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    public void removeBlank() throws SQLException {

        String sql = "DELETE FROM blanks WHERE blankID = ?";
        System.out.println(blankID);
        System.out.println(selectedBlanksList.get(0).getBlankID());


        DBConnect db = new DBConnect();
        db.connect();
        PreparedStatement pstmt = db.getConnection().prepareStatement(sql);

        pstmt.setLong(1, selectedBlanksList.get(0).getBlankID());


        int rowsDeleted = pstmt.executeUpdate();


        if (rowsDeleted > 0) {
            System.out.println("Rows deleted successfully!");
        } else {
            System.out.println("No rows were deleted.");
        }

        pstmt.close();
        db.closeConnection();
        int selectedIndex = blanksTableView.getSelectionModel().getSelectedIndex();
//        blankObservableList.remove(selectedIndex);
        refreshTable();

    }

    /** Backs up the database to src folder DB_Backup folder*/
    public void backupDatabase() throws IOException, InterruptedException {
        DBConnect dbConnect = new DBConnect();
        dbConnect.connect();
        Random random = new Random();
        String randomnumber = String.valueOf(random.nextInt(1,1000));
        // lPP1uVU0
//        dbConnect.backupDatabase2("/Users/bedikudor/Documents/tpairviafx/TP-AirViaFX/src/DB_Backup/backup"+randomnumber+".sql");
//        dbConnect.backupDatabase2("localhost","3306","AirVia","root","rrrrrrrr","/Users/bedikudor/Documents/tpairviafx/TP-AirViaFX/src/DB_Backup/backup"+randomnumber+".sql");
        // /usr/local/mysql-8.0.32-macos13-x86_64/bin/mysqldump -h 10.205.18.228 -P 3306 -u in2018g05_ -p in2018g05_ backupmysql.sql
        // 10.205.18.228
        dbConnect.backupDatabase10("smcse-stuproj00.city.ac.uk",3306,"in2018g05_a","lPP1uVU0","in2018g05","/Users/bedikudor/Documents/tpairviafx/TP-AirViaFX/src/DB_Backup/backup"+randomnumber+".sql");

    }

}
