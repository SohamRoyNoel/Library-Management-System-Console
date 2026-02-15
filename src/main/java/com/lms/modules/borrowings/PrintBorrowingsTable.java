package com.lms.modules.borrowings;

import com.lms.utils.ColumnDef;
import com.lms.utils.TablePrinter;

import java.text.SimpleDateFormat;
import java.util.List;

public class PrintBorrowingsTable {
    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");

    public static void printBorrowingsTable(List<Borrowing> borrowings) {
        System.out.println("=====> " + String.valueOf(borrowings.get(0).getBook().getIsbn()));
        TablePrinter.print(borrowings, List.of(
                new ColumnDef<>("REFERENCE ID", 5, b -> String.valueOf(b.getBorrowingRefId())),
                new ColumnDef<>("BORROWED AT", 10, b -> DF.format(b.getBorrowedAt())),
                new ColumnDef<>("BORROWED TILL", 5, b -> DF.format(b.getBorrowedTill())),
                new ColumnDef<>("QUANTITY", 5, b -> String.valueOf(b.getQuantity())),
                new ColumnDef<>("IS RETURNED", 5, b -> String.valueOf(b.getIsReturned())),
                new ColumnDef<>("RETURNED AT", 5, b -> b.getReturnedAt() != null ? DF.format(b.getReturnedAt()) : null),
                new ColumnDef<>("BOOK ISBN", 8, b -> b.getBook().getIsbn()),
                new ColumnDef<>("BOOK NAME", 5, b -> b.getBook().getTitle()),
                new ColumnDef<>("COPIES TAKEN", 5, b -> b.getQuantity().toString()),
                new ColumnDef<>("MEMBER ID", 5, b -> b.getMember().getMembershipVirtualId()),
                new ColumnDef<>("MEMBER NAME", 5, b -> b.getMember().getName()),
                new ColumnDef<>("MEMBER EMAIL", 5, b -> b.getMember().getEmail()),
                new ColumnDef<>("MEMBER PHONE", 5, b -> b.getMember().getPhoneNumber())
        ));
    }
}
