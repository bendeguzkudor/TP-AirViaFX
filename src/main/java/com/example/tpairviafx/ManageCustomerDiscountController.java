package com.example.tpairviafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ManageCustomerDiscountController implements Initializable {

    @FXML
    private TableView customerTableView;

    @FXML
    private TextField searchnameTextfield;
    @FXML
    private TextField fixedTextfield;
    @FXML
    private TextField belowTextfield;
    @FXML
    private TextField betweenTextfield;
    @FXML
    private TextField moreTextfield;
    @FXML
    private TableColumn<Customer, Double> below1000C;

    @FXML
    private TableColumn<Customer,Double> betweenC;

    @FXML
    private TableColumn<Customer, Integer> customerIDC;

    @FXML
    private TableColumn<Customer, String> firstnameC;

    @FXML
    private TableColumn<Customer, Double> fixedC;

    @FXML
    private TableColumn<Customer, String> lastnameC;

    @FXML
    private TableColumn<Customer, Double> more2000c;
    ObservableList<Customer> customerObservableList = FXCollections.observableArrayList();
    private ObservableList<Customer> selectedCustomerList;
    private ResultSet rs;
    public Scene getPreviousScene() {
        return previousScene;
    }

    public void setPreviousScene(Scene previousScene) {
        this.previousScene = previousScene;
    }

    private Scene previousScene;

    public  void populateCustomerTable(){
        customerObservableList.clear();
        DBConnect db = new DBConnect();
//        ResultSet rs;
        String sql = "SELECT * FROM customer"; //query for dynamic search
//        String sql2 = "SELECT * FROM flights WHERE departure = \"London\" AND arrival = \"budapest\" AND date = \"23-05-10\"";
        try{
            db.connect();
            rs = db.executeQuery(sql);
            while(rs.next()){
                String queryFirtsName = rs.getString(2);
                String queryLastName = rs.getString(3);
                Integer queryCustomerID = rs.getInt(1);
                Double queryfixed = rs.getDouble(6);
                Double querybelow = rs.getDouble(7);
                Double querybetween = rs.getDouble(8);
                Double querymore = rs.getDouble(9);


                //Populate the list
                customerObservableList.add(new Customer(queryFirtsName, queryLastName,queryCustomerID,queryfixed, querybelow,querybetween,querymore));
            }
            firstnameC.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            lastnameC.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            customerIDC.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            fixedC.setCellValueFactory(new PropertyValueFactory<>("fixeddiscount"));
            below1000C.setCellValueFactory(new PropertyValueFactory<>("below1000"));
            betweenC.setCellValueFactory(new PropertyValueFactory<>("between1and2000"));
            more2000c.setCellValueFactory(new PropertyValueFactory<>("morethan2000"));


            customerTableView.setItems(customerObservableList);

            FilteredList<Customer> filteredData = new FilteredList<>(customerObservableList, b->true);
            searchnameTextfield.textProperty().addListener((observable, oldValue, newValue)->{
                filteredData.setPredicate(customerSearchModel -> {
                    if(newValue.isEmpty() || newValue.isBlank() || newValue == null){
                        return true;
                    }
                    String searchkeyword = newValue.toLowerCase();
                    if(customerSearchModel.getFirstName().toLowerCase().indexOf(searchkeyword) > -1){
                        return true;

                    }else return customerSearchModel.getLastName().toLowerCase().indexOf(searchkeyword) > -1;
                });
            });
            SortedList<Customer> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(customerTableView.comparatorProperty());
            customerTableView.setItems(sortedData);
            selectedCustomerList = customerTableView.getSelectionModel().getSelectedItems();
//            System.out.println("selected customer");

        } catch (SQLException e){
            throw new RuntimeException(e);
        }finally {
            db.closeConnection();
        }
    }
    public void resetDiscount() throws SQLException {
        if (!selectedCustomerList.isEmpty()){

            int customerID = selectedCustomerList.get(0).getCustomerID();
            String sql = "UPDATE customer " +
                    "SET " +
                    "fixeddiscount = 0.0," +
                    "  below1000 = 0.0,"+
                    "  between1and2000 = 0.0,"+
                    "  morethan2000 = 0.0 " +
                    "WHERE id = "+customerID+";";
            DBConnect db = new DBConnect();
            db.connect();
            PreparedStatement pstmt = db.getConnection().prepareStatement(sql);
            int result = pstmt.executeUpdate();
            belowTextfield.clear();
            betweenTextfield.clear();
            moreTextfield.clear();
            System.out.println(result);
            selectedCustomerList.removeAll();
            populateCustomerTable();
        }

    }
    public void setFixed() throws SQLException {
        if (!selectedCustomerList.isEmpty()
                && selectedCustomerList.get(0).getBelow1000() == 0
                && selectedCustomerList.get(0).getBetween1and2000() == 0
                && selectedCustomerList.get(0).getMorethan2000() == 0){
        int customerID = selectedCustomerList.get(0).getCustomerID();
        String sql = "UPDATE customer " +
                "SET fixeddiscount = "+Double.parseDouble(fixedTextfield.getText())/100+"" +
                "WHERE id = "+customerID+";";
        DBConnect db = new DBConnect();
        db.connect();
        PreparedStatement pstmt = db.getConnection().prepareStatement(sql);
        pstmt.executeUpdate();
        fixedTextfield.clear();
        selectedCustomerList.removeAll();
        populateCustomerTable();

        }
    }
    public void setFlexible() throws SQLException {
        if (!selectedCustomerList.isEmpty()
                && selectedCustomerList.get(0).getFixeddiscount() == 0){

        int customerID = selectedCustomerList.get(0).getCustomerID();
        String sql = "UPDATE customer " +
                "SET " +
                "  below1000 = "+Double.parseDouble(belowTextfield.getText())/100+", " +
                "  between1and2000 = "+Double.parseDouble(betweenTextfield.getText())/100+", " +
                "  morethan2000 = "+Double.parseDouble(moreTextfield.getText())/100+" " +
                "WHERE id = "+customerID+";";
        DBConnect db = new DBConnect();
        db.connect();
        PreparedStatement pstmt = db.getConnection().prepareStatement(sql);
        int result = pstmt.executeUpdate();
        belowTextfield.clear();
        betweenTextfield.clear();
        moreTextfield.clear();
        System.out.println(result);
        selectedCustomerList.removeAll();
        populateCustomerTable();
        }
    }
    public void cancel(){
        Stage stage = (Stage) searchnameTextfield.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateCustomerTable();
    }
}
