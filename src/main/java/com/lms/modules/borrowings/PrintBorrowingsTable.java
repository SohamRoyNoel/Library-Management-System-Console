package com.lms.modules.borrowings;

import com.lms.utils.ColumnDef;
import com.lms.utils.TablePrinter;

import java.text.SimpleDateFormat;
import java.util.List;

public class PrintBorrowingsTable {
    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");

    public static void printBorrowingsTable(List<Borrowing> borrowings) {
        TablePrinter.print(borrowings, List.of(
                new ColumnDef<>("ID", 5, b -> String.valueOf(b.getId())),

                new ColumnDef<>("BORROWED AT", 12,
                        b -> DF.format(b.getBorrowedAt())),

                new ColumnDef<>("BORROWED TILL", 12,
                        b -> DF.format(b.getBorrowedTill())),

                new ColumnDef<>("QTY", 5,
                        b -> String.valueOf(b.getQuantity())),

                new ColumnDef<>("RETURNED", 9,
                        b -> String.valueOf(b.getIsReturned())),

                new ColumnDef<>("RETURNED AT", 12,
                        b -> b.getReturnedAt() != null
                                ? DF.format(b.getReturnedAt())
                                : "-"),

                new ColumnDef<>("BOOK ISBN", 15,
                        b -> b.getBook().getIsbn()),

                new ColumnDef<>("BOOK NAME", 20,
                        b -> b.getBook().getTitle()),

                new ColumnDef<>("MEMBER ID", 12,
                        b -> b.getMember().getMembershipVirtualId()),

                new ColumnDef<>("MEMBER NAME", 15,
                        b -> b.getMember().getName()),

                new ColumnDef<>("EMAIL", 22,
                        b -> b.getMember().getEmail()),

                new ColumnDef<>("PHONE", 12,
                        b -> b.getMember().getPhoneNumber())
        ));
    }
}
