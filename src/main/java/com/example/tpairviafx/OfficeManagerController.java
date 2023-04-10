package com.example.tpairviafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;






public class OfficeManagerController implements Initializable {
    ObservableList<Staff> staffObservableList = FXCollections.observableArrayList();

    @FXML
    private TextField searchTextField;
    @FXML
    private TextField amountTextField;
    @FXML
    private TableView staffTableView;
    @FXML
    private TableColumn<Staff, String> nameColumn;
    @FXML
    private TableColumn<Staff,Integer > staffIDColumn;

    @FXML
    private ChoiceBox<String> blankChoiceBox;
    private final String [] blankTypes = {"444", "440","420","201","101"};

    private ObservableList<Staff> selectedStaffList;
    private ResultSet rs;
    private String queryName;
    private int queryStaffID;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Stage stage;

    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }


    private int staffID;
    private String chosenBlank;
    private String sqlforMinBlank;
    private final String date = Application.getDate();
    private int amount;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateStaffTable();

        blankChoiceBox.getItems().addAll(blankTypes);
        blankChoiceBox.setOnAction(this::getblankChosen);

    }

    public void getblankChosen(ActionEvent e){
        chosenBlank = blankChoiceBox.getValue();
    }
    public void populateStaffTable(){
        System.out.println("sadgf");
        DBConnect db = new DBConnect();
//        ResultSet rs;
        String sql = "SELECT name, staffID FROM staff"; //query for dynamic search
//        String sql2 = "SELECT * FROM flights WHERE departure = \"London\" AND arrival = \"budapest\" AND date = \"23-05-10\"";
        try{
            db.connect();
            rs = db.statement.executeQuery(sql);
            while(rs.next()){
                queryStaffID = rs.getInt("staffID");
                String queryName = rs.getString("name");
                System.out.println(queryName);
                System.out.println(queryStaffID);
                //Populate the list
                staffObservableList.add(new Staff(queryName,queryStaffID));

            }

            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            staffIDColumn.setCellValueFactory(new PropertyValueFactory<>("staffID"));

            staffTableView.setItems(staffObservableList);

            FilteredList<Staff> filteredData = new FilteredList<>(staffObservableList, b->true);
            searchTextField.textProperty().addListener((observable, oldValue, newValue)->{
                filteredData.setPredicate(staffSearchModel -> {
                    if(newValue.isEmpty() || newValue.isBlank() || newValue == null){
                        return true;

                    }
                    String searchkeyword = newValue.toLowerCase();
                    if(staffSearchModel.getName().toLowerCase().indexOf(searchkeyword) > -1){
                        return true;

                    }else return staffSearchModel.getName().toLowerCase().indexOf(searchkeyword) > -1;

                });

            });
            SortedList<Staff > sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(staffTableView.comparatorProperty());
            staffTableView.setItems(sortedData);
            selectedStaffList = staffTableView.getSelectionModel().getSelectedItems();
            System.out.println("selected flight");

        } catch (SQLException e){
            throw new RuntimeException(e);
        }finally {
            db.closeConnection();
        }
    }
    public void assignBlanks(){
        System.out.println("addblanks");
        Long rangeFrom = getMinBlank();
        amount = Integer.parseInt(amountTextField.getText());
        Long rangeTo;
        if(amount == 1){
            rangeTo = rangeFrom;
        }else{
            rangeTo = rangeFrom+amount-1;
        System.out.println(rangeTo);
        }

        String sql = "UPDATE blanks SET staffID = '"+selectedStaffList.get(0).getStaffID()+"', dateAssigned = '"+date+"'" +
                "WHERE blankID BETWEEN   ?  AND   ? ";
        try {
//                String sql = "INSERT INTO blanks (blankID, staffID, dateAssigned, sold) VALUES (?,?,?,?)";

                DBConnect db = new DBConnect();
                db.connect();
                PreparedStatement stmt = db.connection.prepareStatement(sql);

                // Set the values of the parameters in the prepared statement
                stmt.setLong(1, rangeFrom);
                stmt.setLong(2, rangeTo);
                System.out.println(sql);
                stmt.executeUpdate();
                System.out.println("ran");

        }catch (SQLException e){
            e.printStackTrace();}

    }
    public Long getMinBlank(){
        long minBlank = 0;
//        System.out.println(type);

        switch (chosenBlank){
            case "444":
                sqlforMinBlank = "select min(blankID) from blanks where SUBSTR(blankID, 1, 3) = '444' and staffID = 0";
//                System.out.println(sql);
                break;
            case "440":
                sqlforMinBlank = "select min(blankID) from blanks where SUBSTR(blankID, 1, 3) = '440' and staffID = 0";
//                System.out.println(sql);
                break;
            case "420":
                sqlforMinBlank = "select min(blankID) from blanks where SUBSTR(blankID, 1, 3) = '420' and staffID = 0";
//                System.out.println(sql);
                break;
            case "201":
                sqlforMinBlank = "select min(blankID) from blanks where SUBSTR(blankID, 1, 3) = '201' and staffID = 0";
//                System.out.println(sql);
                break;
            case "101":
                sqlforMinBlank = "select min(blankID) from blanks where SUBSTR(blankID, 1, 3) = '101' and staffID = 0";
//                System.out.println(sql);
                break;

        }
        DBConnect db = new DBConnect();
        try {
            db.connect();
            Statement statement = db.statement;
            System.out.println(sqlforMinBlank);
            ResultSet rs = statement.executeQuery(sqlforMinBlank);
            rs.next();
            minBlank = rs.getLong(1);
            System.out.println("Min value is: " + minBlank);
            db.closeConnection();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println(minBlank);
        return minBlank;
    }
    public void logOut() throws IOException {
        Application.logOut(stage);
    }


}
