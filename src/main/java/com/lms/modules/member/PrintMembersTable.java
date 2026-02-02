package com.lms.modules.member;

import com.lms.utils.ColumnDef;
import com.lms.utils.TablePrinter;

import java.text.SimpleDateFormat;
import java.util.List;

public class PrintMembersTable {
    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");

    public static void printMembersTable(List<Member> members) {
        TablePrinter.print(members, List.of(
                // This is a method reference.=> m -> m.getName()
                new ColumnDef<>("NAME", 18, Member::getName),
                new ColumnDef<>("MEM ID", 14, Member::getMembershipVirtualId),
                new ColumnDef<>("PHONE", 12, Member::getPhoneNumber),
                new ColumnDef<>("EMAIL", 24, Member::getEmail),
                new ColumnDef<>("GOVT ID", 18, Member::getGovtId),
                new ColumnDef<>("FEE", 8, m -> String.valueOf(m.getFeeAmount())),
                new ColumnDef<>("TYPE", 20, Member::getMembershipType),
                new ColumnDef<>("DOJ", 10, m -> DF.format(m.getDoj()))
        ));
    }
}
