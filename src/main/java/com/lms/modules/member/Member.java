package com.lms.modules.member;

import com.lms.modules.borrowings.Borrowing;
import com.lms.modules.person.Person;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name= "member")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true, exclude = "borrowings")
@SuperBuilder
public class Member extends Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Float feeAmount;
    @Column(nullable = false)
    private Date membershipExpiredAt;
    @Column(nullable = false)
    private boolean isVolunteer = false; // Later map it to employee with string
    @Column(nullable = false, unique = true)
    private String membershipVirtualId;
    @Column(nullable = false)
    private String membershipType;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Borrowing> borrowings = new ArrayList<>();
}
