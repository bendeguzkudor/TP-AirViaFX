package com.example.tpairviafx;


import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import javax.xml.transform.Result;


public class Report {
    Integer staffID = null;
    Integer amount = null;
    Long max_blankID = null;
    Long min_blankID = null;

    String blankID = null;
    Cell cell;
    Row row;
    Workbook workbook;
    Sheet sheet;
    String dateFrom;
    String dateTo;
    private String currency;
    private double conversionRate;
    private String card;
    private double taxSum;
    private String cash;
    private double commissionSum;
    private int price;
    private Row headerRow;

    public static void main(String[] args) throws Exception {
        Report report = new Report();
//        report.queryAssignedBlanks();
//        report.createTicketStockTurnOverReport();
//        report.generateSalesReport2();
//        report.generateInterlineSalesReportTemplatePerAdvisor();
        report.populateSalesReport();

//


    }

    public void createTicketStockTurnOverReport() {
        workbook = new XSSFWorkbook();

        // Create a new sheet
        sheet = workbook.createSheet("Table");
        for (int i = 0; i < 20; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 16; j++) {
                cell = row.createCell(j);
                cell.setCellValue("Cell " + i + "," + j);
                // Set center alignment and autosize the column width
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cell.setCellStyle(cellStyle);
                sheet.autoSizeColumn(j);
            }
        }


        ////////////////////////////////////////////////

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 5));//RECEIVED BLANKS
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 6, 10));//ASSIGNED USED BLANKS
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 11, 15)); // FINAL AMOUNTS
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(3, 11, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 2));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 5)); // SUB AGENTS
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 10)); // SUB AGENTS
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 12)); //AGEMTS AMOUNT
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 13, 15));
        sheet.getRow(0).getCell(0).setCellValue("NN");
        sheet.getRow(19).getCell(0).setCellValue("Total: ");
        sheet.getRow(0).getCell(1).setCellValue("RECEIVED BLANKS");
        sheet.getRow(0).getCell(6).setCellValue("ASSIGNED/USED BLANKS ");
        sheet.getRow(0).getCell(11).setCellValue("FINAL AMOUNTS ");
        sheet.getRow(1).getCell(1).setCellValue("AGENT'S STOCK ");
        sheet.getRow(1).getCell(3).setCellValue("SUB AGENTS ");
        sheet.getRow(1).getCell(6).setCellValue("SUB AGENTS ");
        sheet.getRow(1).getCell(11).setCellValue("AGENTS AMOUNT ");
        sheet.getRow(1).getCell(13).setCellValue("SUB AGENTS AMOUNT ");
        sheet.getRow(2).getCell(1).setCellValue("FROM/TO BLANKS ");
        sheet.getRow(2).getCell(2).setCellValue("AMOUNT");
        sheet.getRow(2).getCell(3).setCellValue("STAFF ID");
        sheet.getRow(2).getCell(4).setCellValue("ASSIGNED FROM/TO ");
        sheet.getRow(2).getCell(5).setCellValue("AMOUNT");
        sheet.getRow(2).getCell(6).setCellValue("STAFF ID");
        sheet.getRow(2).getCell(7).setCellValue("ASSIGNED FROM TO ");
        sheet.getRow(2).getCell(8).setCellValue("AMOUNT");
        sheet.getRow(2).getCell(9).setCellValue("USED FROM/TO");
        sheet.getRow(2).getCell(10).setCellValue("AMOUNT");
        sheet.getRow(2).getCell(11).setCellValue(" FROM/TO ");
        sheet.getRow(2).getCell(12).setCellValue("AMOUNT");
        sheet.getRow(2).getCell(13).setCellValue("STAFF ID");
        sheet.getRow(2).getCell(14).setCellValue("FROM/TO");
        sheet.getRow(2).getCell(15).setCellValue("AMOUNT");
//
        queryAssignedBlanks();
        for (int i = 0; i < 16; i++) {
            sheet.autoSizeColumn(i);

        }
        // Write the workbook to a file
        try (FileOutputStream outputStream = new FileOutputStream("Table2.xlsx")) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Table.xlsx created successfully!");

    }

    public void queryAssignedBlanks() {
        DBConnect db = new DBConnect();
        ResultSet rs;
//        String sql = // Query that returns the assigned blanks to advisors grouped by ranged and one line per advisor code and assigned blank type
//                "SELECT staffID, MAX(blankID) - MIN(blankID)+1 as amount, MIN(blankID) as min_blankID, MAX(blankID) as max_blankID\n" +
//                        "FROM blanks\n" +
//                        "WHERE staffID != 0\n" +
//                        "GROUP BY staffID,LEFT(blankID, 3);"; // last Column
//        String sql2 = // Query for blanks that have been assigned and sold //
//                "SELECT staffID, MAX(blankID) - MIN(blankID) + 1 AS amount, MIN(blankID) AS min_blankID, MAX(blankID) AS max_blankID FROM blanks " +
//                        "WHERE staffID IS NOT NULL AND sold = 1 and dateUsed >= \"20230401\" and dateUsed <= \"20230430\" GROUP BY staffID, LEFT(blankID, 3);"; // Blanks used
//
//        String firstColumn =
//                "SELECT MAX(blankID) - MIN(blankID) + 1 AS amount, MIN(blankID) AS min_blankID, MAX(blankID) AS max_blankID\n" +
//                        "FROM blanks WHERE dateAdded >= 20230401 and dateAdded <=20230430 \n" +
//                        "GROUP BY staffID, LEFT(blankID, 3)";
//        String secondColumn =
//                "SELECT staffID, MAX(blankID) - MIN(blankID) + 1 AS amount, MIN(blankID) AS min_blankID, MAX(blankID) AS max_blankID\n" +
//                        "FROM blanks\n" +
//                        "WHERE staffID IS NOT NULL AND dateAssigned >= 20230401 and dateAssigned <= 20230401 " +
//                        "GROUP BY staffID, LEFT(blankID, 3);"; // col 3
//        String sql5 =
//                "SELECT MAX(blankID) - MIN(blankID) + 1 AS amount, MIN(blankID) AS min_blankID, MAX(blankID) AS max_blankID\n" +
//                        "FROM blanks\n" +
//                        "WHERE staffID IS NOT NULL" +
//                        "GROUP BY staffID, LEFT(blankID, 3);";

        String addedBlanksInDateRange = "SELECT MAX(blankID) - MIN(blankID) + 1 AS amount, MIN(blankID) AS min_blankID, MAX(blankID) AS max_blankID\n" +
                "FROM blanks WHERE dateAdded >= \"20230401\" and dateAdded <=\"20230430\"\n" +
                "GROUP BY LEFT(blankID, 3);"; // col 1
        String newlyAddedBlanksAssigned = "SELECT staffID, MAX(blankID) - MIN(blankID) + 1 AS amount, MIN(blankID) AS min_blankID, MAX(blankID) AS max_blankID\n" +
                "FROM blanks WHERE dateAdded >= \"20230401\" and dateAdded <=\"20230430\"\n AND  staffID != 0 AND dateAssigned >= \"20230401\" and dateAssigned <=\"20230430\"\n" +
                "GROUP BY staffID, LEFT(blankID, 3);"; // col 2
        String assignedBlanksInTheReportPeriod = "SELECT staffID, MAX(blankID) - MIN(blankID)+1 as amount, MIN(blankID) as min_blankID, MAX(blankID) as max_blankID\n" +
                "FROM blanks\n" +
                "WHERE staffID != 0\n AND dateAssigned >= 20230401 and dateAssigned <= 20230430 " +
                "GROUP BY staffID,LEFT(blankID, 3);"; // col 3
        String blanksUsedWithinTheReportPeriod = "SELECT staffID, MAX(blankID) - MIN(blankID) + 1 AS amount, MIN(blankID) AS min_blankID, MAX(blankID) AS max_blankID " +
                "FROM blanks " +
                "WHERE staffID IS NOT NULL AND sold = 1 and dateUsed >= \"20230401\" and dateUsed <= \"20230430\" " +
                "GROUP BY staffID, LEFT(blankID, 3);"; //col4
        String blanksAvailableAtTheEnd = "SELECT staffID, MAX(blankID) - MIN(blankID) + 1 AS amount, MIN(blankID) AS min_blankID, MAX(blankID) AS max_blankID\n" +
                "FROM blanks\n" +
                "WHERE sold != 1\n" +
                "GROUP BY staffID, LEFT(blankID, 3);"; // col 5
        String AssignedBlanksAvailableAtTheEndByAdvisor = "SELECT staffID, MAX(blankID) - MIN(blankID) + 1 AS amount, MIN(blankID) AS min_blankID, MAX(blankID) AS max_blankID\n" +
                "FROM blanks\n" +
                "WHERE sold != 1 AND staffID != 0\n" +
                "GROUP BY staffID, LEFT(blankID, 3);"; // col 6

        try {
            db.connect();
            System.out.println(addedBlanksInDateRange);
            rs = db.statement.executeQuery(addedBlanksInDateRange);
            int i = 3;
            int j = 1;
            int sum = 0;
            while (rs.next()) {
                amount = rs.getInt(1);
                min_blankID = rs.getLong(2);
                max_blankID = rs.getLong(3);
                sum += amount;
                System.out.print(rs.getInt(1) + ":");
                System.out.print(rs.getLong(2) + ":");
                System.out.print(rs.getLong(3) + ":");
                System.out.println();
                System.out.println(i + "" + j);
                sheet.getRow(i).getCell(j).setCellValue(min_blankID + "-" + max_blankID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(amount);
                j = 1;
                i++;
            }
            sheet.getRow(19).getCell(2).setCellValue(sum);
            rs = db.statement.executeQuery(newlyAddedBlanksAssigned);
            i = 3;
            j = 3;
            sum = 0;
            while (rs.next()) {
                staffID = rs.getInt(1);
                amount = rs.getInt(2);
                min_blankID = rs.getLong(3);
                max_blankID = rs.getLong(4);
                sum += amount;
                System.out.print(rs.getInt(1) + ":");
                System.out.print(rs.getInt(2) + ":");
                System.out.print(rs.getLong(3) + ":");
                System.out.print(rs.getLong(4) + ":");
                System.out.println();
                System.out.println(i + "" + j);
                sheet.getRow(i).getCell(j).setCellValue(staffID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(min_blankID + "-" + max_blankID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(amount);
                j = 3;
                i++;
            }
            sheet.getRow(19).getCell(5).setCellValue(sum);
            rs = db.statement.executeQuery(assignedBlanksInTheReportPeriod);
            i = 3;
            j = 6;
            sum = 0;
            while (rs.next()) {
                staffID = rs.getInt(1);
                amount = rs.getInt(2);
                min_blankID = rs.getLong(3);
                max_blankID = rs.getLong(4);
                sum += amount;
                System.out.print(rs.getInt(1) + ":");
                System.out.print(rs.getInt(2) + ":");
                System.out.print(rs.getLong(3) + ":");
                System.out.print(rs.getLong(4) + ":");
                System.out.println();
                System.out.println(i + "" + j);
                sheet.getRow(i).getCell(j).setCellValue(staffID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(min_blankID + "-" + max_blankID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(amount);
                j = 6;
                i++;
            }
            sheet.getRow(19).getCell(8).setCellValue(sum);
            rs = db.statement.executeQuery(blanksUsedWithinTheReportPeriod);
            i = 3;
            j = 9;
            sum = 0;
            while (rs.next()) {
                amount = rs.getInt(2);
                min_blankID = rs.getLong(3);
                max_blankID = rs.getLong(4);
                sum += amount;
                System.out.print(rs.getInt(1) + ":");
                System.out.print(rs.getInt(2) + ":");
                System.out.print(rs.getLong(3) + ":");
                System.out.print(rs.getLong(4) + ":");
                System.out.println();
                System.out.println(i + "" + j);
                sheet.getRow(i).getCell(j).setCellValue(min_blankID + "-" + max_blankID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(amount);
                j = 9;
                i++;
            }
            sheet.getRow(19).getCell(10).setCellValue(sum);
            rs = db.statement.executeQuery(blanksAvailableAtTheEnd);
            i = 3;
            j = 11;
            sum = 0;
            while (rs.next()) {
                amount = rs.getInt(2);
                min_blankID = rs.getLong(3);
                max_blankID = rs.getLong(4);
                sum += amount;
                System.out.print(rs.getInt(1) + ":");
                System.out.print(rs.getInt(2) + ":");
                System.out.print(rs.getLong(3) + ":");
                System.out.print(rs.getLong(4) + ":");
                System.out.println();
                System.out.println(i + "" + j);
                sheet.getRow(i).getCell(j).setCellValue(min_blankID + "-" + max_blankID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(amount);
                j = 11;
                i++;
            }
            sheet.getRow(19).getCell(12).setCellValue(sum);
            rs = db.statement.executeQuery(AssignedBlanksAvailableAtTheEndByAdvisor);
            i = 3;
            j = 13;
            sum = 0;
            while (rs.next()) {
                staffID = rs.getInt(1);
                amount = rs.getInt(2);
                min_blankID = rs.getLong(3);
                max_blankID = rs.getLong(4);
                sum += amount;
                System.out.print(rs.getInt(1) + ":");
                System.out.print(rs.getInt(2) + ":");
                System.out.print(rs.getLong(3) + ":");
                System.out.print(rs.getLong(4) + ":");
                System.out.println();
                System.out.println(i + "" + j);
                sheet.getRow(i).getCell(j).setCellValue(staffID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(min_blankID + "-" + max_blankID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(amount);
                j = 13;
                i++;
            }
            sheet.getRow(19).getCell(15).setCellValue(sum);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void generateSalesReport() {
        workbook = new XSSFWorkbook();

        sheet = workbook.createSheet("Interline Report");

        for (int i = 0; i < 14; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 21; j++) {
                cell = row.createCell(j);
                cell.setCellValue("Cell " + i + "," + j);

                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cell.setCellStyle(cellStyle);
                sheet.autoSizeColumn(j);
            }
        }

        //sheet.addMergedRegion(new CellRangeAddress(startrow, endrow, startcoloumn, endcoloum)); use this template
        //sheet.getRow().getCell().setCellValue("");

        //AIRVIA DOCUMENTS
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 7));//airvia documents
        sheet.getRow(0).getCell(1).setCellValue("AirVia Documents");

        sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 4));//fare amount
        sheet.getRow(1).getCell(2).setCellValue("Fare amount");

        sheet.addMergedRegion(new CellRangeAddress(1, 1, 5, 6));//taxes
        sheet.getRow(1).getCell(5).setCellValue("TAXES");

        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 0));//N
        sheet.getRow(0).getCell(0).setCellValue("N");

        sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1));//Original issued number
        sheet.getRow(1).getCell(1).setCellValue("ORIGINAL \n ISSUED \n NUMBER");


        sheet.addMergedRegion(new CellRangeAddress(1, 2, 7, 7));//TTL AMOUNTS
        sheet.getRow(1).getCell(7).setCellValue("TTL \n Documents");
        sheet.getRow(2).getCell(2).setCellValue("USD");
        sheet.getRow(2).getCell(3).setCellValue("USD/BGL");
        sheet.getRow(2).getCell(4).setCellValue("BGL");
        sheet.getRow(2).getCell(5).setCellValue("LZ");
        sheet.getRow(2).getCell(6).setCellValue("OTHS");

        sheet.addMergedRegion(new CellRangeAddress(13, 13, 0, 1)); // nbr of tkts
        sheet.getRow(13).getCell(0).setCellValue("NMBR TKTS");

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 11));//In exchange for docs
        sheet.getRow(0).getCell(8).setCellValue("IN EXCHANGE FOR DOCS");


        sheet.addMergedRegion(new CellRangeAddress(1, 1, 8, 11));//AIRLINES
        sheet.getRow(1).getCell(8).setCellValue("AIRLINES");

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 12, 17));//forms of payments
        sheet.getRow(0).getCell(12).setCellValue("FORMS OF PAYMENTS");

        sheet.addMergedRegion(new CellRangeAddress(1, 2, 12, 12));//CASH
        sheet.getRow(1).getCell(12).setCellValue("CASH");

        sheet.getRow(2).getCell(8).setCellValue("CD");

        sheet.getRow(2).getCell(9).setCellValue("DOC.NBR");
        sheet.getRow(2).getCell(10).setCellValue("FC");
        sheet.getRow(2).getCell(11).setCellValue("PROR.AMNT");

        sheet.addMergedRegion(new CellRangeAddress(1, 1, 13, 16));//CASH
        sheet.getRow(1).getCell(13).setCellValue("CREDIT CARDS");

        sheet.getRow(2).getCell(13).setCellValue("LC");
        sheet.getRow(2).getCell(14).setCellValue("FULL CC NUMBER");
        sheet.getRow(2).getCell(15).setCellValue("USD");
        sheet.getRow(2).getCell(16).setCellValue("BGL");

        sheet.addMergedRegion(new CellRangeAddress(1, 2, 17, 17));//TOTAL AMNTS PAID
        sheet.getRow(1).getCell(17).setCellValue("TTL \n AMOUNTS \n PAID");

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 18, 19));//COMMISSIONS
        sheet.getRow(0).getCell(18).setCellValue("COMMISSIONS");

        sheet.addMergedRegion(new CellRangeAddress(1, 1, 18, 19));//COMMISSIONS
        sheet.getRow(1).getCell(18).setCellValue("ASSESSABLE AMOUNTS");

        sheet.addMergedRegion(new CellRangeAddress(0, 2, 20, 20));//COMMISSIONS
        sheet.getRow(0).getCell(20).setCellValue("NON \n ASSESSBALE \n AMOUNTS");



        sheet.getRow(2).getCell(18).setCellValue("Commission %");

        for (int i = 0; i < 21; i++) {
            sheet.autoSizeColumn(i);

        }
        // Write the workbook to a file
        try (FileOutputStream outputStream = new FileOutputStream("Table10.xlsx")) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void populateSalesReport(){
        workbook = new XSSFWorkbook();

        // create a new sheet
        sheet = workbook.createSheet("Sheet1");

        // create a header row
        headerRow = sheet.createRow(0);
        String[] columnTitles = {"NO. ","BLANKS", "PRICE USD", "PRICE CURRNECY", "CONVERSION RATE", "TAXES", "TAX RATE",
                "CASH", "CARD NUMBER", "COMMISSION RATE", "COMMISSION SUM", "TOTAL AMOUNTS PAID"};
        String sql = "SELECT GROUP_CONCAT(b.blankID) as blankIDs, s.staffID, s.price, s.currency,s.conversionRate, s.paymentType,s.cardNumber, s.commissionsum, s.taxSum,  COUNT(b.blankID) as num_blanks\n" +
                "FROM sale s\n" +
                "LEFT JOIN soldBlanks b ON s.saleID = b.saleID\n" +
                "GROUP BY s.saleID\n" +
                "HAVING num_blanks > 1 OR num_blanks = 1;";
        DBConnect db = new DBConnect();
        ResultSet rs;
        try {
            db.connect();
            rs = db.statement.executeQuery(sql);

//
            rs.last();
            int lastRow = rs.getRow();
            rs.first();
            for (int i = 0; i < lastRow+1; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < columnTitles.length; j++) {
                    cell = row.createCell(j);
                    cell.setCellValue("Cell " + i + "," + j);
                    // Set center alignment and autosize the column width
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    cell.setCellStyle(cellStyle);
                    sheet.autoSizeColumn(j);
                }
            }
            for(int j = 0; j < columnTitles.length; j++) {
                sheet.getRow(0).getCell(j).setCellValue(columnTitles[j]);
            }
            int i = 1;
            int j = 0;
            int taxSumm = 0;
            int priceSum = 0;
            int basePriceSum = 0;
            int count = 1;

            while (rs.next()) {
                blankID = rs.getString(1);
                price = rs.getInt(3);
                currency  = rs.getString(4);
                conversionRate = rs.getDouble(5);
                cash = rs.getString(6);
                card = rs.getString(7);
                commissionSum = rs.getDouble(8);
                taxSum = rs.getDouble(9);
                taxSumm += taxSum;
                priceSum += price;
                basePriceSum += price-taxSum;
                sheet.getRow(i).getCell(j).setCellValue(i);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(blankID +"\n" );
                j++;
                sheet.getRow(i).getCell(j).setCellValue(price-taxSum);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(currency);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(conversionRate);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(taxSum);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(taxSum / price);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(cash);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(card);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(commissionSum/price);
                j++;
                sheet.getRow(i).getCell(j).setCellValue((price-taxSum) * conversionRate);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(price);
                j=0;
                i++;

            }for(int k = 0; k < columnTitles.length; k++) {
                sheet.autoSizeColumn(j);
            }

            sheet.getRow(lastRow).getCell(0).setCellValue("TOTALS ");
            sheet.getRow(lastRow).getCell(11).setCellValue(priceSum);

            sheet.createRow(lastRow+2).createCell(9);
            sheet.addMergedRegion(new CellRangeAddress(lastRow+2, lastRow+2, 9, 10));//COMMISSIONS
            sheet.getRow(lastRow+2).getCell(9).setCellValue("TOTAL COMMISSION AMOUNTS  :"+ (priceSum-taxSumm)*(commissionSum/price));

            sheet.createRow(lastRow+3).createCell(9);
            sheet.addMergedRegion(new CellRangeAddress(lastRow+3, lastRow+3, 9, 10));//COMMISSIONS
            sheet.getRow(lastRow+3).getCell(9).setCellValue("NET AMOUNTS FOR AGEMTS DEBIT  :" + (basePriceSum - (priceSum-taxSumm)*(commissionSum/price)));

            sheet.createRow(lastRow+4).createCell(9);
            sheet.addMergedRegion(new CellRangeAddress(lastRow+4, lastRow+4, 9, 10));//COMMISSIONS
            sheet.getRow(lastRow+4).getCell(9).setCellValue("TOTAL BANK REMMITTANCE TO AIRVIA  :" + (priceSum -(priceSum-taxSumm)*(commissionSum/price)));

//        sheet.getRow(1).getCell(0).setCellValue("1");

            for(int x = 0; x < columnTitles.length; x++) {
                sheet.autoSizeColumn(x);
            }
            // write the workbook to a file
            try {
                FileOutputStream outputStream = new FileOutputStream("excel-sheet.xlsx");
                workbook.write(outputStream);
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            sheet.getRow(19).getCell(2).setCellValue(sum);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void generateInterlineSalesReportTemplatePerAdvisor(){
        workbook = new XSSFWorkbook();

        // create a new sheet
        sheet = workbook.createSheet("Sheet1");

        // create a header row
         headerRow = sheet.createRow(0);

        // add column titles to header row
        String[] columnTitles = {"NO. ","BLANKS", "PRICE USD", "PRICE CURRNECY", "CONVERSION RATE", "TAXES", "TAX RATE",
                "CASH", "CARD NUMBER", "COMMISSION RATE", "COMMISSION SUM", "TOTAL AMOUNTS PAID"};
//        int rowCount = 10;
//        int columnCount = columnTitles.length;
//        Row firstRow = sheet.createRow(0);
//        for(int j = 0; j < columnCount; j++) {
//            Cell cell = firstRow.createCell(j);
//            cell.setCellValue(columnTitles[j]);
//        }
//        for(int i = 1; i < rowCount; i++) {
//            Row row = sheet.createRow(i);
//            for(int j = 0; j < columnCount; j++) {
//                Cell cell = row.createCell(j);
//                cell.setCellValue("Cell " + j + "," + i);
//            }
//        }/////////////
        for (int i = 0; i < 10; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < columnTitles.length; j++) {
                cell = row.createCell(j);
                cell.setCellValue("Cell " + i + "," + j);
                // Set center alignment and autosize the column width
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cell.setCellStyle(cellStyle);
                sheet.autoSizeColumn(j);
            }
        }
        for(int j = 0; j < columnTitles.length; j++) {
            sheet.getRow(0).getCell(j).setCellValue(columnTitles[j]);
        }

        /////////////
        for(int j = 0; j < columnTitles.length; j++) {
            sheet.autoSizeColumn(j);
        }
        sheet.getRow(9).getCell(0).setCellValue("TOTALS ");

        sheet.createRow(11).createCell(9);
        sheet.addMergedRegion(new CellRangeAddress(11, 11, 9, 10));//COMMISSIONS
        sheet.getRow(11).getCell(9).setCellValue("TOTAL COMMISSION AMOUNTS");

        sheet.createRow(12).createCell(9);
        sheet.addMergedRegion(new CellRangeAddress(12, 12, 9, 10));//COMMISSIONS
        sheet.getRow(12).getCell(9).setCellValue("NET AMOUNTS FOR AGEMTS DEBIT");

        sheet.createRow(13).createCell(9);
        sheet.addMergedRegion(new CellRangeAddress(13, 13, 9, 10));//COMMISSIONS
        sheet.getRow(13).getCell(9).setCellValue("TOTAL BANK REMMITTANCE TO AIRVIA");

//        sheet.getRow(1).getCell(0).setCellValue("1");


//        populateSalesReport(sheet);
        for(int j = 0; j < columnTitles.length; j++) {
            sheet.autoSizeColumn(j);
        }
        // write the workbook to a file
        try {
            FileOutputStream outputStream = new FileOutputStream("excel-sheet.xlsx");
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}






