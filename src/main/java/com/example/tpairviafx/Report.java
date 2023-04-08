package com.example.tpairviafx;


import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;


public class Report {
    Integer staffID = null;
    Integer amount = null;
    Long max_blankID = null;
    Long min_blankID = null;
    Cell cell;
    Row row;
    Workbook workbook;
    Sheet sheet;

    public static void main(String[] args) throws Exception {
        Report report = new Report();
//        report.queryAssignedBlanks();
        report.createTicketStockTurnOverReport();
//        report.generateSalesReport();

//        report.createTicketStockTurnOverReport();


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
        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFSheet sheet = workbook.createSheet("Interline Report");

        for (int i = 0; i < 20; i++) {
            XSSFRow row = sheet.createRow(i);
            for (int j = 0; j < 27; j++) {
                XSSFCell cell = row.createCell(j);
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
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 6));
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 4, 5));
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 6, 6));

        sheet.getRow(0).getCell(0).setCellValue("NN");
        sheet.getRow(0).getCell(1).setCellValue("AIR VIA DOCUMENTS");
        sheet.getRow(1).getCell(1).setCellValue("ADVISOR NUMBER");
        sheet.getRow(1).getCell(2).setCellValue("DOC NMBRS ACPNS");
        sheet.getRow(1).getCell(3).setCellValue("FARE AMOUNT");
        sheet.getRow(1).getCell(4).setCellValue("TAXES");
        sheet.getRow(2).getCell(4).setCellValue("LZ");
        sheet.getRow(2).getCell(5).setCellValue("OTHERS");
        sheet.getRow(1).getCell(6).setCellValue("TOTAL DOCUMENT'S AMOUNT");

        //ISSUED IN EXCHANGE FOR DOCUMENTS OF:
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 7, 14));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 7, 10));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 14));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 9, 10));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 13, 14));

        sheet.getRow(0).getCell(7).setCellValue("ISSUED IN EXCHANGE FOR DOCUMENTS OF:");
        sheet.getRow(1).getCell(7).setCellValue("AIR VIA");
        sheet.getRow(1).getCell(11).setCellValue("OTHER AIRLINES");
        sheet.getRow(2).getCell(7).setCellValue("DOCS.");
        sheet.getRow(2).getCell(8).setCellValue("FCPNS");
        sheet.getRow(2).getCell(9).setCellValue("PRORATE AMNTS");
        sheet.getRow(2).getCell(11).setCellValue("DOCS.");
        sheet.getRow(2).getCell(12).setCellValue("FCPNS");
        sheet.getRow(2).getCell(13).setCellValue("PRORATE AMNTS");

        //FORMS OF PAYMENT
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 15, 19));
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 15, 15));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 16, 18));
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 19, 19));

        sheet.getRow(0).getCell(15).setCellValue("FORMS OF PAYMENT");
        sheet.getRow(1).getCell(15).setCellValue("CASH");
        sheet.getRow(1).getCell(16).setCellValue("CREDIT CARDS");
        sheet.getRow(2).getCell(16).setCellValue("NMBR");
        sheet.getRow(2).getCell(17).setCellValue("USD");
        sheet.getRow(2).getCell(18).setCellValue("BGL");
        sheet.getRow(1).getCell(19).setCellValue("TOTAL AMOUNTS PAID");

        //COMMISSION
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 20, 25));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 20, 25));
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 26, 26));

        sheet.getRow(0).getCell(20).setCellValue("COMMISSIONS");
        sheet.getRow(1).getCell(20).setCellValue("ASSESSABLE AMOUNTS");
        sheet.getRow(0).getCell(26).setCellValue("NON ASSESS AMOUNTS");

        for (int i = 0; i < 26; i++) {
            sheet.autoSizeColumn(i);

        }
        // Write the workbook to a file
        try (FileOutputStream outputStream = new FileOutputStream("Table9.xlsx")) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}






