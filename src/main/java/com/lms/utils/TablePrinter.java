package com.lms.utils;

import java.util.List;

public final class TablePrinter {

    private TablePrinter() {}

    public static <T> void print(List<T> data, List<ColumnDef<T>> columns) {

        if (data == null || data.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (ColumnDef<T> col : columns) {
            int maxWidth = col.getHeader().length();

            for (T row : data) {
                String value = col.getValueExtractor().apply(row);
                if (value != null) {
                    maxWidth = Math.max(maxWidth, value.length());
                }
            }

            col.setWidth(maxWidth + 2); // little padding
        }

        printLine(columns);
        printHeader(columns);
        printLine(columns);

        for (T row : data) {
            printRow(row, columns);
        }

        printLine(columns);
    }

    private static <T> void printHeader(List<ColumnDef<T>> columns) {
        for (ColumnDef<T> col : columns) {
            System.out.printf("| %-"+col.getWidth()+"s ", col.getHeader());
        }
        System.out.println("|");
    }

    private static <T> void printRow(T row, List<ColumnDef<T>> columns) {
        for (ColumnDef<T> col : columns) {
            String value = col.getValueExtractor().apply(row);
            System.out.printf("| %-"+col.getWidth()+"s ",
                    fit(value, col.getWidth()));
        }
        System.out.println("|");
    }

    private static <T> void printLine(List<ColumnDef<T>> columns) {
        for (ColumnDef<T> col : columns) {
            System.out.print("+" + "-".repeat(col.getWidth() + 2));
        }
        System.out.println("+");
    }

    private static String fit(String value, int width) {
        if (value == null) return "";
        return value.length() > width
                ? value.substring(0, width - 3) + "..."
                : value;
    }
}