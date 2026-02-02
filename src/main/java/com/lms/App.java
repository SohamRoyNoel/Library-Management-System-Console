package com.lms;

import java.util.Scanner;
import com.lms.modules.books.BooksConsole;
import com.lms.modules.member.MemberConsole;

public class App {

    public static void main(String[] args) {

        System.out.println("📚 Library Management System");

        Scanner sc = new Scanner(System.in);
        BooksConsole bc = new BooksConsole();
        MemberConsole mc = new MemberConsole();

        while (true) {
            System.out.println("\nChoose module: book | member | exit");
            String input = sc.nextLine();

            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("👋 Exiting application...");
                break;
            }

            if ("book".equalsIgnoreCase(input)) {
                bc.decideActions(sc);
            } else if ("member".equalsIgnoreCase(input)) {
                mc.decideActions(sc);
            } else {
                System.out.println("❌ Invalid option");
            }
        }

        sc.close();
    }
}
