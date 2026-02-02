package com.lms.person;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public abstract class Person {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String phoneNumber;
    @Column(nullable = false)
    private Date doj;
    @Column(nullable = false)
    private boolean isActive;
    @Column(nullable = false, unique = true)
    private String govtId;

    @PrePersist
    protected void prePersist() {
        if (!this.isActive) {
            this.isActive = true;
        }
    }
}
