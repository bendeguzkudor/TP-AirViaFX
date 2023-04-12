package com.example.tpairviafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.example.tpairviafx.Blank.getManualTicketingBlank;

public class ManualTicketingController implements Initializable {
    public Scene getPreviousScene() {
        return previousScene;
    }

    public void setPreviousScene(Scene previousScene) {
        this.previousScene = previousScene;
    }

    private Scene previousScene;
    @FXML
    private TableColumn<FlightModel, Integer> blankflightNumberColumn;
    @FXML
    private TableColumn<FlightModel, String>  blankdepartureColumn;
    @FXML
    private TableColumn<FlightModel, String>blankarrivalColumn;
    @FXML
    private TableColumn<FlightModel, String> blankdateColumn;
    @FXML
    private TableColumn<FlightModel, String> blankpriceColumn;

    @FXML
    private TableView currentBlankTableView;
    @FXML
    private DatePicker datepicker;


    @FXML
    private TextField departuretextfield;
    @FXML
    private TextField blankinuseTextfield;

    @FXML
    private TextField flightnumbertextfield;

    @FXML
    private TextField pricetextfield;
    @FXML
    private TextField arrivaltextfield;
    @FXML
    private TextField timetextfield;


    @FXML
    private TableColumn<Customer, Integer> customerIDColumn;
    @FXML
    private TableColumn<Customer, String> customerFirstameColumn;
    @FXML
    private TableColumn<Customer, String> customerLastnameColumn;

    @FXML
    private TableView customerTableView;
    private String salePayment;

    @FXML
    private Button recordCustomerButton;

    @FXML
    private TextField searchCustomerField;

    @FXML
    private Label searchCustomerLabel;
    @FXML
    private RadioButton cashRadiobutton;
    @FXML
    private RadioButton cardRadiobutton;

    @FXML
    private Button selectCustomerButton;
    private ObservableList<Customer> selectedCustomerList;
    private Blank blank;
    private ArrayList<Blank> blankArrayList;
    @FXML
    private TextField cardNumberTextField;


    @FXML
    void recordNewCustomer(ActionEvent event) {}
    private ArrayList<FlightModel> flightsToBlank;

    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    private int staffID;

    private Customer customer;
    ObservableList<FlightModel> flightsOnBlankList = FXCollections.observableArrayList();
    ObservableList<Customer> customerObservableList = FXCollections.observableArrayList();

    public ManualTicketingController(){
        System.out.println(staffID);
    }
    public  void populateCustomerTable(){
        customerObservableList.clear();
        DBConnect db = new DBConnect();
        ResultSet rs;
        String sql = "SELECT * FROM customer"; //query for dynamic search
//        String sql2 = "SELECT * FROM flights WHERE departure = \"London\" AND arrival = \"budapest\" AND date = \"23-05-10\"";
        try{
            db.connect();
            rs = db.executeQuery(sql);
            while(rs.next()){
                Integer queryCustomerID = rs.getInt("id");
                String queryFirtsName = rs.getString("firstName");
                String queryLastName = rs.getString("lastName");


                //Populate the list
                customerObservableList.add(new Customer(queryFirtsName, queryLastName,queryCustomerID));
            }
            customerFirstameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            customerLastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));


            customerTableView .setItems(customerObservableList);

            FilteredList<Customer> filteredData = new FilteredList<>(customerObservableList, b->true);
            searchCustomerField.textProperty().addListener((observable, oldValue, newValue)->{
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
    public void selectCustomer(){
        System.out.println("hb");
        selectedCustomerList = customerTableView.getSelectionModel().getSelectedItems();
        customer = selectedCustomerList.get(0);
        System.out.println("sadfasdf");
        if (customer.getQueryDiscount() != 0){
            discountAlert();
        }

        System.out.println(customer.getCustomerID());
    }
    public void getPayment(){
        if (cashRadiobutton.isSelected()){
            salePayment = "Cash";
            cardNumberTextField.setDisable(true);
        }else {
            salePayment = "Card";
            cardNumberTextField.setDisable(false);
        }
    }
    public void blockCardtext(){
        cashRadiobutton.setOnAction(event -> {
            if (cashRadiobutton.isSelected()) {
                cardNumberTextField.setDisable(true);
            } else {
                cardNumberTextField.setDisable(false);
            }
        });
    }
    public void discountAlert(){
        Alert discountAlert = new Alert(Alert.AlertType.CONFIRMATION);
        discountAlert.setTitle("Discount");
        discountAlert.setHeaderText("A discount for this customer is applicable");
        discountAlert.setContentText("This customer has is on a fixed discount plan, Would you like to apply the discount to this sale ? ");
        ButtonType applybutton = new ButtonType("Apply");
        ButtonType declinebutton = new ButtonType("Discard");
        discountAlert.getButtonTypes().setAll(applybutton,declinebutton);
        Optional<ButtonType> result = discountAlert.showAndWait();
        if (discountAlert.getResult() == applybutton){
            System.out.println("Applied");
        }else{
            customer.setQueryDiscount(0);
            System.out.println("discarded");
        }
    }
    public void addToBlankTable(FlightModel flightModel){

        flightsOnBlankList.add(flightModel);
        blankflightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
        blankdepartureColumn.setCellValueFactory(new PropertyValueFactory<>("departure"));
        blankarrivalColumn.setCellValueFactory(new PropertyValueFactory<>("arrival"));
        blankdateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        blankpriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        currentBlankTableView.setItems(flightsOnBlankList);

    }
    public void addFlightsToFlights() throws SQLException {
            flightsToBlank.add(new FlightModel(Integer.parseInt(flightnumbertextfield.getText()),departuretextfield.getText(),arrivaltextfield.getText(),String.valueOf(datepicker.getValue()), timetextfield.getText(), Integer.parseInt(pricetextfield.getText())));
//            selectedFlightsList.get(0).retrieveFlightType();
            addToBlankTable(flightsToBlank.get(0));
        }

    public void addToBlank() throws SQLException {
//        blanks.add(new Blank())
        if(!flightsToBlank.isEmpty()) {
            Blank blank = new Blank(staffID, "Interline", flightsToBlank);
            System.out.println(getManualTicketingBlank(staffID));
            Long blankID = getManualTicketingBlank(staffID);
            blank.setBlankID(blankID);
            System.out.println(blankID);
            blankArrayList.add(blank);

            blankinuseTextfield.setText(String.valueOf(blank.getBlankID()));

            if (blankID != 0){
                blank.setBlankID(blankID);
                flightsToBlank.clear();
                currentBlankTableView.refresh();
                blank.markBlankAsUsed(blank);
            }else{
//                noBlankAlert();
                System.out.println("No blanks avaliable ");
            }
        }

    }
    public void sell() throws SQLException {
        if(customer != null ) {

            System.out.println("sell");
//            customer.setCardNumber(cardNumberTextField.getText());
            Sale sale = new Sale(staffID, Application.getDate(), customer, "GBP", blankArrayList,salePayment);
            System.out.println(customer);
            sale.setSaleID(sale.selectMaxSaleID() + sale.getSaleID());
           for (Blank x:blankArrayList) {
               x.markBlankAsUsed(x);
           }
            sale.pushToDatabase();

            sale.pushSaleToSoldBlanks("");
//           sale.printSale();
            blankArrayList.clear();
            flightsToBlank.clear();
            populateCustomerTable();


            //int userID, int price,String date, int saleID, Customer customer, int commisionRate, int type, boolean latePayment, int discount//
        }

    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        flightsToBlank = new ArrayList<>();
        blankArrayList = new ArrayList<>();
        populateCustomerTable();
//        blockCardtext();
    }
}
