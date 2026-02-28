package com.lms.modules.books;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lms.annotations.ExcelColumn;
import com.lms.modules.borrowings.Borrowing;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	@ExcelColumn("ISBN")
	private String isbn;

	@Column(nullable = false)
	@ExcelColumn("Title")
	private String title;

	@Column(nullable = false)
	@ExcelColumn("Genre")
	private String genre;

	@Column(nullable = false)
	@ExcelColumn("Author")
	private String author;

	@Column(nullable = false)
	@ExcelColumn("Quantity")
	private Integer quantity;

	@Column(nullable = false)
	@ExcelColumn("IsInStock")
	private boolean isInStock;

	@Column(nullable = false)
	@ExcelColumn("Price")
	private Float price;

	@Column(nullable = false)
	@ExcelColumn("Purchased Date")
	private Date purchasedDate;

	@Column(nullable = false)
	private boolean isDeleted;

	@OneToMany(mappedBy = "book")
	private List<Borrowing> borrowings = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		this.isDeleted = false;
	}
}
