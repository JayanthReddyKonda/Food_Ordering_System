package com.foodordering;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Simple database connection utility.
 * Provides a single connection to MySQL database.
 * 
 * Author: Jayanth
 */
public class Database {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/food_ordering_system";
    private static final String USER = "root";
    private static final String PASSWORD = "jayanth";
    
    private static Connection connection = null;
    
    /**
     * Get database connection.
     * Creates connection if it doesn't exist.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✓ Connected to database");
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Close database connection.
     */
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
