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
import java.sql.SQLOutput;
import java.sql.Statement;
import java.text.ParseException;
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

/** Controller class for the Travel Advisor stage*/

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
    private TextField otherTextfield;
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
    @FXML
    private TableColumn<Blank, Double> priceTableColumn;

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
    private RadioButton cardRadioButton, cashRadioButton, extraluggageRadiobutton,otherRadiobutton;

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

    private ManualTicketingController manualTicketingController;
    private ManageSalesController manageSaleController;


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
    private String descruptionChosen;

    @FXML
    private ChoiceBox<String> typeChoiceBox;

    @FXML
    private ChoiceBox<String> reportTypeChoicebox;

    @FXML
    private DatePicker dateFromDatePicker;
    @FXML
    private DatePicker dateToDatePicker;
    private String currentDate;
    private String reportTypeChosen;




//

/**

 Initializes the controller class. Populates the tables, sets the event handlers, and instantiates the arrays*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
//        selectMaxSaleID();
        currentBlankTableView.setPlaceholder(new Label("No flights selected"));
        reportTypeChoicebox.getItems().addAll("Interline","Domestic");
        reportTypeChoicebox.setOnAction(this::getTypeChosen);
        initializeDate();
//        cart = new Cart();
        flightsToBlank = new ArrayList<>();
        blankArrayList = new ArrayList<>();
        DBConnect db = new DBConnect();
        populateFlightsTable();
        populateCustomerTable();
//        currencyChoicebox.getItems().addAll(new Currency().getCurrencies());
        currentDate = Application.getDate();
//        currencyChoicebox.setOnAction(this::getCurrencyChosen);
    }

    private void getTypeChosen(ActionEvent actionEvent) {
        reportTypeChosen = reportTypeChoicebox.getValue();

    }

    /**
     Generates a sales report based on the selected report type (Interline or Domestic) and date range (from dateFromDatePicker to dateToDatePicker).
     If all required fields are selected, a Report object is created and the appropriate method is called based on the report type.
     If not all required fields are selected, no report is generated.
     @throws ParseException if there is an error parsing the dates
     */
    public void generateReport() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateFrom="";
        String dateTo="";
        if (!reportTypeChoicebox.getSelectionModel().isEmpty() && dateFromDatePicker.getValue() != null && dateToDatePicker.getValue() != null){
            dateFrom = (dateFromDatePicker.getValue().format(dtf));
            dateTo = (dateToDatePicker.getValue().format(dtf));
            System.out.println(dateFrom+ " -  " + dateTo);
                Report report = new Report(dateFrom,dateTo,staffID);
                switch(reportTypeChoicebox.getValue()){
                    case "Interline":
                        report.individualInterlineSalesReport();
                        break;
                    case "Domestic":
                        report.domesticIndividualReport();
                    break;

                }
                // global

            }
        }



    public void getCurrencyChosen(ActionEvent e){
        currencyChosen = (String) currencyChoicebox.getValue();
    }

    /** Gets the payment type selected*/
    public void getPayment(){
        if (cashRadioButton.isSelected()){
            salePayment = "Cash";
            cardNumberTextField.setDisable(true);
            cardNumberTextField.clear();
        }else {
            salePayment = "Card";
            cardNumberTextField.setDisable(false);
        }
    }
    /** Gets the MCO type selected*/
    public void getMCO(){
        if (extraluggageRadiobutton.isSelected()){
            descruptionChosen = "Extra Luggage";
            otherTextfield.setDisable(true);
            System.out.println(descruptionChosen);
        }else {
            otherTextfield.setDisable(false);
            descruptionChosen = otherTextfield.getText();
            System.out.println(descruptionChosen);
        }
    }
    /** Logs the user out */
    public void logOut() throws IOException {
        Application.logOut(stage);
    }

    /**
     * Opens a new stage for recording a new customer and sets the scene
     * @throws IOException If the CustomerRegisterForm.fxml file is not found or cannot be loaded.
     */
    public void recordNewCustomer() throws IOException {
        Stage stage = new Stage();
        fxmlLoader = new FXMLLoader(Application.class.getResource("CustomerRegisterForm.fxml"));
        scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setScene(scene);
        CustomerRegisterFormController customerRegisterFormController = new CustomerRegisterFormController();
        customerRegisterFormController.setPreviousScene(this.scene);
        stage.show();
    }
    /**
     * Opens a new stage for manual ticketing.
     * @throws IOException If the ManualTicketing.fxml file is not found or cannot be loaded.
     */
    public void manualTicketing() throws IOException {
        Stage stage = new Stage();
        fxmlLoader = new FXMLLoader(Application.class.getResource("ManualTicketing.fxml"));
        scene = new Scene(fxmlLoader.load(), 800, 700);
        stage.setScene(scene);
        manualTicketingController = fxmlLoader.getController();
        manualTicketingController.setStaffID(this.staffID);
        manualTicketingController.setPreviousScene(this.scene);
        System.out.println(staffID);
        stage.show();
    }
    /**

     This method populates flight table view with flight data from the database, and implements search functionality based on the
     departure and arrival. The method also sets up the cell value factories for each column in the table view,
     and sorts the data based on the selected column.
     @throws RuntimeException if a SQL exception is thrown while executing the database query
     */
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
    /**

     This method populates customer table view with customer data from a database, and implements search functionality customer name
     . The method also sets up the cell value factories for each column in the table view,
     and sorts the data based on the selected column.
     @throws RuntimeException if a SQL exception is thrown while executing the database query
     */
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
    /** Gets the selected customer, prompts the user if said customer has discount */
    public void selectCustomer(){
        selectedCustomerList = customerTableView.getSelectionModel().getSelectedItems();
        customer = selectedCustomerList.get(0);
        if (customer.getQueryDiscount() != 0){
            discountAlert();
        }
        System.out.println(customer.getFirstName()+ " is selected");
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
    /**
     This method creates a new Blank object with the staff ID, flight type and flights in the flightsToBlank list,
     and adds it to the blanks list. It also refreshes the currentBlankTableView and adds the new Blank to the cart table.
     If no blank is available, it displays an alert and does not add the blank.
     @throws SQLException if an SQL exception occurs while retrieving the blank ID from the database
     */
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

    /**
     This method creates a new Blank object with the staff ID, destination "MCO", blank type, and luggage description entered
     by the user. It then adds the Blank to the blanks list and refreshes the currentBlankTableView and the cart table.
     If no blank is available, it displays an alert and does not add the blank.
     @throws SQLException if an SQL exception occurs while retrieving the blank ID from the database
     */
    public void addExtraLuggage() throws SQLException {
        if (otherRadiobutton.isSelected()){
            descruptionChosen = otherTextfield.getText();
        }
        System.out.println(blanks.get(0).getBlankID());
//        blanks.add(new Blank())

        if(!blanks.isEmpty() && descruptionChosen!= "") {
            Blank blank1 = new Blank(0L, staffID, "MCO", blanks.get(0).getBlankType(),descruptionChosen);
            if (blank1.retrieveBlankID(blank1.getBlankType()) != 0){
                blank1.setBlankID(blank1.retrieveBlankID(blank1.getBlankType()));
                blank1.retrieveBlankID(blank1.getBlankType());
                blanks.add(blank1);
                currentBlankTableView.refresh();
                addBlankToCartTable(blank1);
                blank1.markBlankAsUsed(blank1);
            }else{
                noBlankAlert();
                System.out.println("No blanks avaliable ");
            }

        }

    }
    /**

     This method adds the FlightModel object from the selectedFlightsList to the flightsToBlank list.
     It then retrieves the flight type of the selected flight and adds it to the add to blank table.
     */
    public void addFlightsToFlights() throws SQLException {
        if(!selectedFlightsList.isEmpty()){
        flightsToBlank.add(selectedFlightsList.get(0));
        selectedFlightsList.get(0).retrieveFlightType();

        addToBlankTable(selectedFlightsList.get(0));
        }
    }

    /**
     Processes a sale if a customer and sale payment method have been selected. Adds all blanks in the current cart to an
     ArrayList of Blank objects and creates a new Sale object using the staff ID, current date, selected customer, selected
     sale payment method, and ArrayList of blanks. The sale ID is then set to the maximum sale ID retrieved from the database
     plus one, and the sale is pushed to the database. The method then pushes the sale to the "sold_blanks" table in the
     database, clears the blankArrayList and blanks List, refreshes the cartTable and populates the customerTable.
     @throws SQLException If there is an error accessing the database
     */
    public void sell() throws SQLException {
       if(customer != null && salePayment != null) {
           for (Blank x : blanks) {
               System.out.println(x);
               blankArrayList.add(x);
           }
//        Customer customer = new Customer("bedi", 88);
           customer.setCardNumber(cardNumberTextField.getText());
           Sale sale = new Sale(staffID, Application.getDate(), customer, "GBP", blankArrayList,salePayment);
           System.out.println(customer);
           System.out.println(blankArrayList.get(0).getBlankType());
           sale.setSaleID(sale.selectMaxSaleID() + sale.getSaleID());
//           for (Blank x:blankArrayList) {
//               x.markBlankAsUsed(x);
//           }
           sale.pushToDatabase();

           sale.pushSaleToSoldBlanks(descruptionChosen);
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
    /**

     Adds a flight to the table view for the current blank.
     @param flightModel The flight to be added to the table view.
     */
    public void addToBlankTable(FlightModel flightModel){

        flightsOnBlankList.add(flightModel);
        blankflightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
        blankdepartureColumn.setCellValueFactory(new PropertyValueFactory<>("departure"));
        blankarrivalColumn.setCellValueFactory(new PropertyValueFactory<>("arrival"));
        blankdateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        blankpriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        currentBlankTableView.setItems(flightsOnBlankList);

    }
    /**

     Adds a Blank object to the cart table view.
     @param blank the Blank object to be added to the cart table view
     */
    public void addBlankToCartTable(Blank blank){
        cartBlankIDColumn.setCellValueFactory(new PropertyValueFactory<>("blankID"));
        priceTableColumn.setCellValueFactory(new PropertyValueFactory<>("priceGBP"));
        cartTable.setItems(blanks);

    }
    /**

     This method is responsible for searching flights based on the user input of departure, arrival, and date. It retrieves
     the flights from the database and populates a TableView with the matching results. The method also stores the selected
     flights in a list for further use.
     @throws SQLException if an error occurs while executing the SQL query
     */
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

    /**

     Displays the name and role of the user.
     @param name the name of the user
     @param role the role of the user: 1 for Travel Advisor, 2 for Office Manager, 3 for Admin
     */
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
    /**

     Displays an alert to inform the user that a discount is applicable for the current customer, and prompts the user
     to apply or discard the discount for the current sale.
     */
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
    /**

     Displays an alert when there are no more blanks left for ticketing.
     Prompts the user with a confirmation dialog box with the message "There are no more blanks left that would fit this ticketing.
     */
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
    /**

     This method is responsible for opening the Manage Sales window. It loads the ManageSales.fxml file using an FXMLLoader
     and sets the scene to the loaded fxml. It also sets the staffID and previousScene variables of the ManageSalesController.
     The staffID is set to the current staff member's ID and the previousScene is set to the current scene. This method
     is called when the "Manage Sales" button is clicked in the main window.
     @throws IOException if there is an error loading the ManageSales.fxml file
     */

    public void manageSales() throws IOException {
        Stage stage = new Stage();
        fxmlLoader = new FXMLLoader(Application.class.getResource("ManageSales.fxml"));
        scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setScene(scene);
        manageSaleController = fxmlLoader.getController();
        manageSaleController.setStaffID(this.staffID);
        manageSaleController.setPreviousScene(this.scene);
        System.out.println(manageSaleController.getStaffID());

        stage.show();

    }

}


