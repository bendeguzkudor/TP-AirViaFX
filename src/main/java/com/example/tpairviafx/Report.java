package com.example.tpairviafx;


import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.util.Matrix;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.pdfbox.util.Matrix;

import org.apache.pdfbox.pdfwriter.ContentStreamWriter;

import org.apache.poi.xwpf.usermodel.Document;

/**Class for generating the sales and ticket stock turnover report. Uses Apache POI Libary */
public class Report {

    Integer amount = null;
    Long max_blankID = null;
    Long min_blankID = null;

    String blankID = null;
    Cell cell;
    Row row;
    Workbook workbook;
    Sheet sheet;


    private String datefrom;
    private String dateTo;

    private int queryStaffID;
    private String currency;
    private double conversionRate;
    private String card;
    private double taxSum;
    private String cash;
    private double commissionSum;
    private int price;
    private int staffID;
    DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");


    private Row headerRow;
    private Date dateToDate;
    private Date dateFromDate;
    private Date todaysDate;

    public static void main(String[] args) throws Exception {
        Report report = new Report("20230401", "20230430", 250);
//        report.queryAssignedBlanks();
        report.createTicketStockTurnOverReport();
//        report.generateSalesReport2();
//        report.generateInterlineSalesReportTemplatePerAdvisor();


        report.individualInterlineSalesReport();
        report.globalInterLineSalesReport();
        report.globalDomesticSalesReport();
        report.domesticIndividualReport();
//


    }

    /**
     Constructor for generating a sales report for a given date range and staff ID.
     @param datefrom start date of the date range in format "yyyy-MM-dd" converts to yyyyMMdd
     @param dateTo end date of the date range in format "yyyy-MM-dd" converts to yyyyMMdd
     @param staffID ID of the staff member for whom the report is being generated
     @throws ParseException if the input dates are not in the correct format
     */
    public Report(String datefrom, String dateTo, int staffID) throws ParseException {
        this.datefrom = datefrom;
        this.dateTo = dateTo;
        this.staffID = staffID;
        System.out.println(staffID);
        dateFromDate = inputFormat.parse(datefrom);
        dateToDate = inputFormat.parse(dateTo);
        todaysDate = inputFormat.parse(Application.getDate());
        System.out.println(todaysDate);
    }


    /**

    This method creates a Ticket Stock Turnover Report in an Excel workbook. It creates a new sheet in the workbook
    and generates a report on the turnover of ticket stocks. The method populates the sheet with data on the
    RECEIVED BLANKS, ASSIGNED/USED BLANKS, and FINAL AMOUNTS of ticket stocks.
     The method also retrieves data from a database using the
    queryAssignedBlanks() method and populates the sheet with this data. Finally,
     the method adds a title to the report,
    the dates that the report covers, and the current date.
    */
    public void createTicketStockTurnOverReport() {
        workbook = new XSSFWorkbook();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 20);
        CellStyle cstyle = workbook.createCellStyle();
        cstyle.setAlignment(HorizontalAlignment.CENTER);
        cstyle.setFont(font);

        // Create a new sheet
        sheet = workbook.createSheet("Table");
        for (int i = 0; i < 20; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 16; j++) {
                cell = row.createCell(j);
//                cell.setCellValue("Cell " + i + "," + j);
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


        sheet.createRow(23 + 3).createCell(3);
        sheet.addMergedRegion(new CellRangeAddress(23 + 3, 23 + 3, 3, 4)); // StaffID

        sheet.createRow(22).createCell(7);
        sheet.createRow(23).createCell(7);
        sheet.addMergedRegion(new CellRangeAddress(22,23, 7,9));
        sheet.getRow(22).getCell(7).setCellValue("TICKET STOCK TURNOVER REPORT");
        sheet.getRow(22).getCell(7).setCellStyle(cstyle);
        sheet.getRow(23 + 3).createCell(8);
        sheet.addMergedRegion(new CellRangeAddress(23 + 3, 23 + 3, 8, 10)); // Date From
        sheet.getRow(23 + 3).getCell(8).setCellValue("Date From   :  " + outputFormat.format(dateFromDate));

        sheet.createRow(23 + 4).createCell(8);
        sheet.addMergedRegion(new CellRangeAddress(23 + 4, 23 + 4, 8, 10)); // Date To
        sheet.getRow(23 + 4).getCell(8).setCellValue("Date to   :  " + outputFormat.format(dateToDate));

        sheet.getRow(23 + 4).createCell(7);
        sheet.getRow(23 + 4).getCell(7).setCellValue("Date :" + outputFormat.format(todaysDate));
//        signature(sheet, 21, 0, "TICKET STOCK TURNOVER", staffID);

        for (int i = 0; i < 16; i++) {
            sheet.autoSizeColumn(i);

        }
        // Write the workbook to a file
        try (FileOutputStream outputStream = new FileOutputStream("reports/TicketStockTurnOver"+datefrom+"-"+dateTo+".xlsx")) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Table.xlsx created successfully!");

    }
    /** Not used */
    public void signature (Sheet sheet, int lastrow, int middle, String title, int staffID){
        lastrow = lastrow+3;
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 20);
        CellStyle cstyle = workbook.createCellStyle();
        cstyle.setAlignment(HorizontalAlignment.CENTER);

        cstyle.setFont(font);
        sheet.createRow(lastrow + 2).createCell(6);
//        sheet.addMergedRegion(new CellRangeAddress(lastrow + 2, lastrow+2,  6, 6)); // StaffID
        sheet.getRow(lastrow+2).createCell(7);
        sheet.getRow(lastrow+2).getCell(7).setCellValue("StaffID" + staffID);

        sheet.createRow(lastrow).createCell(7);
        sheet.createRow(lastrow+1).createCell(7);
        sheet.addMergedRegion(new CellRangeAddress(lastrow,lastrow+1, 7,9));
        sheet.getRow(lastrow).getCell(7).setCellValue(title);
        sheet.getRow(lastrow).getCell(7).setCellStyle(cstyle);
        sheet.getRow(lastrow+2).createCell(8);
        sheet.addMergedRegion(new CellRangeAddress(lastrow+2, lastrow+2, 8, 10)); // Date From
        sheet.getRow(lastrow+ 2).getCell(8).setCellValue("Date From   :  " + outputFormat.format(dateFromDate));

        sheet.createRow(lastrow + 3).createCell(8);
        sheet.addMergedRegion(new CellRangeAddress(lastrow + 3, lastrow+ 3, 8, 10)); // Date To
        sheet.getRow(lastrow + 3).getCell(8).setCellValue("Date to   :  " + outputFormat.format(dateToDate));

        sheet.getRow(lastrow + 3).createCell(7);
        sheet.getRow(lastrow + 3).getCell(7).setCellValue("Date :" + outputFormat.format(todaysDate));

    }

    /**

     This method queries the database for information related to blanks . It uses the DBConnect class
     to connect to the database, and runs 6 different SQL queries to obtain information about assigned blanks,
     newly added blanks, blanks assigned from newly added blanks, and blanks assigned to advisors.Finally, the blanks stage at the end of the report period.
     The method takes no input parameters, but uses the instance variables "dateFrom" and "dateTo" to specify the date range for
     which to obtain data. The results of each query are stored in ResultSet objects and can be accessed by the caller
     through appropriate methods.
     */

    public void queryAssignedBlanks() {
        DBConnect db = new DBConnect();
        ResultSet rs;

        String blanksUsedWithinTheReportPeriod = "SELECT \n" +
                "    MIN(blankID) AS start_range,\n" +// return the ranges
                "    MAX(blankID) AS end_range,\n" +//
                "    COUNT(*) AS range_size\n" +//
                "FROM (\n" +
                "    SELECT \n" +//
                "        t.*,\n" +
                "        @rn := IF(@prev_staff = staffID AND blankID = @prev_blank + 1, @rn, @rn + 1) AS range_group,\n" +//
                "        @prev_staff := staffID,\n" +/////////////////////////////////////////////////////////////////////////////////////////////
                "        @prev_blank := blankID\n" +//
                "    FROM blanks t\n" +//
                "    CROSS JOIN (SELECT @rn := 0, @prev_staff := NULL, @prev_blank := NULL) vars\n" +//
                "    WHERE sold = 1 and (blankID LIKE '1%' OR blankID LIKE '2%' OR blankID LIKE '4%')\n" +//
                "    ORDER BY blankID\n" +//
                ") t\n" +
                "GROUP BY range_group\n" +//
                "HAVING COUNT(*) >= 1;"; //col4


        String blanksAdded = "SELECT \n" +
                "    MIN(blankID) AS start_range,\n" +//
                "    MAX(blankID) AS end_range,\n" +//
                "    COUNT(*) AS range_size\n" +//
                "FROM (\n" +//
                "    SELECT \n" +
                "        t.*,\n" +//
                "        @rn := IF(blankID = @prev_blank + 1, @rn, @rn + 1) AS range_group,\n" +
                "        @prev_blank := blankID\n" +///////////////////////////////////////////////////////////////////////
                "    FROM blanks t\n" +//
                "    CROSS JOIN (SELECT @rn := 0, @prev_blank := NULL) vars\n" +
                "\twhere\n" +//
                "\tdateAdded >= "+datefrom+" and dateAdded <= "+dateTo+"" +
                "    ORDER BY blankID\n" +//
                ") t\n" +
                "GROUP BY range_group \n" +
                "HAVING COUNT(*) > 1\n" +
                "ORDER BY start_range;";
        String blanksAssignedFromAdded = "SELECT \n" +
                "\tstaffID,\n" +
                "    MIN(blankID) AS start_range,\n" +
                "    MAX(blankID) AS end_range,\n" +
                "    COUNT(*) AS range_size\n" +//
                "FROM (\n" +
                "    SELECT \n" +
                "        t.*,\n" +//
                "        @rn := IF(@prev_staff = staffID AND blankID = @prev_blank + 1, @rn, @rn + 1) AS range_group,\n" +
                "        @prev_staff := staffID,\n" +
                "        @prev_blank := blankID\n" +//
                "    FROM blanks t\n" +
                "    CROSS JOIN (SELECT @rn := 0, @prev_staff := NULL, @prev_blank := NULL) vars\n" +
                "    WHERE dateAdded >= "+datefrom+" \n" +////////////////////////////////////////////////////////////////////////
                "      AND dateAdded <= '"+dateTo+"' \n" +//
                "      AND dateAssigned >= '"+datefrom+"' \n" +//
                "      AND dateAssigned <= '"+dateTo+"'\n" +
                "      and staffID != 0\n" +
                "    ORDER BY staffID, blankID\n" +
                ") t\n" +
                "GROUP BY staffID, range_group \n" +
                "HAVING COUNT(*) > 1\n" +
                "ORDER BY staffID, start_range;";
        String blanksAssignedToAdvisors = "SELECT \n" +
                "\tstaffID,\n" +
                "    MIN(blankID) AS start_range,\n" +//
                "    MAX(blankID) AS end_range,\n" +//
                "    COUNT(*) AS range_size\n" +
                "FROM (\n" +
                "    SELECT \n" +
                "        t.*,\n" +
                "        @rn := IF(@prev_staff = staffID AND blankID = @prev_blank + 1, @rn, @rn + 1) AS range_group,\n" +
                "        @prev_staff := staffID,\n" +/////////////////////
                "        @prev_blank := blankID\n" +/////////////////////
                "    FROM blanks t\n" +/////////////////////////////////////////////
                "    CROSS JOIN (SELECT @rn := 0, @prev_staff := NULL, @prev_blank := NULL) vars\n" +
                "    WHERE staffID != 0\n" +//////////////////////////////////////
                "    AND dateAssigned >= '"+datefrom+"' \n" +
                "    AND dateAssigned <= '"+dateTo+"' " +
                "    ORDER BY staffID, blankID\n" +
                ") t\n" +
                "GROUP BY staffID, range_group \n" +
                "HAVING COUNT(*) > 1\n" +///////////////
                "ORDER BY staffID, start_range;";
        String blanksAvaliableAtEndoF = "SELECT \n" +///////////////////
                "    MIN(blankID) AS start_range,\n" +
                "    MAX(blankID) AS end_range,\n" +
                "    COUNT(*) AS range_size\n" +/////////////////
                "FROM (\n" +
                "    SELECT \n" +
                "        t.*,\n" +
                "        @rn := IF(blankID = @prev_blank + 1, @rn, @rn + 1) AS range_group,\n" +
                "        @prev_blank := blankID\n" +
                "    FROM blanks t\n" +////////////////////////////
                "    CROSS JOIN (SELECT @rn := 0, @prev_blank := NULL) vars\n" +////////////////////////
                "\twhere\n" +
                "\tsold != 1\n" +
                "    ORDER BY blankID\n" +
                ") t\n" +
                "GROUP BY range_group \n" +
                "HAVING COUNT(*) > 1\n" +
                "ORDER BY start_range;";
        String blanksAssignedAtTheEndOf = "SELECT \n" +
                "\tstaffID,\n" +
                "    MIN(blankID) AS start_range,\n" +
                "    MAX(blankID) AS end_range,\n" +
                "    COUNT(*) AS range_size\n" +
                "FROM (\n" +
                "    SELECT \n" +
                "        t.*,\n" +///////////////////////
                "        @rn := IF(@prev_staff = staffID AND blankID = @prev_blank + 1, @rn, @rn + 1) AS range_group,\n" +
                "        @prev_staff := staffID,\n" +
                "        @prev_blank := blankID\n" +
                "    FROM blanks t\n" +/////////////////////////////////////////////////////////////////////////////////////////////////////
                "    CROSS JOIN (SELECT @rn := 0, @prev_staff := NULL, @prev_blank := NULL) vars\n" +
                "    WHERE sold !=1 and staffID != 0\n" +
                "    ORDER BY staffID, blankID\n" +
                ") t\n" +
                "GROUP BY staffID, range_group \n" +
                "HAVING COUNT(*) > 1\n" +
                "ORDER BY staffID, start_range;";
        try {
            db.connect();
            rs = db.executeQuery(blanksAdded);
            int i = 3;
            int j = 1;
            int sum = 0;
            while (rs.next()) {
                min_blankID = rs.getLong(1);
                max_blankID = rs.getLong(2);
                amount = rs.getInt(3);
                sum += amount;
                System.out.print(rs.getLong(1) + ":");
                System.out.print(rs.getLong(2) + ":");
                System.out.print(rs.getInt(3) + ":");
                System.out.println();
                System.out.println(i + "" + j);
                sheet.getRow(i).getCell(j).setCellValue(min_blankID + "-" + max_blankID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(amount);
                j = 1;
                i++;
            }
            sheet.getRow(19).getCell(2).setCellValue(sum);
            rs = db.executeQuery(blanksAssignedFromAdded);
            i = 3;
            j = 3;
            sum = 0;
            while (rs.next()) {
                queryStaffID = rs.getInt(1);
                min_blankID = rs.getLong(2);
                max_blankID = rs.getLong(3);
                amount = rs.getInt(4);
                sum += amount;
                System.out.print(rs.getInt(1) + ":");
                System.out.print(rs.getLong(2) + ":");
                System.out.print(rs.getLong(3) + ":");
                System.out.print(rs.getInt(4) + ":");
                System.out.println(i + "" + j);
                sheet.getRow(i).getCell(j).setCellValue(queryStaffID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(min_blankID + "-" + max_blankID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(amount);
                j = 3;
                i++;
            }
            sheet.getRow(19).getCell(5).setCellValue(sum);
            rs = db.executeQuery(blanksAssignedToAdvisors);
            i = 3;
            j = 6;
            sum = 0;
            while (rs.next()) {
                queryStaffID = rs.getInt(1);
                min_blankID = rs.getLong(2);
                max_blankID = rs.getLong(3);
                amount = rs.getInt(4);
                sum += amount;
                System.out.println();
                System.out.println(i + "" + j);
                sheet.getRow(i).getCell(j).setCellValue(queryStaffID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(min_blankID + "-" + max_blankID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(amount);
                j = 6;
                i++;
            }
            sheet.getRow(19).getCell(8).setCellValue(sum);
            rs = db.executeQuery(blanksUsedWithinTheReportPeriod);
            i = 3;
            j = 9;
            sum = 0;
            while (rs.next()) {
                min_blankID = rs.getLong(1);
                max_blankID = rs.getLong(2);
                amount = rs.getInt(3);
                sum += amount;
                System.out.print(rs.getLong(1) + ":");
                System.out.print(rs.getLong(2) + ":");
                System.out.print(rs.getInt(3) + ":");

                sheet.getRow(i).getCell(j).setCellValue(min_blankID + "-" + max_blankID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(amount);
                j = 9;
                i++;
            }
            sheet.getRow(19).getCell(10).setCellValue(sum);
            rs = db.executeQuery(blanksAvaliableAtEndoF);
            i = 3;
            j = 11;
            sum = 0;
            while (rs.next()) {
                min_blankID = rs.getLong(1);
                max_blankID = rs.getLong(2);
                amount = rs.getInt(3);
                sum += amount;
                System.out.println();
                System.out.println(i + "" + j);
                sheet.getRow(i).getCell(j).setCellValue(min_blankID + "-" + max_blankID);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(amount);
                j = 11;
                i++;
            }
            sheet.getRow(19).getCell(12).setCellValue(sum);
            rs = db.executeQuery(blanksAssignedAtTheEndOf);
            i = 3;
            j = 13;
            sum = 0;
            while (rs.next()) {
                queryStaffID = rs.getInt(1);
                min_blankID = rs.getLong(2);
                max_blankID = rs.getLong(3);
                amount = rs.getInt(4);
                sum += amount;
                System.out.println();
                System.out.println(i + "" + j);
                sheet.getRow(i).getCell(j).setCellValue(queryStaffID);
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

    /** Template for sales report, not used*/
    public void generateSalesReport() {
        workbook = new XSSFWorkbook();

        sheet = workbook.createSheet("Interline Report");

        for (int i = 0; i < 14; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 21; j++) {
                cell = row.createCell(j);
//                cell.setCellValue("Cell " + i + "," + j);

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
    /**
     Generates an individual interline sales report in an Excel spreadsheet. The report includes information such as
     the number of blanks sold, base price in GBP and USD, conversion rate, taxes, tax rate, payment type (cash or card),
     card number, commission rate, commission sum, and total amounts paid. The data is retrieved from the database
     and the report is created using Apache POI library. The generated spreadsheet contains a header row, a row for each
     sale, and a row for totals. The totals row includes the sum of the base price, taxes, commissions, and total amounts
     paid for all sales in the report period.
     staffID the ID of the staff member whose sales should be included in the report
     dateFrom the starting date of the report period (inclusive)
     dateTo the ending date of the report period (inclusive)
     */

    public void individualInterlineSalesReport() {
        workbook = new XSSFWorkbook();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        CellStyle cstyle = workbook.createCellStyle();
        cstyle.setFont(font);
        cstyle.setAlignment(HorizontalAlignment.CENTER);
        // create a new sheet
        sheet = workbook.createSheet("Sheet1");

        // create a header row
        headerRow = sheet.createRow(0);
        String[] columnTitles = {"NO. ", "BLANKS", "BASE PRICE GBP", "IN-USD", "CONVERSION RATE", "TAXES", "TAX RATE",
                "CASH", "CARD NUMBER", "COMMISSION RATE", "COMMISSION SUM", "TOTAL AMOUNTS PAID"};
        String sql = "SELECT \n" +
                "  GROUP_CONCAT(b.blankID) as blankIDs, \n" +
                "  s.staffID, \n" +
                "  s.price, \n" +
                "  s.currency, \n" +
                "  s.conversionRate, \n" +
                "  s.paymentType, \n" +//////////////////////////////////////////////////////////////////////////////////////////////////////
                "  s.cardNumber, \n" +
                "  s.commissionsum, \n" +
                "  s.taxSum,  \n" +
                "  COUNT(b.blankID) as num_blanks\n" +
                "FROM sale s\n" +
                "LEFT JOIN soldBlanks b ON s.saleID = b.saleID\n" +
                "WHERE \n" +
                "  s.staffID = '" + staffID + "' AND \n" +
                "SUBSTR(blankID, 1, 1) = '4' AND" +
                "  s.date BETWEEN '" + datefrom + "' AND '" + dateTo + "'\n" +
                "GROUP BY s.saleID\n" +
                "HAVING num_blanks > 1 OR num_blanks = 1;";
        DBConnect db = new DBConnect();
        ResultSet rs;
        try {
            db.connect();
            rs = db.executeQuery(sql);

//
            rs.last();
            int lastRow = rs.getRow();

            for (int i = 0; i < lastRow + 2; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < columnTitles.length; j++) {
                    cell = row.createCell(j);
//                    cell.setCellValue("Cell " + i + "," + j);
                    // Set center alignment and autosize the column width
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    cell.setCellStyle(cellStyle);
                    sheet.autoSizeColumn(j);
                }
            }
            for (int j = 0; j < columnTitles.length; j++) {
                sheet.getRow(0).getCell(j).setCellValue(columnTitles[j]);
            }
            int i = 1;
            int j = 0;
            int taxSumm = 0;
            int priceSum = 0;
            double basePriceSum = 0;
            double commisionsSum = 0;
            int count = 1;
            double priceSUMUSD = 0;
            double price = 0;
            rs.beforeFirst();

            while (rs.next()) {

                blankID = rs.getString(1);
                price = rs.getDouble(3);
                currency = rs.getString(4);
                conversionRate = rs.getDouble(5);
                cash = rs.getString(6);
                card = rs.getString(7);
                commissionSum = rs.getDouble(8);
                taxSum = rs.getDouble(9);
                taxSumm += taxSum;
                priceSum += price;
                basePriceSum += price - taxSum;
                commisionsSum += commissionSum;
                sheet.getRow(i).getCell(j).setCellValue(i);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(blankID + "\n");
                j++;
                sheet.getRow(i).getCell(j).setCellValue(price - taxSum);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(price/conversionRate);
                priceSUMUSD += price/conversionRate;
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
                sheet.getRow(i).getCell(j).setCellValue(commissionSum / price / 0.8);
                j++;
                sheet.getRow(i).getCell(j).setCellValue((commissionSum));
                j++;
                sheet.getRow(i).getCell(j).setCellValue(price);

                j = 0;
                i++;

            }
            for (int k = 0; k < columnTitles.length; k++) {
                sheet.autoSizeColumn(j);
            }

            sheet.getRow(lastRow + 1).getCell(0).setCellValue("TOTALS ");
            sheet.getRow(lastRow + 1).getCell(11).setCellValue(priceSum);
            sheet.getRow(lastRow + 1).getCell(5).setCellValue(taxSumm);
            sheet.getRow(lastRow + 1).getCell(2).setCellValue(basePriceSum);
            sheet.getRow(lastRow + 1).getCell(10).setCellValue(commisionsSum);
            sheet.getRow(lastRow+1).getCell(3).setCellValue(priceSUMUSD);

            ///////////
            sheet.createRow(lastRow + 3).createCell(3);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 3, lastRow + 3, 3, 4)); // StaffID
            sheet.getRow(lastRow + 3).getCell(3).setCellValue("StaffID  :  " + staffID);

            sheet.getRow(lastRow + 3).createCell(5);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 3, lastRow + 3, 5, 7)); // Date From
            sheet.getRow(lastRow + 3).getCell(5).setCellValue("Date From   :  " + outputFormat.format(dateFromDate));

            sheet.createRow(lastRow + 4).createCell(5);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 4, lastRow + 4, 5, 7)); // Date To
            sheet.getRow(lastRow + 4).getCell(5).setCellValue("Date to   :  " + outputFormat.format(dateToDate));

            sheet.getRow(lastRow + 4).createCell(3);
            sheet.getRow(lastRow + 4).getCell(3).setCellValue("Date :" + outputFormat.format(todaysDate));

            sheet.getRow(lastRow + 3).createCell(9);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 3, lastRow + 3, 9, 11)); // TOTAL COMMISSION AMOUNTS
            sheet.getRow(lastRow + 3).getCell(9).setCellValue("TOTAL COMMISSION AMOUNTS  :  " + (commisionsSum));

            sheet.getRow(lastRow + 4).createCell(9);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 4, lastRow + 4, 9, 11)); // NET AMOUNTS FOR AGENTS DEBIT
            sheet.getRow(lastRow + 4).getCell(9).setCellValue("NET AMOUNTS FOR AGEMTS DEBIT  :  " + (basePriceSum - commisionsSum));

            sheet.createRow(lastRow + 5).createCell(9);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 5, lastRow + 5, 9, 11)); // TOTAL BANK REMMITTANCE TO AIRVIA
            sheet.getRow(lastRow + 5).getCell(9).setCellValue("TOTAL BANK REMMITTANCE TO AIRVIA  :  " + (priceSum - commisionsSum));

            sheet.createRow(lastRow+7).createCell(5);
            sheet.addMergedRegion(new CellRangeAddress(lastRow+7,lastRow+8, 5,8));
            sheet.getRow(lastRow+7).getCell(5).setCellValue("INDIVIDUAL INTERLINE SALES REPORT");
            sheet.getRow(lastRow+7).getCell(5).setCellStyle(cstyle);


//        sheet.getRow(1).getCell(0).setCellValue("1");

            for (int x = 0; x < columnTitles.length; x++) {
                sheet.autoSizeColumn(x);
            }
            // write the workbook to a file
            try {
                FileOutputStream outputStream = new FileOutputStream("reports/"+staffID+"-"+"IndividualInterline"+datefrom+"-"+dateTo+".xlsx");
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


    /**

     Generates a global interline sales report and saves it as an Excel file.
     The report includes the following data for each staff member:
     Number of sales made
     Staff ID
     Total base price in GBP
     Total tax sum
     Total amount paid in cash
     Total amount paid by card
     Commission earned at 10%, 12%, and 15% rates
     Total amount paid (base price + tax)
     Total commission earned
     The report is generated based on sales data from the database in a specified date range and is saved in an Excel file.
     */

    public void globalInterLineSalesReport() {
        //global interline
        workbook = new XSSFWorkbook();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        CellStyle cstyle = workbook.createCellStyle();
        cstyle.setFont(font);
        cstyle.setAlignment(HorizontalAlignment.CENTER);

        // create a new sheet
        sheet = workbook.createSheet("Sheet1");

        // create a header row
        headerRow = sheet.createRow(0);
        String[] columnTitles = {"NO. ", "NO.OF. S. ", "STAFF ID", "BASE PRICE GBP","IN-USD", "CONVERSION RATE", "TAX SUM", "CASH PAYMENT ", "CARD PAYMENT", "10 % COMMISSION RATE ", " 12 % COMMISSION RATE", " 15 % COMMISSION RATE", "TOTAL PAID", "TOTAL COMMISSION"};
        String setmode = "SET sql_mode = '';";
        String sql3 = "" +
                "SELECT \n" +
                "  SUM(subquery.num_sales) AS total_sales, \n" +
                "  subquery.staffID, \n" +
                "  SUM(subquery.base_pricesum) as total_basesum, \n" +
                "  SUM(subquery.sum_tax) as total_tax,\n" +
                "  SUM(CASE WHEN subquery.paymentType = 'cash' THEN subquery.price_total ELSE 0 END) AS payed_in_cash,\n" +
                "  SUM(CASE WHEN subquery.paymentType = 'card' THEN subquery.price_total ELSE 0 END) AS payed_in_card,\n" +
                "  SUM(CASE WHEN subquery.commission_ratio = 0.5 THEN subquery.commissionSum ELSE 0 END) AS commission_10_percent,\n" +
                "  SUM(CASE WHEN subquery.commission_ratio = 0.15 THEN subquery.commissionSum ELSE 0 END) AS commission_15_percent,\n" +
                "  SUM(CASE WHEN subquery.commission_ratio = 0.2 THEN subquery.commissionSum ELSE 0 END) AS commission_20_percent,\n" +
                "  SUM(subquery.price_total),\n" +
                "  SUM(commissionSum) as total_commission\n" +////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                "FROM (\n" +
                "  SELECT \n" +
                "    COUNT(saleID) AS num_sales, \n" +
                "    staffID, \n" +
                "    SUM(price)-SUM(taxSum) AS base_pricesum, \n" +
                "    SUM(taxSum) AS sum_tax, \n" +
                "    paymentType,\n" +
                "    SUM(commissionSum) AS commissionSum,\n" +
                "    SUM(price) AS price_total,\n" +
                "    SUM(commissionSum)/SUM(price)/0.8 AS commission_ratio\n" +
                "  FROM sale\n" +
                "WHERE sale.saleID IN (\n" +
                "    SELECT distinct saleID FROM soldBlanks WHERE blankID LIKE '4%'\n" +
                "  )" +
                "   AND date >= '" + datefrom + "' AND date <= '" + dateTo + "'" +
                "  GROUP BY  conversionRate,paymentType, commissionSum, staffID\n" +
                ") AS subquery\n" +
                "GROUP BY subquery.staffID; ";

        String sql4 = "SELECT\n" +
                "SUM(subquery.num_sales) AS total_sales,\n" +
                "subquery.staffID,\n" +
                "SUM(subquery.base_pricesum) as total_basesum,\n" +
                "subquery.currency,\n" +
                "subquery.conversionRate,\n" +
                "SUM(subquery.sum_tax) as total_tax,\n" +
                "SUM(CASE WHEN subquery.paymentType = 'cash' THEN subquery.price_total ELSE 0 END) AS payed_in_cash,\n" +
                "SUM(CASE WHEN subquery.paymentType = 'card' THEN subquery.price_total ELSE 0 END) AS payed_in_card,\n" +
                "SUM(CASE WHEN subquery.commission_ratio = 0.1 THEN subquery.commissionSum ELSE 0 END) AS commission_10_percent,\n" +
                "SUM(CASE WHEN subquery.commission_ratio = 0.12 THEN subquery.commissionSum ELSE 0 END) AS commission_12_percent,\n" +///////////////////////////////////////////////////////
                "SUM(CASE WHEN subquery.commission_ratio = 0.15 THEN subquery.commissionSum ELSE 0 END) AS commission_15_percent,\n" +
                "SUM(subquery.price_total),\n" +
                "SUM(commissionSum) as total_commission\n" +
                "FROM (\n" +
                "  SELECT \n" +
                "    COUNT(saleID) AS num_sales, \n" +
                "    staffID,\n" +
                "    currency,\n" +
                "    conversionRate,\n" +
                "    SUM(price)-SUM(taxSum) AS base_pricesum,\n" +
                "    SUM(taxSum) AS sum_tax,\n" +
                "    paymentType,\n" +
                "    SUM(commissionSum) AS commissionSum,\n" +
                "    SUM(price) AS price_total,\n" +
                "    SUM(commissionSum)/SUM(price)/0.8 AS commission_ratio\n" +
                "  FROM sale\n" +
                "WHERE sale.saleID IN (\n" +
                "    SELECT distinct saleID FROM soldBlanks WHERE blankID LIKE '4%'\n" +////////////////////////////////////////////
                "  )\n" +
                "   AND date >= "+datefrom+" AND date <= "+dateTo+"" +
                "  GROUP BY  conversionRate,paymentType, commissionSum, staffID\n" +
                ") AS subquery\n" +
                "GROUP BY subquery.staffID; ";
        System.out.println(datefrom);
        System.out.println(dateTo);
        DBConnect db = new DBConnect();
        ResultSet rs;
        try {
            db.connect();
            PreparedStatement stmt = db.getConnection().prepareStatement(setmode);
//             PreparedStatement querys = db.connection.prepareStatement(sql3);
//            String query = querys.toString();
//            System.out.println("Executing query: " + query);
//             stmt.executeUpdate();
//             querys.executeUpdate();
//             String query = db.statement.executeQuery(sql3);
//            System.out.println(query);
            rs = db.executeQuery(sql4);


            rs.last();
            int lastRow = rs.getRow();
            System.out.println(lastRow);
            for (int i = 0; i < lastRow + 2; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < columnTitles.length; j++) {
                    cell = row.createCell(j);
//                    cell.setCellValue("Cell " + i + "," + j);
                    // Set center alignment and autosize the column width
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    cell.setCellStyle(cellStyle);
                    sheet.autoSizeColumn(j);
                }
            }
            for (int j = 0; j < columnTitles.length; j++) {
                sheet.getRow(0).getCell(j).setCellValue(columnTitles[j]);
            }
            int i = 1;
            int j = 0;
            int taxSumm = 0;
            int priceSum = 0;
            int basePriceSum = 0;
            double commissionsSum = 0;
            double priceSUMUSD =0;
            int count = 1;
            rs.beforeFirst();
            while (rs.next()) {
                int numberOfSales = rs.getInt(1);
                queryStaffID = rs.getInt(2);
                System.out.println(queryStaffID);
                Double base_price = rs.getDouble(3);
                System.out.println(base_price);
                currency = rs.getString(4);
                System.out.println(taxSum);
                conversionRate = rs.getDouble(5);
                System.out.println(conversionRate);
                taxSum = rs.getInt(6);
                System.out.println(taxSum);
                int payedCash = rs.getInt(7);
                System.out.println(payedCash);
                int payedCard = rs.getInt(8);
                System.out.println(payedCard);
                Double commission10 = rs.getDouble(9);
                System.out.println(commission10);
                Double commission15 = rs.getDouble(10);
                System.out.println(commission15);
                Double commission20 = rs.getDouble(11);
                System.out.println(commission20);
                Double totalPaid = rs.getDouble(12);
                System.out.println(totalPaid);
                Double totalCommission = rs.getDouble(13);
                System.out.println(totalCommission);
                taxSumm += taxSum;
                priceSum += payedCash + payedCard;
                basePriceSum += base_price;
                sheet.getRow(i).getCell(j).setCellValue(i);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(numberOfSales);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(queryStaffID + "\n");
                j++;
                sheet.getRow(i).getCell(j).setCellValue(base_price);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(base_price/conversionRate);
                priceSUMUSD += base_price/conversionRate;
                j++;
                sheet.getRow(i).getCell(j).setCellValue(conversionRate);/////////////////////////////////////////////////////
                j++;
                sheet.getRow(i).getCell(j).setCellValue(taxSum);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(payedCash);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(payedCard);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(commission10);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(commission15);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(commission20);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(totalPaid);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(totalCommission);
                commissionsSum+=totalCommission;
                j++;
                j = 0;
                i++;

            }
            for (int k = 0; k < columnTitles.length; k++) {
                sheet.autoSizeColumn(j);
            }

            sheet.getRow(lastRow + 1).getCell(0).setCellValue("TOTALS ");
            sheet.getRow(lastRow + 1).getCell(3).setCellValue(basePriceSum);
            sheet.getRow(lastRow + 1).getCell(6).setCellValue(taxSumm);
            sheet.getRow(lastRow + 1).getCell(12).setCellValue(priceSum);////////////////////////////////////////////////////////////////////////////////////////////////////
            sheet.getRow(lastRow+1).getCell(4).setCellValue(priceSUMUSD);
            sheet.getRow(lastRow + 1).getCell(13).setCellValue(commissionsSum);

            ///
            sheet.createRow(lastRow + 3).createCell(3);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 3, lastRow + 3, 3, 4)); // StaffID


            sheet.getRow(lastRow + 3).createCell(5);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 3, lastRow + 3, 5, 7)); // Date From
            sheet.getRow(lastRow + 3).getCell(5).setCellValue("Date From   :  " + outputFormat.format(dateFromDate));

            sheet.createRow(lastRow + 4).createCell(5);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 4, lastRow + 4, 5, 7)); // Date To
            sheet.getRow(lastRow + 4).getCell(5).setCellValue("Date to   :  " + outputFormat.format(dateToDate));

            sheet.getRow(lastRow + 4).createCell(3);
            sheet.getRow(lastRow + 4).getCell(3).setCellValue("Date :" + outputFormat.format(todaysDate));

            sheet.getRow(lastRow + 3).createCell(9);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 3, lastRow + 3, 9, 11)); // TOTAL COMMISSION AMOUNTS
            sheet.getRow(lastRow + 3).getCell(9).setCellValue("TOTAL COMMISSION AMOUNTS  :  " + (commissionsSum));

            sheet.getRow(lastRow + 4).createCell(9);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 4, lastRow + 4, 9, 11)); // NET AMOUNTS FOR AGENTS DEBIT
            sheet.getRow(lastRow + 4).getCell(9).setCellValue("NET AMOUNTS FOR AGEMTS DEBIT  :  " + (basePriceSum - commissionsSum));

            sheet.createRow(lastRow + 5).createCell(9);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 5, lastRow + 5, 9, 11)); // TOTAL BANK REMMITTANCE TO AIRVIA
            sheet.getRow(lastRow + 5).getCell(9).setCellValue("TOTAL BANK REMMITTANCE TO AIRVIA  :  " + (priceSum - commissionsSum));

            ///
            sheet.createRow(lastRow+7).createCell(5);
            sheet.addMergedRegion(new CellRangeAddress(lastRow+7,lastRow+8, 5,8));
            sheet.getRow(lastRow+7).getCell(5).setCellValue("GLOBAL INTERLINE SALES REPORT");
            sheet.getRow(lastRow+7).getCell(5).setCellStyle(cstyle);
//        sheet.getRow(1).getCell(0).setCellValue("1");

            for (int x = 0; x < columnTitles.length; x++) {
                sheet.autoSizeColumn(x);
            }
            // write the workbook to a file
            try {
                FileOutputStream outputStream = new FileOutputStream("reports/GlobalInterline"+datefrom+"-"+dateTo+".xlsx");
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

    /**

     Generates a report for individual domestic sales, and saves it as a new Excel file that includes the staffID and the date range.
     The report includes information such as the staff ID, blank IDs, base price, taxes, cash and card payments,
     and commission rates and sums.
     The report is based on a SQL query that retrieves sales data from a database. The query includes filters for the staff ID,
     date range.
     Data rows correspond to values for each sale.
     @throws SQLException if there is an error executing the SQL query.
     */

    public void domesticIndividualReport() {
        workbook = new XSSFWorkbook();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        CellStyle cstyle = workbook.createCellStyle();
        cstyle.setFont(font);
        cstyle.setAlignment(HorizontalAlignment.CENTER);


        // create a new sheet
        sheet = workbook.createSheet("Sheet1");

        // create a header row
        headerRow = sheet.createRow(0);
        String[] columnTitles = {"NO. ", "BLANKS", "BASE PRICE USD", "PRICE CURRNECY", "CONVERSION RATE", "TAXES", "TAX RATE",
                "CASH", "CARD NUMBER", "COMMISSION RATE", "COMMISSION SUM", "TOTAL AMOUNTS PAID"};
        String sql = "SELECT \n" +
                "  GROUP_CONCAT(b.blankID) as blankIDs, \n" +
                "  s.staffID, \n" +
                "  s.price, \n" +
                "  s.currency, \n" +
                "  s.conversionRate, \n" +
                "  s.paymentType, \n" +
                "  s.cardNumber, \n" +//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                "  s.commissionsum, \n" +
                "  s.taxSum,  \n" +
                "  COUNT(b.blankID) as num_blanks\n" +
                "FROM sale s\n" +
                "LEFT JOIN soldBlanks b ON s.saleID = b.saleID\n" +
                "WHERE \n" +
                "  s.staffID = '" + staffID + "' AND \n" +
                "SUBSTR(blankID, 1, 1) = '2' or SUBSTR(blankID, 1, 1) = '1' AND" +
                "  s.date BETWEEN '" + datefrom + "' AND '" + dateTo + "'\n" +
                "GROUP BY s.saleID\n" +
                "HAVING num_blanks > 1 OR num_blanks = 1;";
        DBConnect db = new DBConnect();
        ResultSet rs;
        try {
            db.connect();
            rs = db.executeQuery(sql);

//
            rs.last();
            int lastRow = rs.getRow();

            for (int i = 0; i < lastRow + 2; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < columnTitles.length; j++) {
                    cell = row.createCell(j);
//                    cell.setCellValue("Cell " + i + "," + j);
                    // Set center alignment and autosize the column width
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    cell.setCellStyle(cellStyle);
                    sheet.autoSizeColumn(j);
                }
            }
            for (int j = 0; j < columnTitles.length; j++) {
                sheet.getRow(0).getCell(j).setCellValue(columnTitles[j]);
            }
            int i = 1;
            int j = 0;
            int taxSumm = 0;
            int priceSum = 0;
            int basePriceSum = 0;
            double commisionsSum = 0;
            int count = 1;

            rs.beforeFirst();
            while (rs.next()) {
                blankID = rs.getString(1);
                price = rs.getInt(3);
                currency = rs.getString(4);
                conversionRate = rs.getDouble(5);
                cash = rs.getString(6);
                card = rs.getString(7);
                commissionSum = rs.getDouble(8);
                taxSum = rs.getDouble(9);
                taxSumm += taxSum;
                priceSum += price;
                basePriceSum += price - taxSum;
                commisionsSum += commissionSum;
                sheet.getRow(i).getCell(j).setCellValue(i);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(blankID + "\n");
                j++;
                sheet.getRow(i).getCell(j).setCellValue(price - taxSum);
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
                sheet.getRow(i).getCell(j).setCellValue(commissionSum / price / 0.8);
                j++;
                sheet.getRow(i).getCell(j).setCellValue((commissionSum));
                j++;
                sheet.getRow(i).getCell(j).setCellValue(price);

                j = 0;
                i++;

            }
            for (int k = 0; k < columnTitles.length; k++) {
                sheet.autoSizeColumn(j);
            }

            sheet.getRow(lastRow + 1).getCell(0).setCellValue("TOTALS ");///////////////////////////////////////////////////////////////////////////////////////////////////////
            sheet.getRow(lastRow + 1).getCell(11).setCellValue(priceSum);
            sheet.getRow(lastRow + 1).getCell(2).setCellValue(basePriceSum);
            sheet.getRow(lastRow + 1).getCell(10).setCellValue(commisionsSum);

            ///////////
            sheet.createRow(lastRow + 3).createCell(3);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 3, lastRow + 3, 3, 4)); // StaffID
            sheet.getRow(lastRow + 3).getCell(3).setCellValue("StaffID  :  " + staffID);

            sheet.getRow(lastRow + 3).createCell(5);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 3, lastRow + 3, 5, 7)); // Date From
            sheet.getRow(lastRow + 3).getCell(5).setCellValue("Date From   :  " + outputFormat.format(dateFromDate));

            sheet.createRow(lastRow + 4).createCell(5);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 4, lastRow + 4, 5, 7)); // Date To
            sheet.getRow(lastRow + 4).getCell(5).setCellValue("Date to   :  " + outputFormat.format(dateToDate));

            sheet.getRow(lastRow + 4).createCell(3);
            sheet.getRow(lastRow + 4).getCell(3).setCellValue("Date :" + outputFormat.format(todaysDate));

            sheet.getRow(lastRow + 3).createCell(9);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 3, lastRow + 3, 9, 11)); // TOTAL COMMISSION AMOUNTS
            sheet.getRow(lastRow + 3).getCell(9).setCellValue("TOTAL COMMISSION AMOUNTS  :  " + (commisionsSum));

            sheet.getRow(lastRow + 4).createCell(9);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 4, lastRow + 4, 9, 11)); // NET AMOUNTS FOR AGENTS DEBIT
            sheet.getRow(lastRow + 4).getCell(9).setCellValue("NET AMOUNTS FOR AGEMTS DEBIT  :  " + (basePriceSum - commisionsSum));

            sheet.createRow(lastRow + 5).createCell(9);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 5, lastRow + 5, 9, 11)); // TOTAL BANK REMMITTANCE TO AIRVIA
            sheet.getRow(lastRow + 5).getCell(9).setCellValue("TOTAL BANK REMMITTANCE TO AIRVIA  :  " + (priceSum - commisionsSum));


//        sheet.getRow(1).getCell(0).setCellValue("1");
            sheet.createRow(lastRow+7).createCell(5);
            sheet.addMergedRegion(new CellRangeAddress(lastRow+7,lastRow+8, 5,8));
            sheet.getRow(lastRow+7).getCell(5).setCellValue("INDIVIDUAL DOMESTIC SALES REPORT");
            sheet.getRow(lastRow+7).getCell(5).setCellStyle(cstyle);

            for (int x = 0; x < columnTitles.length; x++) {
                sheet.autoSizeColumn(x);
            }
            // write the workbook to a file
            try {
                FileOutputStream outputStream = new FileOutputStream("reports/"+staffID+"-"+"IndividualDomestic"+datefrom+"-"+dateTo+".xlsx");
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

    /**

     Generates a global domestic sales report and saves it as an Excel file.
     The report includes the following data for each staff member:
     Number of sales made
     Staff ID
     Total base price in GBP
     Total tax sum
     Total amount paid in cash
     Total amount paid by card
     Commission earned at 5% and 9% rates
     Total amount paid (base price + tax)
     Total commission earned
     The report is generated based on sales data from the database in a specified date range and is saved in an Excel file.
     */
    public void globalDomesticSalesReport() {
        //global interline
        workbook = new XSSFWorkbook();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        CellStyle cstyle = workbook.createCellStyle();
        cstyle.setFont(font);
        cstyle.setAlignment(HorizontalAlignment.CENTER);

        // create a new sheet
        sheet = workbook.createSheet("Sheet1");

        // create a header row
        headerRow = sheet.createRow(0);
        String[] columnTitles = {"NO. ", "NO.OF. S. ", "STAFF ID", "BASE PRICE USD", "TAX SUM", "CASH PAYMENT ", "CARD PAYMENT", "5 % COMMISSION RATE ", " 9 % COMMISSION RATE", "TOTAL PAID", "TOTAL COMMISSION"};
        String setmode = "SET sql_mode = '';";
        String sql3 = "" +
                "SELECT \n" +
                "  SUM(subquery.num_sales) AS total_sales, \n" +// sum the number of sales from subquery
                "  subquery.staffID, \n" +
                "  SUM(subquery.base_pricesum) as total_basesum, \n" +
                "  SUM(subquery.sum_tax) as total_tax,\n" +
                "  SUM(CASE WHEN subquery.paymentType = 'cash' THEN subquery.price_total ELSE 0 END) AS payed_in_cash,\n" +
                "  SUM(CASE WHEN subquery.paymentType = 'card' THEN subquery.price_total ELSE 0 END) AS payed_in_card,\n" +////////////////////////////////////////////////////////////////////////////////////
                "  SUM(CASE WHEN subquery.commission_ratio = 0.05 THEN subquery.commissionSum ELSE 0 END) AS commission_5_percent,\n" +
                "  SUM(CASE WHEN subquery.commission_ratio = 0.09 THEN subquery.commissionSum ELSE 0 END) AS commission_9_percent,\n" +
                "  SUM(subquery.price_total),\n" +
                "  SUM(commissionSum) as total_commission\n" +
                "FROM (\n" +
                "  SELECT \n" +
                "    COUNT(saleID) AS num_sales, \n" +
                "    staffID, \n" +
                "    SUM(price)-SUM(taxSum) AS base_pricesum, \n" +
                "    SUM(taxSum) AS sum_tax, \n" +
                "    paymentType,\n" +
                "    SUM(commissionSum) AS commissionSum,\n" +
                "    SUM(price) AS price_total,\n" +//////////////////////////////////////////////////////////////////////////////////////////////////////////
                "    SUM(commissionSum)/SUM(price)/0.8 AS commission_ratio\n" +
                "  FROM sale\n" +
                "WHERE sale.saleID IN (\n" +
                "    SELECT distinct saleID FROM soldBlanks WHERE blankID LIKE '2%' or blankID LIKE '1%'\n" +
                "  )" +
                "   AND date >= '" + datefrom + "' AND date <= '" + dateTo + "'" +
                "  GROUP BY  conversionRate,paymentType, commissionSum, staffID\n" +
                ") AS subquery\n" +
                "GROUP BY subquery.staffID; ";
        System.out.println(datefrom);
        System.out.println(dateTo);
        DBConnect db = new DBConnect();
        ResultSet rs;
        try {
            db.connect();
            PreparedStatement stmt = db.getConnection().prepareStatement(setmode);
            stmt.executeUpdate();
            rs = db.executeQuery(sql3);


            rs.last();
            int lastRow = rs.getRow();
            System.out.println(lastRow);
            for (int i = 0; i < lastRow + 2; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < columnTitles.length; j++) {
                    cell = row.createCell(j);
//                    cell.setCellValue("Cell " + i + "," + j);
                    // Set center alignment and autosize the column width
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    cell.setCellStyle(cellStyle);
                    sheet.autoSizeColumn(j);
                }
            }
            for (int j = 0; j < columnTitles.length; j++) {
                sheet.getRow(0).getCell(j).setCellValue(columnTitles[j]);
            }
            int i = 1;
            int j = 0;
            int taxSumm = 0;
            int priceSum = 0;
            int basePriceSum = 0;
            double commissionsSum = 0;
            int count = 1;
            rs.beforeFirst();
            while (rs.next()) {
                int numberOfSales = rs.getInt(1);
                queryStaffID = rs.getInt(2);
                System.out.println(queryStaffID);
                Double base_price = rs.getDouble(3);
                System.out.println(base_price);
                taxSum = rs.getDouble(4);
                System.out.println(taxSum);
                int payedCash = rs.getInt(5);
                System.out.println(payedCash);
                int payedCard = rs.getInt(6);
                System.out.println(payedCard);
                Double commission10 = rs.getDouble(7);
                System.out.println(commission10);
                Double commission15 = rs.getDouble(8);
                System.out.println(commission15);
                Double totalPaid = rs.getDouble(9);
                System.out.println(totalPaid);
                Double totalCommission = rs.getDouble(10);
                System.out.println(totalCommission);
                taxSumm += taxSum;
                priceSum += payedCash + payedCard;
                basePriceSum += base_price;
                commissionsSum += commission10 + commission15;
                sheet.getRow(i).getCell(j).setCellValue(i);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(numberOfSales);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(queryStaffID + "\n");
                j++;
                sheet.getRow(i).getCell(j).setCellValue(base_price);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(taxSum);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(payedCash);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(payedCard);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(commission10);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(commission15);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(totalPaid);
                j++;
                sheet.getRow(i).getCell(j).setCellValue(totalCommission);
                j++;
                j = 0;
                i++;

            }
            for (int k = 0; k < columnTitles.length; k++) {
                sheet.autoSizeColumn(j);
            }

            sheet.getRow(lastRow + 1).getCell(0).setCellValue("TOTALS ");
            sheet.getRow(lastRow + 1).getCell(3).setCellValue(basePriceSum);
            sheet.getRow(lastRow + 1).getCell(4).setCellValue(taxSumm);
            sheet.getRow(lastRow + 1).getCell(9).setCellValue(priceSum);
            sheet.getRow(lastRow + 1).getCell(10).setCellValue(commissionsSum);

            ///
            sheet.createRow(lastRow + 3).createCell(3);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 3, lastRow + 3, 3, 4)); // StaffID


            sheet.getRow(lastRow + 3).createCell(5);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 3, lastRow + 3, 5, 7)); // Date From
            sheet.getRow(lastRow + 3).getCell(5).setCellValue("Date From   :  " + outputFormat.format(dateFromDate));

            sheet.createRow(lastRow + 4).createCell(5);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 4, lastRow + 4, 5, 7)); // Date To
            sheet.getRow(lastRow + 4).getCell(5).setCellValue("Date to   :  " + outputFormat.format(dateToDate));

            sheet.getRow(lastRow + 4).createCell(3);
            sheet.getRow(lastRow + 4).getCell(3).setCellValue("Date :" + outputFormat.format(todaysDate));

            sheet.getRow(lastRow + 3).createCell(9);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 3, lastRow + 3, 9, 11)); // TOTAL COMMISSION AMOUNTS
            sheet.getRow(lastRow + 3).getCell(9).setCellValue("TOTAL COMMISSION AMOUNTS  :  " + (commissionsSum));

            sheet.getRow(lastRow + 4).createCell(9);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 4, lastRow + 4, 9, 11)); // NET AMOUNTS FOR AGENTS DEBIT
            sheet.getRow(lastRow + 4).getCell(9).setCellValue("NET AMOUNTS FOR AGEMTS DEBIT  :  " + (basePriceSum - commissionsSum));

            sheet.createRow(lastRow + 5).createCell(9);
            sheet.addMergedRegion(new CellRangeAddress(lastRow + 5, lastRow + 5, 9, 11)); // TOTAL BANK REMMITTANCE TO AIRVIA
            sheet.getRow(lastRow + 5).getCell(9).setCellValue("TOTAL BANK REMMITTANCE TO AIRVIA  :  " + (priceSum - commissionsSum));

            ///
//        sheet.getRow(1).getCell(0).setCellValue("1");
            sheet.createRow(lastRow+7).createCell(5);
            sheet.addMergedRegion(new CellRangeAddress(lastRow+7,lastRow+8, 5,8));
            sheet.getRow(lastRow+7).getCell(5).setCellValue("GLOBAL DOMESTIC SALES REPORT");
            sheet.getRow(lastRow+7).getCell(5).setCellStyle(cstyle);

            for (int x = 0; x < columnTitles.length; x++) {
                sheet.autoSizeColumn(x);
            }
            // write the workbook to a file
            try {
                FileOutputStream outputStream = new FileOutputStream("reports/GlobalDomestic"+datefrom+"-"+dateTo+".xlsx");
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

//    public void generateInterlineSalesReportTemplatePerAdvisor() {
//        workbook = new XSSFWorkbook();
//
//        // create a new sheet
//        sheet = workbook.createSheet("Sheet1");
//
//        // create a header row
//        headerRow = sheet.createRow(0);
//
//        // add column titles to header row
//        String[] columnTitles = {"NO. ", "BLANKS", "PRICE USD", "PRICE CURRNECY", "CONVERSION RATE", "TAXES", "TAX RATE",
//                "CASH", "CARD NUMBER", "COMMISSION RATE", "COMMISSION SUM", "TOTAL AMOUNTS PAID"};
////        int rowCount = 10;
////        int columnCount = columnTitles.length;
////        Row firstRow = sheet.createRow(0);
////        for(int j = 0; j < columnCount; j++) {
////            Cell cell = firstRow.createCell(j);
////            cell.setCellValue(columnTitles[j]);
////        }
////        for(int i = 1; i < rowCount; i++) {
////            Row row = sheet.createRow(i);
////            for(int j = 0; j < columnCount; j++) {
////                Cell cell = row.createCell(j);
////                cell.setCellValue("Cell " + j + "," + i);
////            }
////        }/////////////
//        for (int i = 0; i < 10; i++) {
//            row = sheet.createRow(i);
//            for (int j = 0; j < columnTitles.length; j++) {
//                cell = row.createCell(j);
//                cell.setCellValue("Cell " + i + "," + j);
//                // Set center alignment and autosize the column width
//                CellStyle cellStyle = workbook.createCellStyle();
//                cellStyle.setAlignment(HorizontalAlignment.CENTER);
//                cell.setCellStyle(cellStyle);
//                sheet.autoSizeColumn(j);
//            }
//        }
//        for (int j = 0; j < columnTitles.length; j++) {
//            sheet.getRow(0).getCell(j).setCellValue(columnTitles[j]);
//        }
//
//        /////////////
//        for (int j = 0; j < columnTitles.length; j++) {
//            sheet.autoSizeColumn(j);
//        }
//        sheet.getRow(9).getCell(0).setCellValue("TOTALS ");
//
//        sheet.createRow(11).createCell(9);
//        sheet.addMergedRegion(new CellRangeAddress(11, 11, 9, 10));//COMMISSIONS
//        sheet.getRow(11).getCell(9).setCellValue("TOTAL COMMISSION AMOUNTS");
//
//        sheet.createRow(12).createCell(9);
//        sheet.addMergedRegion(new CellRangeAddress(12, 12, 9, 10));//COMMISSIONS
//        sheet.getRow(12).getCell(9).setCellValue("NET AMOUNTS FOR AGEMTS DEBIT");
//
//        sheet.createRow(13).createCell(9);
//        sheet.addMergedRegion(new CellRangeAddress(13, 13, 9, 10));//COMMISSIONS
//        sheet.getRow(13).getCell(9).setCellValue("TOTAL BANK REMMITTANCE TO AIRVIA");
//
////        sheet.getRow(1).getCell(0).setCellValue("1");
//
//
////        populateSalesReport(sheet);
//        for (int j = 0; j < columnTitles.length; j++) {
//            sheet.autoSizeColumn(j);
//        }
//        // write the workbook to a file
//        try {
//            FileOutputStream outputStream = new FileOutputStream("excel-sheet.xlsx");
//            workbook.write(outputStream);
//            workbook.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
}






