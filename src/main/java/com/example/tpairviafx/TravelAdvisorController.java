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
import java.util.Random;
import java.util.ResourceBundle;

public class TravelAdvisorController implements Initializable {
    private DateTimeFormatter dtf;
    private LocalDateTime now;

    private double commisionRate = 0.1;
    private double discount = 0.05;


    @FXML
    private Label nameLabel;
    private int userID;
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

    ObservableList<FlightModel> flightModelObservableList = FXCollections.observableArrayList();


//

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        initializeDate();
        cart = new Cart();
        DBConnect db = new DBConnect();
        ResultSet rs;
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

    public void addToCart(){
        cart.addFlightToCart(selectedFlightsList.get(0));
    }
    public void sell(){
        Random rand = new Random();
        int saleID = rand.nextInt(100,500);
        String date = now.toString();
        int price = cart.sumCart();
        Customer customer = new Customer("bedi", 88);

        Sale sale = new Sale(userID, price, date, saleID, customer, commisionRate, 0, true, discount );
        System.out.println(sale);
        sale.printSale();
        //int userID, int price,String date, int saleID, Customer customer, int commisionRate, int type, boolean latePayment, int discount//


    }
    public void printCart(){
        cart.printCart();
    }
    public void searchFlight() {
        departure = departureTextField.getText();
        arrival = arrivalTextField.getText();
        date = datePicker.getValue();
        DBConnect db = new DBConnect();
        ResultSet rs;
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
    public void setUserID(int userID){
        this.userID = userID;

    }

}

