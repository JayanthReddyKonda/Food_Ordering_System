package com.foodordering;

import java.sql.*;
import java.util.Scanner;

/**
 * Simple Food Ordering System - Admin Mode
 * Handles admin operations: add items, update prices, manage stock
 * 
 * Author: Jayanth
 */
public class AdminMode {
    private static final String ADMIN_PASSWORD = "admin123";
    private Connection conn;
    private Scanner scanner;
    
    public AdminMode(Scanner scanner) {
        this.conn = Database.getConnection();
        this.scanner = scanner;
    }
    
    /**
     * Admin menu (with password check)
     */
    public void start() {
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();
        
        if (!password.equals(ADMIN_PASSWORD)) {
            System.out.println("[ERROR] Invalid password");
            return;
        }
        
        System.out.println("[OK] Admin login successful");
        
        while (true) {
            System.out.println("\n=== ADMIN MODE ===");
            System.out.println("1. View All Menu Items");
            System.out.println("2. Add New Item");
            System.out.println("3. Update Item Price");
            System.out.println("4. Update Item Stock");
            System.out.println("5. View All Orders");
            System.out.println("6. Logout");
            System.out.print("Choice: ");
            
            String choice = scanner.nextLine();
            
            if (choice.equals("1")) {
                viewAllItems();
            } else if (choice.equals("2")) {
                addItem();
            } else if (choice.equals("3")) {
                updatePrice();
            } else if (choice.equals("4")) {
                updateStock();
            } else if (choice.equals("5")) {
                viewAllOrders();
            } else if (choice.equals("6")) {
                System.out.println("[OK] Admin logged out");
                break;
            } else {
                System.out.println("Invalid choice");
            }
        }
    }
    
    /**
     * View all menu items (including out of stock)
     */
    private void viewAllItems() {
        String sql = "SELECT item_id, name, price, quantity FROM menu_items ORDER BY item_id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n=== ALL MENU ITEMS ===");
            System.out.println("ID   Name                Price     Stock");
            System.out.println("------------------------------------------");
            
            while (rs.next()) {
                int stock = rs.getInt("quantity");
                String stockStr = stock > 0 ? String.valueOf(stock) : "OUT";
                System.out.printf("%-4d %-20s $%-8.2f %-5s%n",
                    rs.getInt("item_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    stockStr);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to load items: " + e.getMessage());
        }
    }
    
    /**
     * Add new menu item
     */
    private void addItem() {
        try {
            System.out.print("Enter item name: ");
            String name = scanner.nextLine().trim();
            
            if (name.isEmpty()) {
                System.out.println("[ERROR] Name cannot be empty");
                return;
            }
            
            System.out.print("Enter price: $");
            double price = Double.parseDouble(scanner.nextLine());
            
            if (price <= 0) {
                System.out.println("[ERROR] Price must be positive");
                return;
            }
            
            System.out.print("Enter initial stock: ");
            int quantity = Integer.parseInt(scanner.nextLine());
            
            if (quantity < 0) {
                System.out.println("[ERROR] Stock cannot be negative");
                return;
            }
            
            String sql = "INSERT INTO menu_items (name, price, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setInt(3, quantity);
                stmt.executeUpdate();
            }
            
            System.out.println("[OK] Item added successfully!");
            
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid input");
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to add item: " + e.getMessage());
        }
    }
    
    /**
     * Update item price
     */
    private void updatePrice() {
        viewAllItems();
        
        try {
            System.out.print("\nEnter item ID: ");
            int itemId = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Enter new price: $");
            double newPrice = Double.parseDouble(scanner.nextLine());
            
            if (newPrice <= 0) {
                System.out.println("[ERROR] Price must be positive");
                return;
            }
            
            String sql = "UPDATE menu_items SET price = ? WHERE item_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, newPrice);
                stmt.setInt(2, itemId);
                
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("[OK] Price updated successfully!");
                } else {
                    System.out.println("[ERROR] Item not found");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid input");
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to update price: " + e.getMessage());
        }
    }
    
    /**
     * Update item stock
     */
    private void updateStock() {
        viewAllItems();
        
        try {
            System.out.print("\nEnter item ID: ");
            int itemId = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Enter new stock quantity: ");
            int newQuantity = Integer.parseInt(scanner.nextLine());
            
            if (newQuantity < 0) {
                System.out.println("[ERROR] Stock cannot be negative");
                return;
            }
            
            String sql = "UPDATE menu_items SET quantity = ? WHERE item_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, newQuantity);
                stmt.setInt(2, itemId);
                
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("[OK] Stock updated successfully!");
                } else {
                    System.out.println("[ERROR] Item not found");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid input");
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to update stock: " + e.getMessage());
        }
    }
    
    /**
     * View all orders in the system
     */
    private void viewAllOrders() {
        String sql = "SELECT o.order_id, u.username, m.name, o.quantity, o.total_price, o.order_date " +
                    "FROM orders o " +
                    "JOIN users u ON o.user_id = u.user_id " +
                    "JOIN menu_items m ON o.item_id = m.item_id " +
                    "ORDER BY o.order_date DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n=== ALL ORDERS ===");
            System.out.println("ID   User        Item                Qty   Total     Date");
            System.out.println("----------------------------------------------------------------");
            
            boolean hasOrders = false;
            while (rs.next()) {
                hasOrders = true;
                System.out.printf("%-4d %-12s %-20s %-5d $%-8.2f %s%n",
                    rs.getInt("order_id"),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getDouble("total_price"),
                    rs.getTimestamp("order_date").toString().substring(0, 16));
            }
            
            if (!hasOrders) {
                System.out.println("No orders yet");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to load orders: " + e.getMessage());
        }
    }
}
