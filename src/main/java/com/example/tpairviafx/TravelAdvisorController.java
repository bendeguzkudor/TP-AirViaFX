package com.example.tpairviafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class TravelAdvisorController implements Initializable {
    private DateTimeFormatter dtf;

    private LocalDateTime now;

    private double commisionRate = 0.1;
    private double discount = 0.05;


    @FXML
    private Label nameLabel;
    private int staffID;
    @FXML
    private Label roleLabel;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField departureTextField;
    @FXML
    private TextField arrivalTextField;
    @FXML
    private Button closeButton;
    private String departure;
    private String arrival;
    private LocalDate date;
    @FXML
    private TableView flightsTableView;

    private ResultSet rs;


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
    private Cart cart;
    private ArrayList<Blank> blanks;
//    private Blank blank;
    private ArrayList<FlightModel> flightsToBlank;

    ObservableList<FlightModel> flightModelObservableList = FXCollections.observableArrayList();


//

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        initializeDate();
        cart = new Cart();
        flightsToBlank = new ArrayList<>();
        blanks = new ArrayList<>();
        DBConnect db = new DBConnect();
//        ResultSet rs;
        String sql = "SELECT flighNumber, departure, arrival,date,time,price FROM flights"; //query for dynamic search
        String sql2 = "SELECT * FROM flights WHERE departure = \"London\" AND arrival = \"budapest\" AND date = \"23-05-10\"";
        try{
            db.connect();
            rs = db.statement.executeQuery(sql);
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

                    }else if(flightSearchModel.getArrival().toLowerCase().indexOf(searchkeyword) > -1){
                        return true;

                    }else{
                        return false;
                    }

                });

            });
            SortedList<FlightModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(flightsTableView.comparatorProperty());
            flightsTableView.setItems(sortedData);
            selectedFlightsList = flightsTableView.getSelectionModel().getSelectedItems();

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
            db.connect();;
            String sql = "SELECT * FROM blanks WHERE staffID = '" + staffID + "'";
            rs = db.statement.executeQuery(sql);
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


    public void getDate(){
        LocalDate date = datePicker.getValue();
        System.out.println(date);
    }
    public void closeWindow(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    public void initializeDate(){
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        now = LocalDateTime.now();
        System.out.println(dtf.format(now));

    }

    public void addToCart() throws SQLException {
//        cart.addFlightToCart(selectedFlightsList.get(0));
        for(Blank x: blanks){
            System.out.println(x);
        }
    }
    public void addToBlank() throws SQLException {
//        blanks.add(new Blank())
        Blank blank1 = new Blank(staffID, flightsToBlank.get(0).getFlightType(), 200, 15,
                10, 1, 0.09, "bedi");

        for(FlightModel x: flightsToBlank){
            blank1.addFlightToBlank(x);

        }

        blank1.printBlankDetails();
        flightsToBlank.clear();
        blanks.add(blank1);



    }
    public void addFlightsToFlights() throws SQLException {
        flightsToBlank.add(selectedFlightsList.get(0));
        selectedFlightsList.get(0).retrieveFlightType();


    }
    public void addBlankToCart(Blank blank) throws SQLException {
        blank.setBlankID(blank.getFlights().get(0).getFlightType());
        blank.setBlankType(blank.getFlights().get(0).getFlightType());



    }
    public void sell() throws SQLException {
        Random rand = new Random();
        int saleID = rand.nextInt(100,500);
        String date = now.toString();
        int price = cart.sumCart();
        Customer customer = new Customer("bedi", 88);

//        Blank blank = new Blank();

        Sale sale = new Sale(staffID, price, date, saleID, customer, commisionRate, 0, true, discount,0 );
        System.out.println(sale);
        sale.printSale();
        //int userID, int price,String date, int saleID, Customer customer, int commisionRate, int type, boolean latePayment, int discount//


    }
    public void printCart(){
//        blank.printFlights();
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
            rs = db.statement.executeQuery(sql2);
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

}

