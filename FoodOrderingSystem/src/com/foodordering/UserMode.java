package com.foodordering;

import java.sql.*;
import java.util.Scanner;

/**
 * Simple Food Ordering System - User Mode
 * Handles all user operations: register, login, wallet, ordering
 * 
 * Author: Jayanth
 */
public class UserMode {
    private Connection conn;
    private Scanner scanner;
    private int currentUserId = -1;
    private String currentUsername = "";
    
    public UserMode(Scanner scanner) {
        this.conn = Database.getConnection();
        this.scanner = scanner;
    }
    
    /**
     * Main user menu
     */
    public void start() {
        while (true) {
            System.out.println("\n=== USER MODE ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Back to Main Menu");
            System.out.print("Choice: ");
            
            String choice = scanner.nextLine();
            
            if (choice.equals("1")) {
                register();
            } else if (choice.equals("2")) {
                login();
            } else if (choice.equals("3")) {
                break;
            } else {
                System.out.println("Invalid choice");
            }
        }
    }
    
    /**
     * Register new user
     */
    private void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        
        if (username.isEmpty()) {
            System.out.println("[ERROR] Username cannot be empty");
            return;
        }
        
        String sql = "INSERT INTO users (username, wallet_balance) VALUES (?, 0.00)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    currentUserId = rs.getInt(1);
                    currentUsername = username;
                    System.out.println("[OK] Registration successful!");
                    System.out.println("[OK] Wallet created with $0.00");
                    userMenu();
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("[ERROR] Username already exists");
            } else {
                System.out.println("[ERROR] Registration failed: " + e.getMessage());
            }
        }
    }
    
    /**
     * Login existing user
     */
    private void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        
        String sql = "SELECT user_id, username FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    currentUserId = rs.getInt("user_id");
                    currentUsername = rs.getString("username");
                    System.out.println("[OK] Login successful! Welcome, " + currentUsername);
                    userMenu();
                } else {
                    System.out.println("[ERROR] User not found");
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Login failed: " + e.getMessage());
        }
    }
    
    /**
     * User menu after login
     */
    private void userMenu() {
        while (true) {
            System.out.println("\n=== Welcome, " + currentUsername + " ===");
            System.out.println("1. Add Money to Wallet");
            System.out.println("2. View Menu");
            System.out.println("3. Place Order");
            System.out.println("4. View My Orders");
            System.out.println("5. Check Balance");
            System.out.println("6. Logout");
            System.out.print("Choice: ");
            
            String choice = scanner.nextLine();
            
            if (choice.equals("1")) {
                addMoney();
            } else if (choice.equals("2")) {
                viewMenu();
            } else if (choice.equals("3")) {
                placeOrder();
            } else if (choice.equals("4")) {
                viewMyOrders();
            } else if (choice.equals("5")) {
                checkBalance();
            } else if (choice.equals("6")) {
                System.out.println("[OK] Logged out");
                currentUserId = -1;
                currentUsername = "";
                break;
            } else {
                System.out.println("Invalid choice");
            }
        }
    }
    
    /**
     * Add money to wallet
     */
    private void addMoney() {
        try {
            System.out.print("Enter amount to add: $");
            double amount = Double.parseDouble(scanner.nextLine());
            
            if (amount <= 0) {
                System.out.println("[ERROR] Amount must be positive");
                return;
            }
            
            String sql = "UPDATE users SET wallet_balance = wallet_balance + ? WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, amount);
                stmt.setInt(2, currentUserId);
                stmt.executeUpdate();
            }
            
            System.out.println("[OK] $" + String.format("%.2f", amount) + " added to wallet");
            checkBalance();
            
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid amount");
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to add money: " + e.getMessage());
        }
    }
    
    /**
     * View available menu items
     */
    private void viewMenu() {
        String sql = "SELECT item_id, name, price, quantity FROM menu_items WHERE quantity > 0";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n=== MENU ===");
            System.out.println("ID   Name                Price     Stock");
            System.out.println("------------------------------------------");
            
            boolean hasItems = false;
            while (rs.next()) {
                hasItems = true;
                System.out.printf("%-4d %-20s $%-8.2f %-5d%n",
                    rs.getInt("item_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"));
            }
            
            if (!hasItems) {
                System.out.println("No items available");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to load menu: " + e.getMessage());
        }
    }
    
    /**
     * Place an order
     */
    private void placeOrder() {
        viewMenu();
        
        try {
            System.out.print("\nEnter item ID: ");
            int itemId = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Enter quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine());
            
            if (quantity <= 0) {
                System.out.println("[ERROR] Quantity must be positive");
                return;
            }
            
            // Get item details and verify stock
            String itemName;
            double price;
            int stock;
            double balance;
            
            String sql = "SELECT name, price, quantity FROM menu_items WHERE item_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, itemId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("[ERROR] Item not found");
                        return;
                    }
                    itemName = rs.getString("name");
                    price = rs.getDouble("price");
                    stock = rs.getInt("quantity");
                }
            }
            
            if (stock < quantity) {
                System.out.println("[ERROR] Insufficient stock. Available: " + stock);
                return;
            }
            
            double totalPrice = price * quantity;
            
            // Check wallet balance
            sql = "SELECT wallet_balance FROM users WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, currentUserId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("[ERROR] User not found");
                        return;
                    }
                    balance = rs.getDouble("wallet_balance");
                }
            }
            
            if (balance < totalPrice) {
                System.out.println("[ERROR] Insufficient balance. Need: $" + String.format("%.2f", totalPrice) 
                    + ", Have: $" + String.format("%.2f", balance));
                return;
            }
            
            // Process order with transaction
            conn.setAutoCommit(false);
            
            try {
                // Deduct from wallet
                sql = "UPDATE users SET wallet_balance = wallet_balance - ? WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setDouble(1, totalPrice);
                    stmt.setInt(2, currentUserId);
                    stmt.executeUpdate();
                }
                
                // Reduce stock
                sql = "UPDATE menu_items SET quantity = quantity - ? WHERE item_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, quantity);
                    stmt.setInt(2, itemId);
                    stmt.executeUpdate();
                }
                
                // Create order
                sql = "INSERT INTO orders (user_id, item_id, quantity, total_price) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, currentUserId);
                    stmt.setInt(2, itemId);
                    stmt.setInt(3, quantity);
                    stmt.setDouble(4, totalPrice);
                    stmt.executeUpdate();
                }
                
                conn.commit();
                
                System.out.println("\n[OK] Order placed successfully!");
                System.out.println("  Item: " + itemName);
                System.out.println("  Quantity: " + quantity);
                System.out.println("  Total: $" + String.format("%.2f", totalPrice));
                checkBalance();
                
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("[ERROR] Order failed: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
            
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid input");
        } catch (SQLException e) {
            System.out.println("[ERROR] Order failed: " + e.getMessage());
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Failed to reset auto-commit: " + ex.getMessage());
            }
        }
    }
    
    /**
     * View user's order history
     */
    private void viewMyOrders() {
        String sql = "SELECT o.order_id, m.name, o.quantity, o.total_price, o.order_date " +
                    "FROM orders o JOIN menu_items m ON o.item_id = m.item_id " +
                    "WHERE o.user_id = ? ORDER BY o.order_date DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUserId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n=== MY ORDERS ===");
                System.out.println("ID   Item                Qty   Total     Date");
                System.out.println("-------------------------------------------------------");
                
                boolean hasOrders = false;
                while (rs.next()) {
                    hasOrders = true;
                    System.out.printf("%-4d %-20s %-5d $%-8.2f %s%n",
                        rs.getInt("order_id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("total_price"),
                        rs.getTimestamp("order_date").toString().substring(0, 16));
                }
                
                if (!hasOrders) {
                    System.out.println("No orders yet");
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to load orders: " + e.getMessage());
        }
    }
    
    /**
     * Check wallet balance
     */
    private void checkBalance() {
        String sql = "SELECT wallet_balance FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUserId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double balance = rs.getDouble("wallet_balance");
                    System.out.println("$ Current Balance: $" + String.format("%.2f", balance));
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to check balance: " + e.getMessage());
        }
    }
}
