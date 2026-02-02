package com.lms.modules.member;

import com.lms.dto.MemberSearchCriteria;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static com.lms.utils.Commons.orElse;

public class MemberConsole {
    public void decideActions(Scanner sc) {
        System.out.println("What you want to do with members?");
        System.out.println("Add | Update | Delete | Search");
        String action = sc.nextLine();
        try {
            switch (action) {
                case "add":
                    Member memberModel = this.readMemberInput(sc, null);
                    Service.getInstance().saveMember(memberModel);
                    break;
                case "search":
                    this.searchMember(sc);
                default:
                    System.out.println("Invalid action");
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Member readMemberInput(@Nonnull Scanner sc, @Nullable Member member) {
        Member base = (member != null) ? member : new Member();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        System.out.print("Name: ");
        String name = orElse(sc.nextLine(), base.getName());

        System.out.print("Email: ");
        String email = orElse(sc.nextLine(), base.getEmail());

        System.out.print("Phone Number: ");
        String phNo = orElse(sc.nextLine(), base.getPhoneNumber());

        System.out.print("Date of joining: ");
        LocalDate date = LocalDate.parse(sc.nextLine(), formatter);
        Date doj = orElse(date, base.getDoj());

        System.out.print("Govt Id: ");
        String govtId = orElse(sc.nextLine(), base.getGovtId());

        System.out.print("Fee: ");
        Float fee = orElse(Float.parseFloat(sc.nextLine()), base.getFeeAmount());

        System.out.print("Membership type: ");
        String membershipType = orElse(sc.nextLine(), base.getMembershipType());

        Date membershipExpiredAt = Date.from(date.plusYears(6).atStartOfDay(ZoneId.systemDefault()).toInstant());
        String membershipVirtualId =Long.toString(Math.abs(ThreadLocalRandom.current().nextLong()), 36).substring(0, 6).toUpperCase();

        return Member.builder()
                .id(base.getId())
                .name(name)
                .email(email)
                .phoneNumber(phNo)
                .doj(doj)
                .govtId(govtId)
                .feeAmount(fee)
                .membershipExpiredAt(membershipExpiredAt)
                .membershipVirtualId(membershipVirtualId)
                .membershipType(membershipType).build();
    }

    private List<Member> searchMember(Scanner sc) {
        MemberSearchCriteria msc = new MemberSearchCriteria();
        System.out.println("You can search/update by Member ID only");
        String input = sc.nextLine();
        msc.setMembershipVirtualId(input);
        List<Member> members = Service.getInstance().searchMember(msc);
        if (!members.isEmpty()) {
            PrintMembersTable.printMembersTable(members);
        }
        return members;
    }
}
