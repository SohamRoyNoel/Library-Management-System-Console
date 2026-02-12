package com.lms.dto;

import com.lms.modules.books.Book;
import com.lms.modules.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingSearchCriteria {
    private Book book = new Book();
    private Member member = Member.builder().build(); // as member is @NoArgsConstructor(access = AccessLevel.PROTECTED) it'll throw error
    private String borrowingRefId;
}
