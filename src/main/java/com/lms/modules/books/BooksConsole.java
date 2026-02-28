package com.lms.modules.books;

import com.lms.db.TxManager;
import com.lms.dto.BookSearchCriteria;
import com.lms.utils.FileOperations;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.io.IOException;
import java.util.*;

public class BooksConsole {
	public void decideActions(Scanner sc) {
		System.out.println("What you want to do with books?");
		System.out.println("Add | Update | Delete | Search | Import");

		String action = sc.nextLine();
		try {
			switch (action) {
				case "add":
					Book booksModel = this.readBookInput(sc, null);
					TxManager.execute(session -> {
						return Service.getInstance().saveABook(booksModel, session);
					});
					break;
				case "search":
					this.bookListingUtils(sc, "", "Book not found.");
					break;
				case "update":
					this.bookUpdaterUtils(sc, "update");
					break;
				case "delete":
					this.bookUpdaterUtils(sc, "delete");
				case "import":
					this.importBooksFromExcel(sc);
					break;
				default:
					System.out.println("Invalid action");
					break;
			}

		} catch (Exception e) {
			System.out.println("Unable to perform action");
			throw e;
		}
	}

	private void importBooksFromExcel(@Nonnull Scanner sc) {
		FileOperations fo = new FileOperations();
        try {
            List<Book> bookList = fo.readExcelFile(Book.class, 10);
			System.out.println("Book => " + bookList.get(0));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	private Book readBookInput(@Nonnull Scanner sc, @Nullable Book book) {
		Book base = (book != null) ? book : new Book();

		System.out.print("ISBN: ");
		String isbn = orElse(sc.nextLine(), base.getIsbn());

		System.out.print("Title: ");
		String title = orElse(sc.nextLine(), base.getTitle());

		System.out.print("Genre: ");
		String genre = orElse(sc.nextLine(), base.getGenre());

		System.out.print("Author: ");
		String author = orElse(sc.nextLine(), base.getAuthor());

		System.out.print("Quantity: ");
		String qtyInput = sc.nextLine();
		int quantity = qtyInput.isBlank() ? base.getQuantity() : Integer.parseInt(qtyInput);

		System.out.print("Price: ");
		String priceInput = sc.nextLine();
		Float price = priceInput.isBlank() ? base.getPrice() : Float.parseFloat(priceInput);

        return Book.builder()
				.id(base.getId())
				.isbn(isbn)
				.title(title)
				.genre(genre)
				.author(author)
				.quantity(quantity)
				.isInStock(true)
				.price(price)
				.purchasedDate(new Date())
				.build();
	}

	private List<Book> searchBook(Scanner sc, String actionFlag) {
		BookSearchCriteria criteria;
		if (!actionFlag.isEmpty() && "update".equalsIgnoreCase(actionFlag)) {
			BookSearchCriteria bsc = new BookSearchCriteria();
			System.out.println("You can search and update by ISBN only");
			String input = sc.nextLine();
			bsc.setIsbn(input);
			criteria = bsc;
		} else {
			System.out.println("You can search by: ISBN: <value> | title: <value> | price: <value> | author: <value>");
			String input = sc.nextLine();
			criteria = parseSearchInput(input);
		}

		return Service.getInstance().searchBooks(criteria);
	}

	private BookSearchCriteria parseSearchInput(String input) {
		BookSearchCriteria bsc = new BookSearchCriteria();
		String[] parts = input.toLowerCase(Locale.ROOT).split(",");
		for (String part: parts) {
			String[] split = part.split(":");
			if (split.length != 2) continue;

			String key = split[0].trim();
			String value = split[1].trim();

			switch (key) {
				case "isbn":
					bsc.setIsbn(value);
					break;
				case "title":
					bsc.setTitle(value);
					break;
				case "author":
					bsc.setAuthor(value);
					break;
				case "price":
					bsc.setSortBy("price");
					bsc.setSortOrder(value.equalsIgnoreCase("desc") ? "desc" : "asc");
					break;
			}
		}

		return bsc;
	}

	private String orElse(String input, String fallback) {
		return (input == null || input.isBlank()) ? fallback : input;
	}

	private List<Book> bookListingUtils(Scanner sc, String actionFlag, String errorMsg) {
		List<Book> booksSearch = this.searchBook(sc, actionFlag);
		if (!booksSearch.isEmpty()) {
			PrintBooksTable.printBooksTable(booksSearch);
		}
		return  booksSearch;
	}

	private void bookUpdaterUtils(Scanner sc, String action) {
		Book updatedBookModel = null;
		List<Book> books = this.bookListingUtils(sc, "update", "Invalid ISBN.");
		System.out.print("Found Book, Details, Type 'Y' to proceed: ");
		String confirmation = sc.nextLine();
		if (!"y".equalsIgnoreCase(confirmation.toLowerCase(Locale.ROOT))) return;
		if ("update".equalsIgnoreCase(action)) {
			updatedBookModel = this.readBookInput(sc, books.get(0));
		} else {
			Book b = books.get(0);
			b.setDeleted(true);
			updatedBookModel = b;
		}
		Book finalUpdatedBookModel = updatedBookModel;
		TxManager.execute(session -> {
			return Service.getInstance().saveABook(finalUpdatedBookModel, session);
		});
	}
}
