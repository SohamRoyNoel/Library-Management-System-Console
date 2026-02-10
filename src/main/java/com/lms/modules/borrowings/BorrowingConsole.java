package com.lms.modules.borrowings;

import com.lms.dto.BookSearchCriteria;
import com.lms.dto.MemberSearchCriteria;
import com.lms.modules.books.Book;
import com.lms.modules.member.Member;
import com.lms.modules.member.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static com.lms.utils.Commons.orElse;

public class BorrowingConsole {
    public void decideActions(Scanner sc) {
        List<Borrowing> borrowList = new ArrayList<>();
        System.out.println("What you want to do with borrowings?");
        System.out.println("Add | Update | Delete | Search");
        String action = sc.nextLine();
        try {
            switch (action) {
                case "add":
                    Borrowing borrowing = this.addABorrowing(sc);
                    Borrowing save = com.lms.modules.borrowings.Service.getInstance().saveABooking(borrowing);
                    borrowList.add(save);
                    System.out.println("Saved Borrowing Details");
                    PrintBorrowingsTable.printBorrowingsTable(borrowList);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Borrowing addABorrowing(Scanner sc) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        System.out.print("Borrowed Till: ");
        String borrowedTill = sc.nextLine();
        Date dueDate = Date.from(
                LocalDate.parse(borrowedTill, formatter)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );

        System.out.print("Quantity: ");
        String quantity = sc.nextLine();

        System.out.print("ISBN: ");
        String isbn = sc.nextLine();

        System.out.print("Member Id: ");
        String memberId = sc.nextLine();

        MemberSearchCriteria msc = new MemberSearchCriteria();
        msc.setMembershipVirtualId(memberId);
        List<Member> members = Service.getInstance().searchMember(msc);

        BookSearchCriteria bsc = new BookSearchCriteria();
        bsc.setIsbn(isbn);
        List<Book> books = com.lms.modules.books.Service.getInstance().searchBooks(bsc);

        return Borrowing.builder().borrowedTill(dueDate).quantity(Integer.parseInt(quantity))
                .book(books.get(0)).member(members.get(0)).build();
    }
}
