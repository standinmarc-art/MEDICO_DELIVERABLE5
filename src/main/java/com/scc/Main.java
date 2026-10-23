package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.dao.UserDao;
import com.scc.model.User;
import com.scc.ui.Authentication;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.init();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== CONDOMINIUM ACCESS CONTROL & DUES COLLECTION SYSTEM =====");
            System.out.println("1. LOGIN");
            System.out.println("2. REGISTER");
            System.out.println("3. EXIT");
            System.out.print("Enter choice: ");

            switch (sc.nextLine().trim()) {
                case "1" -> new Authentication().start();
                case "2" -> register(sc);
                case "3" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("[!] Invalid choice! Try again.");
            }
        }
    }

    private static void register(Scanner sc) {
        System.out.println("\n--- REGISTER NEW ACCOUNT ---");
        System.out.print("Username: ");
        String u = sc.nextLine().trim();
        System.out.print("Password: ");
        String p = sc.nextLine().trim();

        if (u.isEmpty() || p.isEmpty()) {
            System.out.println("[!] Username and password cannot be empty.");
            return;
        }

        System.out.println("\nSelect Role:");
        System.out.println("  1. Security Gate Staff / Guards");
        System.out.println("  2. Service / Delivery Personnel");
        System.out.print("Choice: ");
        String role = switch (sc.nextLine().trim()) {
            case "1" -> "SECURITY";
            case "2" -> "DELIVERY";
            default -> null;
        };

        if (role == null) {
            System.out.println("[✗] Invalid choice!");
            return;
        }

        if (new UserDao().addUser(new User(u, p, role, "PENDING"))) {
            System.out.println("[✓] Registered! Wait for Admin approval.");
        } else {
            System.out.println("[✗] Username already exists.");
        }
    }
}
