package com.scc.dao;

import com.scc.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private static final String[] VALID_ROLES = {"ADMIN", "SECURITY", "DELIVERY"};
    private static final String[] VALID_STATUSES = {"PENDING", "APPROVED", "SUSPENDED"};

    private boolean isValidRole(String role) {
        for (String r : VALID_ROLES) if (r.equals(role)) return true;
        return false;
    }

    private boolean isValidStatus(String status) {
        for (String s : VALID_STATUSES) if (s.equals(status)) return true;
        return false;
    }

    public boolean addUser(User user) {
        if (!isValidRole(user.role())) {
            System.out.println("[✗] Invalid Role!");
            return false;
        }
        String status = user.accountStatus() != null ? user.accountStatus() : "PENDING";
        try (Connection c = DatabaseConnection.connect();
             PreparedStatement p = c.prepareStatement(
                     "INSERT INTO Users (Username,Password,Role,AccountStatus) VALUES (?,?,?,?)")) {
            p.setString(1, user.username());
            p.setString(2, user.password());
            p.setString(3, user.role());
            p.setString(4, status);
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public User getUserByUsername(String uname) {
        try (Connection c = DatabaseConnection.connect();
             PreparedStatement p = c.prepareStatement("SELECT * FROM Users WHERE Username=?")) {
            p.setString(1, uname);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return new User(
                            r.getString("Username"),
                            r.getString("Password"),
                            r.getString("Role"),
                            r.getString("AccountStatus")
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("[✗] Lookup Error: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.connect();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT * FROM Users ORDER BY User_ID")) {
            while (r.next()) {
                list.add(new User(
                        r.getString("Username"), "", r.getString("Role"), r.getString("AccountStatus")
                ));
            }
        } catch (Exception e) {
            System.out.println("[✗] Load Error: " + e.getMessage());
        }
        return list;
    }

    public List<User> getPendingUsers() {
        List<User> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.connect();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(
                     "SELECT * FROM Users WHERE AccountStatus='PENDING' ORDER BY User_ID")) {
            while (r.next()) {
                list.add(new User(
                        r.getString("Username"), "", r.getString("Role"), r.getString("AccountStatus")
                ));
            }
        } catch (Exception e) {
            System.out.println("[✗] Load Error: " + e.getMessage());
        }
        return list;
    }

    public boolean updateStatus(String uname, String status) {
        if (!isValidStatus(status)) return false;
        try (Connection c = DatabaseConnection.connect();
             PreparedStatement p = c.prepareStatement(
                     "UPDATE Users SET AccountStatus=? WHERE Username=?")) {
            p.setString(1, status);
            p.setString(2, uname);
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateRole(String uname, String newRole) {
        if (!isValidRole(newRole)) return false;
        try (Connection c = DatabaseConnection.connect();
             PreparedStatement p = c.prepareStatement(
                     "UPDATE Users SET Role=? WHERE Username=?")) {
            p.setString(1, newRole);
            p.setString(2, uname);
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updatePassword(String uname, String newPassword) {
        try (Connection c = DatabaseConnection.connect();
             PreparedStatement p = c.prepareStatement(
                     "UPDATE Users SET Password=? WHERE Username=?")) {
            p.setString(1, newPassword);
            p.setString(2, uname);
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteUser(String uname) {
        try (Connection c = DatabaseConnection.connect();
             PreparedStatement p = c.prepareStatement("DELETE FROM Users WHERE Username=?")) {
            p.setString(1, uname);
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}