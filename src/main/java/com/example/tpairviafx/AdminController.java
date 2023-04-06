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
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private Button addButton;


    @FXML
    private TextField amountTextField;
    @FXML
    private TextField searchBlankIDTextField;

    @FXML
    private ChoiceBox<String> choiceBox2;
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
    private String [] blankTypes = {"444", "440","420","201","101"};
    private ResultSet rs;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Stage stage;

    ObservableList<Blank> blankObservableList = FXCollections.observableArrayList();
    private ObservableList<Blank> selectedBlanksList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        date = Application.getDate();

        choiceBox2.getItems().addAll(blankTypes);
        choiceBox2.setOnAction(this::getBlankChosen);
        populateBlankTable();


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
                sql = "INSERT INTO blanks (blankID, staffID, sold, dateAdded) VALUES (?,?,?,?)";

            DBConnect db = new DBConnect();
            db.connect();
            PreparedStatement stmt = db.connection.prepareStatement(sql);

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

    public static void main(String[] args) {

    }
    public void refreshTable(){
        blankObservableList.clear();
        populateBlankTable();
        blanksTableView.refresh();
    }

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
        if(maxBlankID == 0){
            return initialize;
        }
        return maxBlankID;
    }
    public void populateBlankTable(){
        DBConnect db = new DBConnect();
//        ResultSet rs;
        String sql = "SELECT * FROM blanks"; //query for dynamic search
//        String sql2 = "SELECT * FROM flights WHERE departure = \"London\" AND arrival = \"budapest\" AND date = \"23-05-10\"";
        try{
            db.connect();
            rs = db.statement.executeQuery(sql);
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
                    if(blankSearchModel.getBlankID() > -1){
                        return true;

                    }else{
                        return false;
                    }

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
    public void logOut() throws IOException {
        Application.logOut(stage);
    }
    public void removeBlank() throws SQLException {

        String sql = "DELETE FROM blanks WHERE blankID = ?";
        System.out.println(blankID);
        System.out.println(selectedBlanksList.get(0).getBlankID());

// Create a PreparedStatement object
        DBConnect db = new DBConnect();
        db.connect();
        PreparedStatement pstmt = db.connection.prepareStatement(sql);

// Set the parameter value
        pstmt.setLong(1, selectedBlanksList.get(0).getBlankID());

// Execute the DELETE query
        int rowsDeleted = pstmt.executeUpdate();

// Check if any rows were deleted
        if (rowsDeleted > 0) {
            System.out.println("Rows deleted successfully!");
        } else {
            System.out.println("No rows were deleted.");
        }
// Close the resources
        pstmt.close();
        db.closeConnection();
        int selectedIndex = blanksTableView.getSelectionModel().getSelectedIndex();
//        blankObservableList.remove(selectedIndex);
        refreshTable();

    }
}
