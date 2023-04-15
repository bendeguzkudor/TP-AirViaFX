package com.example.tpairviafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.poi.xwpf.usermodel.Document;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;





/**Controller class to manage Office Manager stage   */


public class OfficeManagerController implements Initializable {
    ObservableList<Staff> staffObservableList = FXCollections.observableArrayList();

    @FXML
    private TableView blanksTableView;
    @FXML
    private TextField customerSearchTextField;
    @FXML
    private TextField searchBlankIDTextField;
    ObservableList<Blank> blankObservableList = FXCollections.observableArrayList();
    private ObservableList<Blank> selectedBlanksList;
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
    private DatePicker dateFromDatePicker;
    @FXML
    private DatePicker dateToDatePicker;
    private FXMLLoader fxmlLoader;
    private Scene scene;

    @FXML
    private ChoiceBox<String> blankChoiceBox;
    @FXML
    private ChoiceBox<String> typeChoiceBox;
    @FXML
    private ChoiceBox<String> advisorChoiceBox;
    @FXML
    private ChoiceBox<String> reAssignTochoiceBox;
    private String reassignto;
    private final String [] blankTypes = {"444", "440","420","201","101","451","452"};

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

    //////////////////////////////////
    @FXML
    private TableColumn<SaleForm, Integer> customercolumn1;

    @FXML
    private TableColumn<SaleForm, String> firstnamecolumn1;

    @FXML
    private TableColumn<SaleForm, String> lastnamecolumn1;

    @FXML
    private TableColumn<SaleForm, Integer> pricecolumn1;

    @FXML
    private TableColumn<SaleForm, Integer> saleIDColumn1;

    @FXML
    private TableColumn<SaleForm, Integer> staffIDColumn1;

    ObservableList<SaleForm> saleObservableList = FXCollections.observableArrayList();
    private ObservableList<SaleForm> selectedSaleList;
    @FXML
    private TableView saleTableView;
    @FXML
    private TextField searchTextfield;

    /////////////////////////////////


    private int staffID;
    private String chosenBlank;
    private String sqlforMinBlank;
    private final String date = Application.getDate();
    private int amount;
    private  Integer [] advisors;
    private final String[] reportTypes = { "Stock Turnover", "Domestic", "Interline" };
    private String chosenAdvisor;
    private String typeChosen;


    /**
     Initializes the controller by populating the various ChoiceBox and TableView objects with appropriate data.
     This method first calls the populateStaffTable() method to populate the staffTableView object with staff member data.
     It then calls the populateBlankTable() method to populate the blankTableView object with ticket blank data.
     The report types are added to the typeChoiceBox object via the addAll() method.
     When the user selects a report type from the typeChoiceBox, the getTypeChosen() method is called.
     The staff members are added to the advisorChoiceBox and reAssignTochoiceBox objects via the addStaffToChoiceBox() method.
     When the user selects a staff member from the advisorChoiceBox, the getAdvisorChosen() method is called.
     When the user selects a staff member from the reAssignTochoiceBox, the setReassignToChosen() method is called.
     The blank types are added to the blankChoiceBox object via the addAll() method.
     When the user selects a blank type from the blankChoiceBox, the getblankChosen() method is called.
     Finally, the populateSaleTable() method is called to populate the saleTableView object with sales data.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        populateStaffTable();
        populateBlankTable();
        typeChoiceBox.getItems().addAll(reportTypes);
        typeChoiceBox.setOnAction(this::getTypeChosen);

        addStaffToChoiceBox();
        advisorChoiceBox.setOnAction(this::getAdvisorChosen);
        reAssignTochoiceBox.setOnAction(this::setReassignToChosen);

        blankChoiceBox.getItems().addAll(blankTypes);
        blankChoiceBox.setOnAction(this::getblankChosen);
        populateSaleTable();

    }

    /**
     Adds staff members to the advisorChoiceBox and reAssignTochoiceBox ChoiceBox objects.
     This method loops through the staffObservableList and adds each staff member's ID to the advisorChoiceBox and reAssignTochoiceBox ChoiceBox objects as a string.
     If the staffObservableList is empty, this method does not add any items to the ChoiceBox objects.
     */
    public void addStaffToChoiceBox(){
        for (Staff x : staffObservableList){
            advisorChoiceBox.getItems().add(String.valueOf(x.getStaffID()));
            reAssignTochoiceBox.getItems().add(String.valueOf(x.getStaffID()));
        }
    }
    /** Generates a report based on the selected criteria. This method first checks that both the "from" and "to" dates have been selected.
     If the criteria includes generating a stock turnover report, it creates an instance of the Report class and calls the createTicketStockTurnOverReport() method.
     If the criteria is for a global interline or domestic sales report, it creates an instance of the Report class with a staffID of 0 and calls the appropriate global report method.
     If the criteria is for an individual interline or domestic sales report, it creates an instance of the Report class with a staffID corresponding to the selected advisor and calls the appropriate individual report method.
     If any required criteria is missing, this method does not generate a report and simply prints a message to the console.
     @throws ParseException if there is an error parsing the dates from the date picker
     */
    public void generateReport() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateFrom="";
        String dateTo="";

        if (dateFromDatePicker.getValue() != null && dateToDatePicker.getValue() != null){
             dateFrom = (dateFromDatePicker.getValue().format(dtf));
             dateTo = (dateToDatePicker.getValue().format(dtf));
            System.out.println(dateFrom+ " -  " + dateTo);
            if (typeChosen.equals("Stock Turnover")){
                Report report = new Report(dateFrom,dateTo,staffID);
                report.createTicketStockTurnOverReport();
            }
            if (advisorChoiceBox.getSelectionModel().isEmpty()){
                System.out.println("empty");
                // global
                if (typeChosen.equals("Interline")){
                    Report report = new Report(dateFrom, dateTo, 0);
                    report.globalInterLineSalesReport();
                } else if (typeChosen.equals("Domestic")) {
                    Report report = new Report(dateFrom, dateTo, 0);
                    report.globalDomesticSalesReport();
                }
            }if(!advisorChoiceBox.getSelectionModel().isEmpty() && !typeChoiceBox.getSelectionModel().isEmpty()){
                if(typeChosen.equals("Domestic")){
                    Report report = new Report(dateFrom, dateTo, Integer.parseInt(chosenAdvisor));
                    report.domesticIndividualReport();
                } else if (typeChosen.equals("Interline")) {
                    Report report = new Report(dateFrom, dateTo, Integer.parseInt(chosenAdvisor));
                    report.individualInterlineSalesReport();
                }

            }
        }

    }

    private void getTypeChosen(ActionEvent actionEvent) {typeChosen = typeChoiceBox.getValue();}
    public void getblankChosen(ActionEvent e){
        chosenBlank = blankChoiceBox.getValue();
    }
    public void getAdvisorChosen(ActionEvent e){
        chosenAdvisor = advisorChoiceBox.getValue();
    }
    public void setReassignToChosen(ActionEvent e){
        reassignto = reAssignTochoiceBox.getValue();
        System.out.println(reassignto);
    }

    /**
     Populates the staff table view with data retrieved from the database.
     Uses a query to retrieve staff with a role of 1 (advisors).
     Populates a staffObservableList with staff objects and sets the staffTableView items to this list.
     Allows for filtering and sorting of the table view by name using searchTextField.
     */
    public void populateStaffTable(){
        System.out.println("sadgf");
        DBConnect db = new DBConnect();
//        ResultSet rs;
        String sql = "SELECT name, staffID FROM staff where role = 1"; //query for dynamic search
//        String sql2 = "SELECT * FROM flights WHERE departure = \"London\" AND arrival = \"budapest\" AND date = \"23-05-10\"";
        try{
            db.connect();
            rs = db.executeQuery(sql);
            int counter =0;
            while(rs.next()){
                queryStaffID = rs.getInt("staffID");
                String queryName = rs.getString("name");
                System.out.println(queryName);
                System.out.println(queryStaffID);
                //Populate the list
                staffObservableList.add(new Staff(queryName,queryStaffID));
                counter++;

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
    /**

     Reassigns a blank ticket to a new staff member.
     Gets the ID of the selected blank from selectedBlanksList.
     Constructs a SQL update statement with the new staff member's ID (reassignto) and the selected blank ID.
     Executes the statement and updates the database.
     Calls populateBlankTable() to refresh the table view.
     Throws an SQLException if there is an error executing the SQL statement.
     */
    public void reAssign() throws SQLException {
        Long selectedBlankID = selectedBlanksList.get(0).getBlankID();

        String sql = "update  blanks set staffID = "+reassignto+" where blankID = "+selectedBlankID+" and sold != 1;";
        DBConnect db = new DBConnect();
        db.connect();
        PreparedStatement preparedStatement = db.getConnection().prepareStatement(sql);

        int update = preparedStatement.executeUpdate();
        System.out.println(update);
        populateBlankTable();


    }
    /**
     Assigns a range of blanks to a selected staff member and sets the date of assignment to the current date.
     @throws SQLException if there is an error executing the SQL statement.
     */


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
                PreparedStatement stmt = db.getConnection().prepareStatement(sql);

                // Set the values of the parameters in the prepared statement
                stmt.setLong(1, rangeFrom);
                stmt.setLong(2, rangeTo);
                System.out.println(sql);
                stmt.executeUpdate();
                System.out.println("ran");

        }catch (SQLException e){
            e.printStackTrace();}

    }
    /**
     This method retrieves the minimum blank ID available for the chosen blank type and with no staff assigned to it.
     It uses a switch statement to determine the appropriate SQL statement based on the chosen blank type.
     It then executes the SQL statement and retrieves the minimum blank ID from the result set.
     Finally, it returns the minimum blank ID.
     @return the minimum blank ID available for the chosen blank type and with no staff assigned to it
     */
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
            case "451":
                sqlforMinBlank = "select min(blankID) from blanks where SUBSTR(blankID, 1, 3) = '451' and staffID = 0";
//                System.out.println(sql);
                break;
            case "452":
                sqlforMinBlank = "select min(blankID) from blanks where SUBSTR(blankID, 1, 3) = '452' and staffID = 0";
//                System.out.println(sql);
                break;

        }
        DBConnect db = new DBConnect();
        try {
            db.connect();
            System.out.println(sqlforMinBlank);
            ResultSet rs = db.executeQuery(sqlforMinBlank);
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

    /**
     Logs out the current user and returns to the login screen.
     @throws IOException if an error occurs during the redirection to the login screen.
     */
    public void logOut() throws IOException {
        Application.logOut(stage);
    }

    /**  Opens the manage customer discount screen */
    public void manageCustomerDiscount() throws IOException {
        Stage stage = new Stage();
        fxmlLoader = new FXMLLoader(Application.class.getResource("ManageCustomerDiscount.fxml"));
        scene = new Scene(fxmlLoader.load(), 600, 500);
        stage.setScene(scene);
        ManageCustomerDiscountController manageCustomerDiscountController = new ManageCustomerDiscountController();
        manageCustomerDiscountController.setPreviousScene(this.scene);
        stage.show();

    }

    /**
     Populates the table view of the blanks with data from the database.
     Clears the observable list of blanks, connects to the database, and executes a query to retrieve all blanks from the "blanks" table.
     Then, each row of data is added to the observable list of blanks.
     The observable list is used to populate the table view of the blanks.
     Additionally, the method sets up a filter for searching the blanks by ID and allows the user to select one or multiple rows from the table.
     @throws RuntimeException if there is a SQL exception.
     */
    public void populateBlankTable(){
        DBConnect db = new DBConnect();
        blankObservableList.clear();
//        ResultSet rs;
        String sql = "SELECT * FROM blanks"; //query for dynamic search
//        String sql2 = "SELECT * FROM flights WHERE departure = \"London\" AND arrival = \"budapest\" AND date = \"23-05-10\"";
        try{
            db.connect();
            rs = db.executeQuery(sql);
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
                    return blankSearchModel.getBlankID() > -1;

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
    public void generatePDF(){

    }
    /**
     Populates the Sale table view by querying the database and adding the resulting SaleForm objects to the saleObservableList.
     Then, it sets the column values for the table and applies filtering and sorting to the table view.
     Also, it sets the selectedSaleList to the currently selected items in the table view.
     */
    public  void populateSaleTable(){
        saleObservableList.clear();
        DBConnect db = new DBConnect();

//        ResultSet rs;
        String sql = "SELECT staffID, sale.saleID, sale.price , sale.customerID, customer.firstname, customer.lastname FROM sale\n" +
                "                JOIN customer ON sale.customerID = customer.id where refund = -1;";
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
            staffIDColumn1.setCellValueFactory(new PropertyValueFactory<>("staffID"));
            saleIDColumn1.setCellValueFactory(new PropertyValueFactory<>("saleID"));
            pricecolumn1.setCellValueFactory(new PropertyValueFactory<>("price"));
            customercolumn1.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            firstnamecolumn1.setCellValueFactory(new PropertyValueFactory<>("firstname"));
            lastnamecolumn1.setCellValueFactory(new PropertyValueFactory<>("lastname"));



            saleTableView.setItems(saleObservableList);

            FilteredList<SaleForm> filteredData = new FilteredList<>(saleObservableList, b->true);
            customerSearchTextField.textProperty().addListener((observable, oldValue, newValue)->{
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

    /**

     Approves a refund for the selected sale in the sale table view.
     Updates the database by setting the 'refund' column to 1 for the selected sale.
     Then calls the method 'populateSaleTable()' to refresh the table view with the updated data.
     @throws SQLException if a database access error occurs.
     */
    public void approveRefund() throws SQLException {
        int saleID = selectedSaleList.get(0).getSaleID();
        String sql = "UPDATE sale set refund = 1 where refund = -1 and saleID = "+saleID+";";
        DBConnect db = new DBConnect();
        db.connect();
        PreparedStatement pstmt = db.getConnection().prepareStatement(sql);
        int result = pstmt.executeUpdate();
        System.out.println(result);
        populateSaleTable();

    }


}
