package com.lms.books;

import java.util.List;

public class PrintBooksTable {
    public static void printBooksTable(List<Book> books) {

        if (books == null || books.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        System.out.println(
                "+----+--------------+------------+---------------+----------+--------+");
        System.out.printf(
                "| %-2s | %-12s | %-10s | %-13s | %-8s | %-6s |%n",
                "ID", "ISBN", "Title", "Author", "Quantity", "Price");
        System.out.println(
                "+----+--------------+------------+---------------+----------+--------+");

        for (Book b : books) {
            System.out.printf(
                    "| %-2d | %-12s | %-10s | %-13s | %-8d | %-6.2f |%n",
                    b.getId(),
                    b.getIsbn(),
                    truncate(b.getTitle(), 10),
                    truncate(b.getAuthor(), 13),
                    b.getQuantity(),
                    b.getPrice()
            );
        }

        System.out.println(
                "+----+--------------+------------+---------------+----------+--------+");
    }

    private static String truncate(String value, int length) {
        return value.length() > length ? value.substring(0, length - 3) + "..." : value;
    }
}
