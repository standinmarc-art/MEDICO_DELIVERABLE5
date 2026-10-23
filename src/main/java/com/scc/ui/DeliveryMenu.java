package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.Scanner;

public class DeliveryMenu {
    private Scanner sc = new Scanner(System.in);
    private UserDao dao = new UserDao();
    private User currentUser;

    public DeliveryMenu(User currentUser) {
        this.currentUser = currentUser;
    }

    public void show() {
        while (true) {
            System.out.println("\n--> Welcome " + currentUser.username() + " | ROLE: DELIVERY");
            System.out.println("----------------------------------------");
            System.out.println("      SERVICE / DELIVERY DASHBOARD      ");
            System.out.println("----------------------------------------");
            System.out.println("1. View My Profile");
            System.out.println("2. Check My Account Status");
            System.out.println("3. Change My Password");
            System.out.println("4. Logout");
            System.out.print("Choose an option (1-4): ");

            switch (sc.nextLine().trim()) {
                case "1" -> viewProfile();
                case "2" -> checkStatus();
                case "3" -> changePassword();
                case "4" -> {
                    System.out.println("Logging out... Goodbye!");
                    return;
                }
                default -> System.out.println("[!] Invalid choice! Try again.");
            }
        }
    }

    private void viewProfile() {
        User u = dao.getUserByUsername(currentUser.username());
        if (u == null) {
            System.out.println("[✗] Account no longer exists.");
            return;
        }
        System.out.println("\n--- MY PROFILE ---");
        System.out.println("Username : " + u.username());
        System.out.println("Role     : Service / Delivery Personnel");
        System.out.println("Status   : " + u.accountStatus());
    }

    private void checkStatus() {
        User u = dao.getUserByUsername(currentUser.username());
        if (u == null) {
            System.out.println("[✗] Account no longer exists.");
            return;
        }
        System.out.println("\n--- ACCOUNT STATUS ---");
        System.out.println("Current Status: " + u.accountStatus());
    }

    private void changePassword() {
        User u = dao.getUserByUsername(currentUser.username());
        if (u == null) {
            System.out.println("[✗] Account no longer exists.");
            return;
        }
        System.out.println("\n--- CHANGE PASSWORD ---");
        System.out.print("Current Password: ");
        String oldPwd = sc.nextLine().trim();
        if (!u.password().equals(oldPwd)) {
            System.out.println("[✗] Incorrect Current Password.");
            return;
        }
        System.out.print("New Password: ");
        String newPwd = sc.nextLine().trim();
        if (newPwd.isEmpty()) {
            System.out.println("[!] Password cannot be empty.");
            return;
        }
        System.out.print("Confirm New Password: ");
        if (!newPwd.equals(sc.nextLine().trim())) {
            System.out.println("[✗] Passwords do not match.");
            return;
        }
        if (dao.updatePassword(u.username(), newPwd)) {
            System.out.println("[✓] Password Updated Successfully!");
        } else {
            System.out.println("[✗] Failed to update password.");
        }
    }
}