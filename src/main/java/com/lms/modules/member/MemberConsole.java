package com.lms.modules.member;

import com.lms.db.TxManager;
import com.lms.dto.MemberSearchCriteria;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.lms.utils.Commons.*;

public class MemberConsole {
    public void decideActions(Scanner sc) {
        List<Member> membersList = new ArrayList<>();
        System.out.println("What you want to do with members?");
        System.out.println("Add | Update | Delete | Search");
        String action = sc.nextLine();
        try {
            switch (action) {
                case "add":
                    Member memberModel = this.readMemberInput(sc, null, null);
                    Member savedMem = TxManager.execute(session -> {
                        return Service.getInstance().saveMember(memberModel, session);
                    });
                    membersList.add(savedMem);
                    System.out.println("Saved Member Details");
                    PrintMembersTable.printMembersTable(membersList);
                    break;
                case "search":
                    this.searchMember(sc);
                    break;
                case "update":
                    Member memberUpdateModel = this.updateMember(sc, "update");
                    Member updatedMem = TxManager.execute(session -> {
                        return Service.getInstance().saveMember(memberUpdateModel, session);
                    });
                    membersList.add(updatedMem);
                    System.out.println("Updated Member Details");
                    PrintMembersTable.printMembersTable(membersList);
                    break;
                case "delete":
                    Member memberDeleteModel = this.updateMember(sc,  "delete");
                    Member deletedMem = TxManager.execute(session -> {
                        return Service.getInstance().saveMember(memberDeleteModel, session);
                    });
                    if (!deletedMem.isActive()) {
                        System.out.println("Member Deactivated with ID of " + deletedMem.getId());
                    }
                    break;
                default:
                    System.out.println("Invalid action");
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Member readMemberInput(@Nonnull Scanner sc, @Nullable Member member, @Nullable String flag) {
        Member base = (member != null) ? member : new Member();
        if ("delete".equalsIgnoreCase(flag)) {
            base.setActive(false);
            return base;
        }
        Date membershipExpiredAt;
        String membershipVirtualId;


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        System.out.print("Name: ");
        String name = orElse(sc.nextLine(), base.getName());

        System.out.print("Email: ");
        String email = orElse(sc.nextLine(), base.getEmail());

        System.out.print("Phone Number: ");
        String phNo = orElse(sc.nextLine(), base.getPhoneNumber());

        System.out.print("Date of joining: ");
        String locDate = sc.nextLine();
        Date doj = locDate.isEmpty()
                ? base.getDoj()
                : Date.from(
                LocalDate.parse(locDate, formatter)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );

        System.out.print("Govt Id: ");
        String govtId = orElse(sc.nextLine(), base.getGovtId());

        System.out.print("Fee: ");
        Float fee = readFloatOrElse(sc, base.getFeeAmount());

        System.out.print("Membership type: ");
        String membershipType = orElse(sc.nextLine(), base.getMembershipType());

        membershipExpiredAt = !locDate.isEmpty() ? addYears(doj, 5) : base.getMembershipExpiredAt();
        membershipVirtualId = member == null ? Long.toString(
                Math.abs(ThreadLocalRandom.current().nextLong()), 36
        ).substring(0, 6).toUpperCase() : base.getMembershipVirtualId();

        if ("update".equalsIgnoreCase(flag)) {
            // Never create another new build object while update, use existing
            this.applyMemberUpdates(base, name, email, phNo, doj, govtId, fee, membershipExpiredAt, membershipType);
            return base;
        }

        // CREATE
        return Member.builder()
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

    private void applyMemberUpdates(Member base, String name, String email, String phNo, Date doj, String govtId, Float fee, Date expiry, String type) {
        base.setName(name);
        base.setEmail(email);
        base.setPhoneNumber(phNo);
        base.setDoj(doj);
        base.setGovtId(govtId);
        base.setFeeAmount(fee);
        base.setMembershipExpiredAt(expiry);
        base.setMembershipType(type);
    }


    private List<Member> searchMember(Scanner sc) {
        MemberSearchCriteria msc = new MemberSearchCriteria();
        System.out.println("You can search by Member ID only");
        String input = sc.nextLine();
        msc.setMembershipVirtualId(input);
        List<Member> members = Service.getInstance().searchMember(msc);
        if (!members.isEmpty()) {
            System.out.println("Member Found with ID " + input);
            PrintMembersTable.printMembersTable(members);
        }
        return members;
    }

    private Member updateMember(Scanner sc, String flag) {
        var memberAsList = this.searchMember(sc);
        System.out.print("Found Member, Details, Type 'Y' to proceed: ");
        String confirmation = sc.nextLine();
        if (!"y".equalsIgnoreCase(confirmation.toLowerCase(Locale.ROOT))) {
            System.out.print("Update Aborted");
            return null;
        }
        return this.readMemberInput(sc, memberAsList.get(0), flag);
    }
}
