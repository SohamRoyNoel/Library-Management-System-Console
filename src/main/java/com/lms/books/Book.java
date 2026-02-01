package com.lms.books;

import java.util.Date;

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

	@PrePersist
	public void prePersist() {
		this.isDeleted = false;
	}
}
