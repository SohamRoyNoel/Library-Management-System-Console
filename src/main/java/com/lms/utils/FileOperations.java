package com.lms.utils;

import com.lms.modules.books.Book;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FileOperations {

    public void readExcelFile() throws IOException {

        Properties properties = new Properties();

        try (InputStream propStream = FileOperations.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (propStream == null) {
                System.out.println("application.properties not found!");
                return;
            }
            properties.load(propStream);
            String fileName = properties.getProperty("import.file.name");
            int sheetNo = 0;
            InputStream is = FileOperations.class.getClassLoader().getResourceAsStream(fileName);
            if (is == null) {
                System.out.println("Excel file not found in resources folder!");
                return;
            }
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(sheetNo);
            if (sheet == null) {
                System.out.println("Sheet not found!");
                return;
            }

            System.out.println("Total Rows: " + sheet.getPhysicalNumberOfRows());

            List<Book> books = new ArrayList<>();
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = new HashMap<>();
            for (Cell cell : headerRow) {
                columnMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Book book = new Book();
                book.setTitle(
                        row.getCell(columnMap.get("Title")).getStringCellValue()
                );
                book.setAuthor(
                        row.getCell(columnMap.get("Author")).getStringCellValue()
                );
                book.setPrice(
                        (float) row.getCell(columnMap.get("Price")).getNumericCellValue()
                );
                books.add(book);
            }

            System.out.println("Books size: " + books.size());
            System.out.println(books.get(0));
        } catch (Exception e) {
            System.out.println("Unable to perform action");
            throw e;
        }
    }
}