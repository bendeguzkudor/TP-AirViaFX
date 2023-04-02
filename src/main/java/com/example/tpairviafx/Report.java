package com.example.tpairviafx;


    

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Report {

    public static void main(String[] args) throws Exception {
        createTicketStockTurnOverReport();


  }
  public static void createTicketStockTurnOverReport(){
      Workbook workbook = new XSSFWorkbook();

      // Create a new sheet
      Sheet sheet = workbook.createSheet("Table");
      for (int i = 0; i < 13; i++) {
          Row row = sheet.createRow(i);
          for (int j = 0; j < 16; j++) {
              Cell cell = row.createCell(j);
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
      sheet.getRow(12).getCell(0).setCellValue("Total: ");
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
      for (int i = 0; i < 16; i++) {
          sheet.autoSizeColumn(i);
      }
      // Write the workbook to a file
      try (FileOutputStream outputStream = new FileOutputStream("Table.xlsx")) {
          workbook.write(outputStream);
      } catch (IOException e) {
          e.printStackTrace();
      }

      System.out.println("Table.xlsx created successfully!");

  }

}




