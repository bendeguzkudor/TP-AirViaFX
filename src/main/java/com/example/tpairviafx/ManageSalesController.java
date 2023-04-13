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

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ManageSalesController implements Initializable {


    public Scene getPreviousScene() {
        return previousScene;
    }

    public void setPreviousScene(Scene previousScene) {
        this.previousScene = previousScene;
    }

    private Scene previousScene;

    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    private int staffID;

    @FXML
    private TableColumn<SaleForm, Integer> customercolumn;

    @FXML
    private TableColumn<SaleForm, String> firstnamecolumn;

    @FXML
    private TableColumn<SaleForm, String> lastnamecolumn;

    @FXML
    private TableColumn<SaleForm, Integer> pricecolumn;

    @FXML
    private TableColumn<SaleForm, Integer> saleIDColumn;

    @FXML
    private TableColumn<SaleForm, Integer> staffIDColumn;
    ObservableList<SaleForm> saleObservableList = FXCollections.observableArrayList();
    private ObservableList<SaleForm> selectedSaleList;
    @FXML
    private TableView saleTableView;
    @FXML
    private TextField searchTextfield;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        populateSaleTable();
        System.out.println(staffID);


    }
    public void refresh(){
        populateSaleTable();
    }
    public  void populateSaleTable(){
        saleObservableList.clear();
        DBConnect db = new DBConnect();

//        ResultSet rs;
        String sql = "SELECT staffID, sale.saleID, sale.price , sale.customerID, customer.firstname, customer.lastname FROM sale " +
                "JOIN customer ON sale.customerID = customer.id where staffID ="+staffID+" and refund = 0;";
        ResultSet rs;

        try{
            db.connect();
            rs = db.executeQuery(sql);
            while(rs.next()){
                Integer queryStaffID = rs.getInt("staffID");
                System.out.println(queryStaffID);
                Integer querySaleID = rs.getInt(2);
                System.out.println(querySaleID);
                Integer queryprice = rs.getInt(3);
                System.out.println(queryprice);
                Integer queryCustomerID = rs.getInt(4);
                String queryfirstname = rs.getString(5);
                String querylastname = rs.getString(6);

                //Populate the list
                saleObservableList.add(new SaleForm(queryStaffID, querySaleID,queryprice,queryCustomerID, queryfirstname,querylastname));
            }
            staffIDColumn.setCellValueFactory(new PropertyValueFactory<>("staffID"));
            saleIDColumn.setCellValueFactory(new PropertyValueFactory<>("saleID"));
            pricecolumn.setCellValueFactory(new PropertyValueFactory<>("price"));
            customercolumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            firstnamecolumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
            lastnamecolumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));



            saleTableView.setItems(saleObservableList);

            FilteredList<SaleForm> filteredData = new FilteredList<>(saleObservableList, b->true);
            searchTextfield.textProperty().addListener((observable, oldValue, newValue)->{
                filteredData.setPredicate(salesearchmodel -> {
                    if(newValue.isEmpty() || newValue.isBlank() || newValue == null){
                        return true;
                    }
                    String searchkeyword = newValue.toLowerCase();
                    if(salesearchmodel.getFirstname().toLowerCase().indexOf(searchkeyword) > -1){
                        return true;

                    }else
                        return salesearchmodel.getLastname().toLowerCase().indexOf(searchkeyword) > -1;
                });
            });
            SortedList<SaleForm> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(saleTableView.comparatorProperty());
            saleTableView.setItems(sortedData);
            selectedSaleList = saleTableView.getSelectionModel().getSelectedItems();
//            System.out.println("selected customer");

        } catch (SQLException e){
            throw new RuntimeException(e);
        }finally {
            db.closeConnection();
        }
    }
    public void markForRefund() throws SQLException {
        int saleID = selectedSaleList.get(0).getSaleID();
        String sql = "UPDATE  sale set refund = -1 where saleID = "+saleID+"";
        DBConnect dbConnect = new DBConnect();
        dbConnect.connect();
        PreparedStatement preparedStatement = dbConnect.getConnection().prepareStatement(sql);
        preparedStatement.executeUpdate();
        populateSaleTable();


    }
}