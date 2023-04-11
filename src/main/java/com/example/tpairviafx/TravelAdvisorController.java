package com.example.tpairviafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

public class TravelAdvisorController implements Initializable {
    private DateTimeFormatter dtf;

    private LocalDateTime now;

    private final double commisionRate = 0.1;
    private final double discount = 0.05;


    @FXML
    private Label nameLabel;
    private int staffID;
    private FXMLLoader fxmlLoader;
    @FXML
    private Label roleLabel;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button recordCustomerButton;
    @FXML
    private TextField departureTextField;
    @FXML
    private TextField arrivalTextField;
    @FXML
    private TextField cardNumberTextField;
    @FXML
    private TextField searchCustomerField;
    @FXML
    private Button closeButton;
    private String departure;
    private String arrival;
    private LocalDate date;
    private String dateString;
    private Scene scene;
    @FXML
    private TableView flightsTableView;
    @FXML
    private TableView customerTableView;

    @FXML
    private TableView currentBlankTableView;
    @FXML
    private TableView cartTable;


    private ResultSet rs;
    //Columns for customer table

    @FXML
    private TableColumn<Customer, Integer> customerIDColumn;
    @FXML
    private TableColumn<Customer, String> customerFirstameColumn;
    @FXML
    private TableColumn<Customer, String> customerLastnameColumn;


    //Columns for cart
    @FXML
    private TableColumn<Blank, Integer> cartBlankIDColumn;
//    private TableColumn<Blank, Integer> cartDeparture;
//    private TableColumn<Blank, Integer> cartArrival;


    // Columns for table that list the current items on the current blank
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
    private RadioButton cardRadioButton, cashRadioButton;
    @FXML
    private ChoiceBox currencyChoicebox;



    //Flights tables columns
    @FXML
    private TableColumn<FlightModel, Integer> flightNumberColumn;
    @FXML
    private TableColumn<FlightModel,String > departureColumn;
    @FXML
    private TableColumn<FlightModel, String> arrivalColumn;
    @FXML
    private TableColumn<FlightModel, String> dateColumn;
    @FXML
    private TableColumn<FlightModel, String> timeColumn;
    @FXML
    private TableColumn<FlightModel, String> priceColumn;

    private ObservableList<FlightModel> selectedFlightsList;
    private ObservableList<Customer> selectedCustomerList;
//    private ObservableList<FlightModel> flightsOnBlankList;


    private String salePayment;

//    private Blank blank;
    private ArrayList<FlightModel> flightsToBlank;
    private ArrayList<Blank> blankArrayList;

    ObservableList<FlightModel> flightModelObservableList = FXCollections.observableArrayList();

    ObservableList<FlightModel> flightsOnBlankList = FXCollections.observableArrayList();
    ObservableList<Customer> customerObservableList = FXCollections.observableArrayList();
    ObservableList<Blank> blanks = FXCollections.observableArrayList();

    private Customer customer;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Stage stage;
    private String currencyChosen;




//

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
//        selectMaxSaleID();
        currentBlankTableView.setPlaceholder(new Label("No flights selected"));
        initializeDate();
//        cart = new Cart();
        flightsToBlank = new ArrayList<>();
        blankArrayList = new ArrayList<>();
        DBConnect db = new DBConnect();
        populateFlightsTable();
        populateCustomerTable();
        currencyChoicebox.getItems().addAll(new Currency().getCurrencies());
//        currencyChoicebox.setOnAction(this::getCurrencyChosen);
    }


    public void getCurrencyChosen(ActionEvent e){
        currencyChosen = (String) currencyChoicebox.getValue();
    }
    public void getPayment(){
        if (cashRadioButton.isSelected()){
            salePayment = "Cash";
        }else {
            salePayment = "Card";
        }
    }
    public void logOut() throws IOException {
        Application.logOut(stage);
    }
    public void recordNewCustomer() throws IOException {
        Stage stage = new Stage();
        fxmlLoader = new FXMLLoader(Application.class.getResource("CustomerRegisterForm.fxml"));
        scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setScene(scene);
        CustomerRegisterFormController customerRegisterFormController = new CustomerRegisterFormController();
        customerRegisterFormController.setPreviousScene(this.scene);
        stage.show();
    }
    public void populateFlightsTable(){
        DBConnect db = new DBConnect();
//        ResultSet rs;
        String sql = "SELECT flighNumber, departure, arrival,date,time,price FROM flights"; //query for dynamic search
//        String sql2 = "SELECT * FROM flights WHERE departure = \"London\" AND arrival = \"budapest\" AND date = \"23-05-10\"";
        try{
            db.connect();
            rs = db.executeQuery(sql);
            while(rs.next()){
                System.out.println(rs.getInt("flighNumber"));
                Integer queryflightNumber = rs.getInt("flighNumber");
                String querydeparture = rs.getString("departure");
                String queryarrival = rs.getString("arrival");
                String querydate = rs.getString("date");
                String querytime = rs.getString("time");
                Integer queryprice = rs.getInt("price");

                //Populate the list
                flightModelObservableList.add(new FlightModel(queryflightNumber,querydeparture,
                        queryarrival,querydate,querytime,queryprice));
            }

            flightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
            departureColumn.setCellValueFactory(new PropertyValueFactory<>("departure"));
            arrivalColumn.setCellValueFactory(new PropertyValueFactory<>("arrival"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

            flightsTableView .setItems(flightModelObservableList);

            FilteredList<FlightModel> filteredData = new FilteredList<>(flightModelObservableList, b->true);
            departureTextField.textProperty().addListener((observable, oldValue, newValue)->{
                filteredData.setPredicate(flightSearchModel -> {
                    if(newValue.isEmpty() || newValue.isBlank() || newValue == null){
                        return true;
                    }
                    String searchkeyword = newValue.toLowerCase();
                    if(flightSearchModel.getDeparture().toLowerCase().indexOf(searchkeyword) > -1){
                        return true;

                    }else return flightSearchModel.getArrival().toLowerCase().indexOf(searchkeyword) > -1;
                });
            });
            SortedList<FlightModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(flightsTableView.comparatorProperty());
            flightsTableView.setItems(sortedData);
            selectedFlightsList = flightsTableView.getSelectionModel().getSelectedItems();
            System.out.println("selected flight");

        } catch (SQLException e){
            throw new RuntimeException(e);
        }finally {
            db.closeConnection();
        }

    }
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
//            selectedCustomerList = customerTableView.getSelectionModel().getSelectedItems();
//            System.out.println("selected customer");

        } catch (SQLException e){
            throw new RuntimeException(e);
        }finally {
            db.closeConnection();
        }
    }




    //method to test blanks;
    public void printBlanks() throws SQLException {
        DBConnect db = new DBConnect();
        try {
            db.connect();
            String sql = "SELECT * FROM blanks WHERE staffID = '" + staffID + "'";
            rs = db.executeQuery(sql);
            while (rs.next()) {
                System.out.println(rs.getLong("blankID"));
                System.out.println(rs.getInt("staffID"));
                System.out.println(rs.getString("dateAssigned"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            rs.close();
            db.closeConnection();
        }

    }
    public void selectCustomer(){
        selectedCustomerList = customerTableView.getSelectionModel().getSelectedItems();
        customer = selectedCustomerList.get(0);
        if (customer.getQueryDiscount() != 0){
            discountAlert();

        }

        System.out.println(customer.getCustomerID());
    }

    public void closeWindow(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    public void initializeDate(){
        dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        now = LocalDateTime.now();
        dateString = now.toString();

    }

    public void addToCart() throws SQLException {
//        cart.addFlightToCart(selectedFlightsList.get(0));
        for(Blank x: blanks){
            System.out.println(x);
        }
    }
    public void addToBlank() throws SQLException {
//        blanks.add(new Blank())
        if(!flightsToBlank.isEmpty()) {
            Blank blank1 = new Blank(staffID, flightsToBlank.get(0).getFlightType(), flightsToBlank);
            if (blank1.retrieveBlankID(blank1.getBlankType()) != 0){
                blank1.setBlankID(blank1.retrieveBlankID(blank1.getBlankType()));
                blank1.retrieveBlankID(blank1.getBlankType());
                blank1.printBlankDetails();
                flightsToBlank.clear();
                blanks.add(blank1);
                flightsOnBlankList.clear();
                currentBlankTableView.refresh();
                addBlankToCartTable(blank1);
                blank1.markBlankAsUsed(blank1);
            }else{
                noBlankAlert();
                System.out.println("No blanks avaliable ");
            }

        }

    }
    public void addFlightsToFlights() throws SQLException {
        if(!selectedFlightsList.isEmpty()){
        flightsToBlank.add(selectedFlightsList.get(0));
        selectedFlightsList.get(0).retrieveFlightType();

        addToBlankTable(selectedFlightsList.get(0));
        }
    }

    public void sell() throws SQLException {
       if(customer != null && salePayment != null) {
           for (Blank x : blanks) {
               System.out.println(x);
               blankArrayList.add(x);
           }
//        Customer customer = new Customer("bedi", 88);
           customer.setCardNumber(cardNumberTextField.getText());
           Sale sale = new Sale(staffID, Application.getDate(), customer, currencyChoicebox.getValue().toString(), blankArrayList,salePayment);
           System.out.println(customer);
           System.out.println(blankArrayList.get(0).getBlankType());
           System.out.println(currencyChoicebox.getValue().toString());
           sale.setSaleID(sale.selectMaxSaleID() + sale.getSaleID());
//           for (Blank x:blankArrayList) {
//               x.markBlankAsUsed(x);
//           }
           sale.pushToDatabase();

           sale.pushSaleToSoldBlanks();
//           sale.printSale();
           blankArrayList.clear();
           blanks.clear();
           cartTable.refresh();
           populateCustomerTable();


           //int userID, int price,String date, int saleID, Customer customer, int commisionRate, int type, boolean latePayment, int discount//
       }

    }

    public void printCart(){
//        blank.printFlights();
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
    public void addBlankToCartTable(Blank blank){
        cartBlankIDColumn.setCellValueFactory(new PropertyValueFactory<>("blankID"));
        cartTable.setItems(blanks);

    }
    public void searchFlight() throws SQLException {
        departure = departureTextField.getText();
        arrival = arrivalTextField.getText();
        date = datePicker.getValue();
        flightsTableView.getItems().clear();
        DBConnect db = new DBConnect();
//        ResultSet rs;
        String sql = "SELECT flighNumber, departure, arrival,date,time,price FROM flights"; //query for dynamic search
        String sql2 = "SELECT * FROM flights WHERE departure = '" + departure + "' AND  arrival = '"+arrival+"' AND date = '"+ date +"' ";
        Integer queryflightNumber = null;
        String querydeparture = null;
        String queryarrival = null;
        String querydate = null;
        String querytime = null;
        Integer queryprice = null;
        try {
            db.connect();
            System.out.println(sql2);
            rs = db.executeQuery(sql2);
            while (rs.next()) {
                System.out.println(rs.getInt("flighNumber"));
                queryflightNumber = rs.getInt("flighNumber");
                querydeparture = rs.getString("departure");
                queryarrival = rs.getString("arrival");
                querydate = rs.getString("date");
                querytime = rs.getString("time");
                queryprice = rs.getInt("price");

                //Populate the list
                flightModelObservableList.add(new FlightModel(queryflightNumber, querydeparture,
                        queryarrival, querydate, querytime, queryprice));
            }

            flightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
            departureColumn.setCellValueFactory(new PropertyValueFactory<>("departure"));
            arrivalColumn.setCellValueFactory(new PropertyValueFactory<>("arrival"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

            flightsTableView.setItems(flightModelObservableList);
            selectedFlightsList = flightsTableView.getSelectionModel().getSelectedItems();



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            rs.close();
            db.closeConnection();
        }
        System.out.println("sadv" + "  " + date);
        System.out.println(queryflightNumber + querydeparture + queryarrival + querydate + querytime + queryprice);

    }


    public void displayNameAndRole(String name, int role){

        switch (role){
            case 1:
                roleLabel.setText("Travel Advisor");
                break;
            case 2:
                roleLabel.setText("Office Manager");
                break;
            case 3:
                roleLabel.setText("Admin");
                break;

        }
        nameLabel.setText(name);
    }
    public void setStaffID(int staffID){
        this.staffID = staffID;

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
    public void noBlankAlert(){
        Alert discountAlert = new Alert(Alert.AlertType.CONFIRMATION);
        discountAlert.setTitle("NO BLANKS LEFT");
        discountAlert.setHeaderText("OUT OF BLANKS");
        discountAlert.setContentText("There are no more blanks left that would fit this ticketing ");
        ButtonType okbutton = new ButtonType("OK");

        discountAlert.getButtonTypes().setAll(okbutton);
        Optional<ButtonType> result = discountAlert.showAndWait();
        if (discountAlert.getResult() == okbutton){
            System.out.println("OK");
        }
    }

}


