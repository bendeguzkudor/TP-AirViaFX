package com.example.tpairviafx;


    

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


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

//        report.createTicketStockTurnOverReport();


  }
  public  void createTicketStockTurnOverReport(){
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
  public  void queryAssignedBlanks(){
      DBConnect db = new DBConnect();
      ResultSet rs;
      String sql = "SELECT staffID, MAX(blankID) - MIN(blankID)+1 as amount, MIN(blankID) as min_blankID, MAX(blankID) as max_blankID\n" +
              "FROM blanks\n" +
              "WHERE staffID != 0\n" +
              "GROUP BY staffID,LEFT(blankID, 3);"; //query for dynamic search
      String sql2 = "SELECT staffID, MAX(blankID) - MIN(blankID) + 1 AS amount, MIN(blankID) AS min_blankID, MAX(blankID) AS max_blankID\n" +
              "FROM blanks\n" +
              "WHERE staffID IS NOT NULL AND sold = 1\n" +
              "GROUP BY staffID, LEFT(blankID, 3);";

      try {
          db.connect();
          System.out.println(sql);
          rs = db.statement.executeQuery(sql);
          int i = 3;
          int j = 6;
          int sum = 0;
          while (rs.next()) {
               staffID = rs.getInt(1);
               amount = rs.getInt(2);
               min_blankID = rs.getLong(3);
               max_blankID = rs.getLong(4);
               sum += amount;
              System.out.print(rs.getInt(1)+ ":");
              System.out.print(rs.getInt(2 )+ ":");
              System.out.print(rs.getLong(3)+ ":");
              System.out.print(rs.getLong(4)+ ":");
              System.out.println("");
              System.out.println(i+""+ j);
              sheet.getRow(i).getCell(j).setCellValue(staffID);
              j++;
              sheet.getRow(i).getCell(j).setCellValue(min_blankID+ "-"+ max_blankID);
              j++;
              sheet.getRow(i).getCell(j).setCellValue(amount);
              j = 6;
              i++;
          }
          sheet.getRow(19).getCell(8).setCellValue(sum);
          rs = db.statement.executeQuery(sql2);
            i =3;
            j=9;
            sum = 0;
          while(rs.next()){
                  staffID = rs.getInt(1);
                  amount = rs.getInt(2);
                  min_blankID = rs.getLong(3);
                  max_blankID = rs.getLong(4);
                  sum += amount;
                  System.out.print(rs.getInt(1)+ ":");
                  System.out.print(rs.getInt(2 )+ ":");
                  System.out.print(rs.getLong(3)+ ":");
                  System.out.print(rs.getLong(4)+ ":");
                  System.out.println("");
                  System.out.println(i+""+ j);
                  sheet.getRow(i).getCell(j).setCellValue(min_blankID+ "-"+ max_blankID);
                  j++;
                  sheet.getRow(i).getCell(j).setCellValue(amount);
                  j = 9;
                  i++;
          }
          sheet.getRow(19).getCell(10).setCellValue(sum);

        }catch (SQLException e){
          e.printStackTrace();
      }
    }
    }





