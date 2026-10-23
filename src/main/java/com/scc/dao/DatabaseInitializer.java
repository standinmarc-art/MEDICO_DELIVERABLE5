package com.scc.dao;

import com.scc.model.User;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void init() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            String createUsers = """
                CREATE TABLE IF NOT EXISTS Users (
                    User_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    Username TEXT NOT NULL UNIQUE,
                    Password TEXT NOT NULL,
                    Role TEXT NOT NULL,
                    AccountStatus TEXT NOT NULL DEFAULT 'PENDING'
                )
                """;
            stmt.execute(createUsers);

            UserDao dao = new UserDao();
            if (dao.getUserByUsername("admin") == null) {
                dao.addUser(new User("admin", "admin123", "ADMIN", "APPROVED"));
                System.out.println("[✓] Default Admin — admin / admin123");
            }
            System.out.println("[✓] System Ready — condo.db");
        } catch (Exception e) {
            System.out.println("[✗] Init Error: " + e.getMessage());
        }
    }
}