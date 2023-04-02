package com.example.tpairviafx;


    

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
        import org.apache.logging.log4j.Logger;
        import org.apache.logging.log4j.LogManager;

public class Report {

    public static void main(String[] args) throws Exception {
        // Create a workbook

        Workbook workbook = new XSSFWorkbook();

        // Create a sheet
        Sheet sheet = workbook.createSheet("Column With Sub Columns");

        // Define the header row
        Row headerRow = sheet.createRow(0);

        // Define the main column
        CellStyle categoryCellStyle = workbook.createCellStyle();
        categoryCellStyle.setAlignment(HorizontalAlignment.CENTER);
        categoryCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        categoryCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Cell columnCell = headerRow.createCell(0);
        columnCell.setCellValue("Received Blank");
        columnCell.setCellStyle(categoryCellStyle);

        // Define the sub-columns
        CellStyle subCategoryCellStyle = workbook.createCellStyle();
        subCategoryCellStyle.setAlignment(HorizontalAlignment.CENTER);
        subCategoryCellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        subCategoryCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Cell subColumn1Cell = headerRow.createCell(1);
        subColumn1Cell.setCellValue("Sub Column 1");
        subColumn1Cell.setCellStyle(subCategoryCellStyle);

        Cell subColumn2Cell = headerRow.createCell(2);
        subColumn2Cell.setCellValue("Sub Column 2");
        subColumn2Cell.setCellStyle(subCategoryCellStyle);

        // Merge cells to create the main column with two sub-columns
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        // Add some data
        int rowNum = 1;
        for (int i = 1; i <= 5; i++) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Data " + i);
            row.createCell(1).setCellValue("Sub Column 1 Data " + i);
            row.createCell(2).setCellValue("Sub Column 2 Data " + i);
        }

        // Write the output to a file
        try {
            FileOutputStream fileOut = new FileOutputStream("ColumnWithSubColumns.xlsx");
            workbook.write(fileOut);
            fileOut.close();
            System.out.println("Excel report generated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Close the workbook
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

