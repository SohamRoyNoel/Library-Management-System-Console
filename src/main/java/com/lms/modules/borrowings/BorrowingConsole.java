package com.lms.modules.borrowings;

import com.lms.dto.BookSearchCriteria;
import com.lms.dto.BorrowingSearchCriteria;
import com.lms.dto.MemberSearchCriteria;
import com.lms.modules.books.Book;
import com.lms.modules.member.Member;
import com.lms.modules.member.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
                    Borrowing savedBooking = com.lms.modules.borrowings.Service.getInstance().saveABooking(borrowing);
                    borrowList.add(savedBooking);
                    System.out.println("Saved Borrowing Details");
                    this.updateBookDataOnSuccessfulBorrow(savedBooking);
                    PrintBorrowingsTable.printBorrowingsTable(borrowList);
                    break;
                case "search":
                    // search by BOOK ISBN | MEMBER ID | BORROWING ID
                    List<Borrowing> listOfBorrowings = this.searchDetails(sc);
                    PrintBorrowingsTable.printBorrowingsTable(listOfBorrowings);
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

        if (members.isEmpty() || books.isEmpty()) {
            System.out.println("Wrong Info, Fuck off now");
            throw new RuntimeException("Wrong Info, Fuck off now");
        }

        return Borrowing.builder().borrowedTill(dueDate).quantity(Integer.parseInt(quantity))
                .book(books.get(0)).member(members.get(0)).build();
    }

    private void updateBookDataOnSuccessfulBorrow(Borrowing borrowing) {
        BookSearchCriteria bsc = new BookSearchCriteria();
        bsc.setId(borrowing.getBook().getId());
        List<Book> books = com.lms.modules.books.Service.getInstance().searchBooks(bsc);

        if (books.get(0).getQuantity() < borrowing.getQuantity()) {
            System.out.println("Available: " + books.get(0).getQuantity() + " Requested: " + borrowing.getQuantity());
            return;
        }
        books.get(0).setQuantity(books.get(0).getQuantity() - borrowing.getQuantity());
        com.lms.modules.books.Service.getInstance().saveABook(books.get(0));
    }

    private List<Borrowing> searchDetails(Scanner sc) {
        System.out.println("Search By BOOK ISBN | MEMBER ID | REF NUMBER, All or by any");
        String searchParams = sc.nextLine();
        BorrowingSearchCriteria bsc = this.parseSearchInput(searchParams);
        return com.lms.modules.borrowings.Service.getInstance().searchBorrowing(bsc);
    }

    private BorrowingSearchCriteria parseSearchInput(String input) {
        try {
            BorrowingSearchCriteria bsc = new BorrowingSearchCriteria();
            String[] parts = input.toLowerCase(Locale.ROOT).split(",");
            for (String part: parts) {
                String[] params = part.split(":");
                if (params.length != 2) throw new Exception("Wrong parameters");
                var key = params[0].trim();
                var value = params[1].trim();
                if ("isbn".equalsIgnoreCase(key)) {
                    bsc.getBook().setIsbn(value);
                } else if ("member".equalsIgnoreCase(key)) {
                    bsc.getMember().setMembershipVirtualId(value);
                } else if ("reference".equalsIgnoreCase(key)){
                    bsc.setBorrowingRefId(value);
                }
            }
            return bsc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
