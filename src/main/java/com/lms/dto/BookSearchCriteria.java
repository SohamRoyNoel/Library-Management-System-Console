package com.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchCriteria {
    private Long id;
    private Integer quantity;
    private String isbn;
    private String title;
    private String author;
    private Float price;
    private String sortBy;
    private String sortOrder;
}
