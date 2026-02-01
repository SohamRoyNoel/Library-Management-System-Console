package com.lms;

import java.util.Scanner;
import com.lms.books.BooksConsole;

public class App {

    public static void main(String[] args) {

        System.out.println("📚 Library Management System");

        Scanner sc = new Scanner(System.in);
        BooksConsole bc = new BooksConsole();

        while (true) {
            System.out.println("\nChoose module: book | exit");
            String input = sc.nextLine();

            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("👋 Exiting application...");
                break;
            }

            if ("book".equalsIgnoreCase(input)) {
                bc.decideActions(sc);
            } else {
                System.out.println("❌ Invalid option");
            }
        }

        sc.close();
    }
}
