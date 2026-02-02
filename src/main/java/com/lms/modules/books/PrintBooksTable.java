package com.lms.modules.books;

import com.lms.utils.ColumnDef;
import com.lms.utils.TablePrinter;

import java.util.List;

public class PrintBooksTable {
    public static void printBooksTable(List<Book> books) {
        TablePrinter.print(books, List.of(
                new ColumnDef<>("ID", 4, b -> String.valueOf(b.getId())),
                new ColumnDef<>("ISBN", 14, Book::getIsbn),
                new ColumnDef<>("TITLE", 16, Book::getTitle),
                new ColumnDef<>("AUTHOR", 18, Book::getAuthor),
                new ColumnDef<>("QTY", 6, b -> String.valueOf(b.getQuantity())),
                new ColumnDef<>("PRICE", 8, b -> String.format("%.2f", b.getPrice()))
        ));
    }
}
