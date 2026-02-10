package com.lms.modules.books;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	private String isbn;
	@Column(nullable = false)
	private String title;
	@Column(nullable = false)
	private String genre;
	@Column(nullable = false)
	private String author;
	@Column(nullable = false)
	private Integer quantity;
	@Column(nullable = false)
	private boolean isInStock;
	@Column(nullable = false)
	private Float price;
	@Column(nullable = false)
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
