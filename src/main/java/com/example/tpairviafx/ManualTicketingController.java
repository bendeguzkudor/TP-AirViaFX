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

/** Controller class for the manual ticketing stage  */
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

    /**
     Populates the customer table with data from the database and sets up search and sorting functionality for the table.
     The data is obtained from the "customer" table in the database using a SQL query.
     The function also sets up listeners for the search field and sorting.
     */
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

    /** This method is called when clicking the select customer button , places the selected customer into an arraylist and notifies the user if the said
     * customer has discount available */
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

    /** Takes the payment method that was chosen in the radio buttons */
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
    /**
     Displays an alert to the user to confirm whether or not to apply a discount to a sale
     based on a fixed discount plan of the customer.
     If the user clicks on the Apply button, it prints "Applied" to the console.
     If the user clicks on the Discard button, it sets the customer's query discount to 0 and
     prints "Discarded" to the console.
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

     Adds a flight to the current blank table.
     @param flightModel the flight to be added to the table
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

     Adds a new flight to the flightsToBlank list using the information inputted by the user in the flight number, departure, arrival,
     date, time, and price text fields in the GUI. Then calls the addToBlankTable method to update the table view with the new flight.
     @throws SQLException if there is an error retrieving the flight from the database
     */
    public void addFlightsToFlights() throws SQLException {
            flightsToBlank.add(new FlightModel(Integer.parseInt(flightnumbertextfield.getText()),departuretextfield.getText(),arrivaltextfield.getText(),String.valueOf(datepicker.getValue()), timetextfield.getText(), Integer.parseInt(pricetextfield.getText())));
//            selectedFlightsList.get(0).retrieveFlightType();
            addToBlankTable(flightsToBlank.get(0));
        }

    /**

     Adds flights to a new blank ticket and updates the UI to display the new blank ticket ID.
     If there are no more available blanks, a message is displayed to the user.
     @throws SQLException if an error occurs while communicating with the database
     */
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
    /**
     Processes a sale if a customer and sale payment method have been selected. Adds all blanks in the current cart to an
     ArrayList of Blank objects and creates a new Sale object using the staff ID, current date, selected customer, selected
     sale payment method, and ArrayList of blanks. The sale ID is then set to the maximum sale ID retrieved from the database
     plus one, and the sale is pushed to the database. The method then pushes the sale to the "sold_blanks" table in the
     database, clears the blankArrayList and blanks List, refreshes the cartTable and populates the customerTable.
     @throws SQLException If there is an error accessing the database
     */
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



    /**Initialize method to populate the customer table and to instantiate the arraylists*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        flightsToBlank = new ArrayList<>();
        blankArrayList = new ArrayList<>();
        populateCustomerTable();
//        blockCardtext();
    }
}
