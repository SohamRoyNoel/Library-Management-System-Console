package com.lms.dto;

import com.lms.modules.borrowings.Borrowing;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BorrowingResult {
    private Borrowing updatedBorrowing;
    private List<Borrowing> previousBorrowings;
}
