package com.foodordering;

import java.util.Scanner;

/**
 * Simple Food Ordering System
 * A beginner-friendly console application with MySQL database
 * 
 * Author: Jayanth
 * 
 * Features:
 * - User registration and login
 * - Wallet management
 * - Menu browsing and ordering
 * - Admin menu management
 * 
 * Database: MySQL (food_ordering_system)
 * Connection: jdbc:mysql://127.0.0.1:3306/food_ordering_system
 */
public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Test database connection
        if (Database.getConnection() == null) {
            System.err.println("[ERROR] Cannot connect to database. Exiting...");
            return;
        }
        
        System.out.println("\n+=========================================+");
        System.out.println("|          FOOD ORDERING SYSTEM           |");
        System.out.println("+=========================================+");
        
        // Main application loop
        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. User Mode");
            System.out.println("2. Admin Mode");
            System.out.println("3. Exit");
            System.out.print("Choice: ");
            
            String choice = scanner.nextLine();
            
            if (choice.equals("1")) {
                UserMode userMode = new UserMode(scanner);
                userMode.start();
            } else if (choice.equals("2")) {
                AdminMode adminMode = new AdminMode(scanner);
                adminMode.start();
            } else if (choice.equals("3")) {
                System.out.println("\n[OK] Thank you for using Food Ordering System!");
                break;
            } else {
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
        
        // Cleanup
        Database.close();
        scanner.close();
    }
}
