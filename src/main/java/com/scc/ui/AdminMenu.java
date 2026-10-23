package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private Scanner sc = new Scanner(System.in);
    private UserDao dao = new UserDao();
    private User currentUser;

    public AdminMenu(User currentUser) {
        this.currentUser = currentUser;
    }

    public void show() {
        while (true) {
            System.out.println("\n--> Welcome " + currentUser.username() + " | ROLE: ADMIN");
            System.out.println("----------------------------------------");
            System.out.println("           ADMIN DASHBOARD              ");
            System.out.println("----------------------------------------");
            System.out.println("1. View All Users");
            System.out.println("2. View Pending User Approvals");
            System.out.println("3. Approve User Registration");
            System.out.println("4. Add New User");
            System.out.println("5. Update User Role / Status");
            System.out.println("6. Delete User Account");
            System.out.println("7. Logout");
            System.out.print("Choose an option (1-7): ");

            switch (sc.nextLine().trim()) {
                case "1" -> viewAllUsers();
                case "2" -> viewPending();
                case "3" -> approveUser();
                case "4" -> addUser();
                case "5" -> updateUser();
                case "6" -> deleteUser();
                case "7" -> {
                    System.out.println("Logging out... Goodbye!");
                    return;
                }
                default -> System.out.println("[!] Invalid choice! Try again.");
            }
        }
    }

    private String roleName(String role) {
        return switch (role) {
            case "ADMIN" -> "Condo Administrator / Building Management";
            case "SECURITY" -> "Security Gate Staff / Guards";
            case "DELIVERY" -> "Service / Delivery Personnel";
            default -> role;
        };
    }

    private String chooseRole() {
        System.out.println("  1. Condo Administrator / Building Management");
        System.out.println("  2. Security Gate Staff / Guards");
        System.out.println("  3. Service / Delivery Personnel");
        System.out.print("Enter Choice: ");
        return switch (sc.nextLine().trim()) {
            case "1" -> "ADMIN";
            case "2" -> "SECURITY";
            case "3" -> "DELIVERY";
            default -> null;
        };
    }

    private boolean isSelf(String uname) {
        return uname.equalsIgnoreCase(currentUser.username());
    }

    private void viewAllUsers() {
        System.out.println("\n--- ALL USERS ---");
        List<User> list = dao.getAllUsers();
        if (list.isEmpty()) {
            System.out.println("[!] No users found.");
            return;
        }
        System.out.printf("%-15s | %-42s | %-12s%n", "USERNAME", "ROLE", "STATUS");
        System.out.println("--------------------------------------------------------------------------");
        for (User u : list) {
            System.out.printf("%-15s | %-42s | %-12s%n",
                    u.username(), roleName(u.role()), u.accountStatus());
        }
    }

    private void viewPending() {
        System.out.println("\n--- PENDING USER APPROVALS ---");
        List<User> list = dao.getPendingUsers();
        if (list.isEmpty()) {
            System.out.println("[!] No pending approvals.");
            return;
        }
        System.out.printf("%-15s | %-42s%n", "USERNAME", "ROLE");
        System.out.println("---------------------------------------------------------------");
        for (User u : list) {
            System.out.printf("%-15s | %-42s%n", u.username(), roleName(u.role()));
        }
    }

    private void approveUser() {
        if (dao.getPendingUsers().isEmpty()) {
            System.out.println("\n[!] No pending approvals.");
            return;
        }
        viewPending();
        System.out.print("\nEnter Username to Approve: ");
        String uname = sc.nextLine().trim();

        User target = dao.getUserByUsername(uname);
        if (target == null) {
            System.out.println("[✗] Failed — Username Not Found.");
            return;
        }
        if (!"PENDING".equals(target.accountStatus())) {
            System.out.println("[!] That account is not pending (status: " + target.accountStatus() + ").");
            return;
        }
        if (dao.updateStatus(uname, "APPROVED")) {
            System.out.println("[✓] User Approved Successfully!");
        } else {
            System.out.println("[✗] Failed to approve user.");
        }
    }

    private void addUser() {
        System.out.println("\n--- ADD NEW USER ---");
        System.out.print("Username: ");
        String uname = sc.nextLine().trim();
        if (uname.isEmpty()) {
            System.out.println("[!] Username cannot be empty.");
            return;
        }
        System.out.print("Password: ");
        String pwd = sc.nextLine().trim();
        if (pwd.isEmpty()) {
            System.out.println("[!] Password cannot be empty.");
            return;
        }

        System.out.println("\nSelect Role:");
        String role = chooseRole();
        if (role == null) {
            System.out.println("[✗] Invalid Role Choice!");
            return;
        }

        System.out.print("\nStatus — PENDING or APPROVED? ");
        String statusInput = sc.nextLine().trim().toUpperCase();
        String status = statusInput.equals("APPROVED") ? "APPROVED" : "PENDING";

        if (dao.addUser(new User(uname, pwd, role, status))) {
            System.out.println("[✓] User Added!");
            System.out.println("    Role: " + roleName(role));
            System.out.println("    Status: " + status);
        } else {
            System.out.println("[✗] Failed — Username Already Exists.");
        }
    }

    private void updateUser() {
        viewAllUsers();
        System.out.print("\nEnter Username to Update: ");
        String uname = sc.nextLine().trim();

        if (dao.getUserByUsername(uname) == null) {
            System.out.println("[✗] Failed — Username Not Found.");
            return;
        }
        if (isSelf(uname)) {
            System.out.println("[!] You cannot change your own role or status.");
            return;
        }

        System.out.print("Update Role? (Y/N): ");
        if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("\nSelect New Role:");
            String newRole = chooseRole();
            if (newRole != null && dao.updateRole(uname, newRole)) {
                System.out.println("[✓] Role Updated To: " + roleName(newRole));
            } else {
                System.out.println("[✗] Failed — Invalid Choice.");
            }
        }

        System.out.print("\nUpdate Status? (Y/N): ");
        if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("New Status:");
            System.out.println("  1. PENDING");
            System.out.println("  2. APPROVED");
            System.out.println("  3. SUSPENDED");
            System.out.print("Choice: ");
            String newStatus = switch (sc.nextLine().trim()) {
                case "1" -> "PENDING";
                case "2" -> "APPROVED";
                case "3" -> "SUSPENDED";
                default -> null;
            };
            if (newStatus != null && dao.updateStatus(uname, newStatus)) {
                System.out.println("[✓] Status Updated To: " + newStatus);
            } else {
                System.out.println("[✗] Failed — Invalid Choice.");
            }
        }
    }

    private void deleteUser() {
        viewAllUsers();
        System.out.print("\nEnter Username to Delete: ");
        String uname = sc.nextLine().trim();

        if (dao.getUserByUsername(uname) == null) {
            System.out.println("[✗] Failed — Username Not Found.");
            return;
        }
        if (isSelf(uname)) {
            System.out.println("[!] You cannot delete your own account while logged in.");
            return;
        }

        System.out.print("Are you sure? Type YES to confirm: ");
        if (sc.nextLine().trim().equalsIgnoreCase("YES")) {
            if (dao.deleteUser(uname)) {
                System.out.println("[✓] User Account Deleted.");
            } else {
                System.out.println("[✗] Failed to delete user.");
            }
        } else {
            System.out.println("[!] Deletion Cancelled.");
        }
    }
}