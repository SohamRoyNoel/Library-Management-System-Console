package com.lms.modules.borrowings;

import com.lms.dto.BookSearchCriteria;
import com.lms.dto.BorrowingResult;
import com.lms.dto.BorrowingSearchCriteria;
import com.lms.dto.MemberSearchCriteria;
import com.lms.modules.books.Book;
import com.lms.modules.member.Member;
import com.lms.modules.member.Service;
import jakarta.annotation.Nonnull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.lms.utils.Commons.orElse;

public class BorrowingConsole {
    private record BorrowingResultAction(Borrowing borrowing, int difference) {}

    public void decideActions(Scanner sc) {
        List<Borrowing> borrowList = new ArrayList<>();
        System.out.println("What you want to do with borrowings?");
        System.out.println("Add | Update | Delete | Search");
        String action = sc.nextLine();
        try {
            switch (action) {
                case "add":
                    Borrowing borrowing = this.addABorrowing(sc, "").getUpdatedBorrowing();
                    Borrowing savedBooking = com.lms.modules.borrowings.Service.getInstance().saveABooking(borrowing);
                    borrowList.add(savedBooking);
                    System.out.println("Saved Borrowing Details");
                    this.updateBookDataOnSuccessfulBorrow(savedBooking);
                    PrintBorrowingsTable.printBorrowingsTable(borrowList);
                    break;
                case "search":
                    List<Borrowing> listOfBorrowings = this.searchDetails(sc);
                    PrintBorrowingsTable.printBorrowingsTable(listOfBorrowings);
                    break;
                case "update":
                    var update = this.updateABorrowing(sc);
                    Borrowing updatedBorrowingBody = update.borrowing();
                    int bookDiff = update.difference();
                    Borrowing updateBooking = com.lms.modules.borrowings.Service.getInstance().saveABooking(updatedBorrowingBody);
                    borrowList.add(updateBooking);
                    System.out.println("Updated Borrowing Details");
                    if (bookDiff != 0) {
                        updateBooking.setQuantity(bookDiff);
                        this.updateBookDataOnSuccessfulBorrow(updateBooking);
                    }
                    PrintBorrowingsTable.printBorrowingsTable(borrowList);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BorrowingResult addABorrowing(Scanner sc, @Nonnull String refNo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        BorrowingSearchCriteria borrowingSearchCriteria;
        List<Borrowing> borrowings = null;
        BorrowingResult brs = new BorrowingResult();

        if (!refNo.isEmpty()) {
            borrowingSearchCriteria = new BorrowingSearchCriteria();
            borrowingSearchCriteria.setBorrowingRefId(refNo);
             borrowings = com.lms.modules.borrowings.Service.getInstance().searchBorrowing(borrowingSearchCriteria);
        }

        System.out.print("Borrowed Till: ");
        String borrowedTill = sc.nextLine();
        if (borrowings == null && refNo.isEmpty()) {
            throw new RuntimeException("No borrowing data found");
        }
        Date dueDate = !borrowedTill.isBlank() ? Date.from(
                LocalDate.parse(borrowedTill, formatter)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        ) : borrowings.get(0).getBorrowedTill();

        System.out.print("Quantity: ");
        String quantity = orElse(sc.nextLine(), borrowings.get(0).getQuantity().toString());

        System.out.print("ISBN: ");
        String isbn = orElse(sc.nextLine(), borrowings.get(0).getBook().getIsbn());

        System.out.print("Member Id: ");
        String memberId = orElse(sc.nextLine(), borrowings.get(0).getMember().getMembershipVirtualId());

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
        Borrowing borrowingBuilder = com.lms.modules.borrowings.Borrowing.builder().
                id(borrowings.get(0).getId())
                .borrowedAt(borrowings.get(0).getBorrowedAt())
                .borrowedTill(dueDate)
                .quantity(Integer.parseInt(quantity))
                .book(books.get(0))
                .member(members.get(0))
                .build();

        brs.setUpdatedBorrowing(borrowingBuilder);
        brs.setPreviousBorrowings(borrowings);
        return brs;
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

    private BorrowingResultAction updateABorrowing(Scanner sc) {
        System.out.println("Updatable fields, MEMBER ID | BOOK ID | Borrowed till | ISBN");
        System.out.print("Borrowing reference number: ");
        String refNo = sc.nextLine();

        BorrowingResult borrowing = this.addABorrowing(sc, refNo);
        var newBorrow = borrowing.getUpdatedBorrowing();
        // this can be done inside "updateBookDataOnSuccessfulBorrow", this is just for some fun, using record 😁
        var previousBorrowings = borrowing.getPreviousBorrowings();
        int bookDifferences = 0;
        if (!Objects.equals(newBorrow.getQuantity(), previousBorrowings.get(0).getQuantity())) {
            bookDifferences = previousBorrowings.get(0).getQuantity() - newBorrow.getQuantity();
        }
        return new BorrowingResultAction(newBorrow, bookDifferences);
    }
}
