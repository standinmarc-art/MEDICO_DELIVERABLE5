package com.scc.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:condo.db";

    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            System.out.println("[✗] DB Error: " + e.getMessage());
            return null;
        }
    }
}