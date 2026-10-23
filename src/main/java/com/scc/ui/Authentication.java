package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.Scanner;

public class Authentication {
    private Scanner sc = new Scanner(System.in);
    private UserDao dao = new UserDao();

    public void start() {
        System.out.println("\n===== LOGIN =====");
        System.out.print("Username: ");
        String username = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        User user = dao.getUserByUsername(username);

        if (user == null) {
            System.out.println("[✗] Username Not Registered.");
            return;
        }
        if (!user.password().equals(password)) {
            System.out.println("[✗] Incorrect Password.");
            return;
        }
        if ("SUSPENDED".equals(user.accountStatus())) {
            System.out.println("[!] Account Suspended. Please Contact the Admin.");
            return;
        }
        if (!"APPROVED".equals(user.accountStatus())) {
            System.out.println("[!] Account Not Approved Yet. Please Wait For Admin Approval.");
            return;
        }

        switch (user.role()) {
            case "ADMIN" -> new AdminMenu(user).show();
            case "DELIVERY" -> new DeliveryMenu(user).show();
            default -> System.out.println("[!] No menu available for this role yet.");
        }
    }
}