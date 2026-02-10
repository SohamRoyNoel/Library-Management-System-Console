package com.lms.modules.borrowings;

import com.lms.modules.books.Book;
import com.lms.modules.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="borrowing")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"member", "book"})
public class Borrowing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, updatable = false)
    private Integer borrowingRefId;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date borrowedAt;
    @Column(nullable = false)
    private Date borrowedTill;
    @Column(nullable = false)
    private Integer quantity = 1;
    @Column(nullable = true)
    private Boolean isReturned = false;
    @Column(nullable = true)
    private Date returnedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @PrePersist
    protected void onCreate() {
        if (this.borrowedAt == null) {
            this.borrowedAt = new Date();
        }
        if (this.borrowingRefId == null) {
            this.borrowingRefId = (int) (System.currentTimeMillis() % 1_000_000);
        }
    }
}
