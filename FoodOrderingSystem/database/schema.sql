-- ========================================
-- Food Ordering System - Database Schema
-- Database: food_ordering_system (must be created manually)
-- Author: Jayanth
-- ========================================
-- Run: CREATE DATABASE food_ordering_system;
-- Then execute this script to create tables
-- ========================================

-- Drop existing tables if they exist
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS users;

-- Users table - stores user accounts and wallet balance
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    wallet_balance DECIMAL(10, 2) DEFAULT 0.00
);

-- Menu items table - stores food items
CREATE TABLE menu_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0
);

-- Orders table - stores order history
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id)
);

-- Sample menu items
INSERT INTO menu_items (name, price, quantity) VALUES
('Burger', 5.99, 50),
('Pizza', 12.99, 30),
('Pasta', 8.99, 40),
('Sandwich', 4.99, 60),
('Salad', 6.49, 35),
('French Fries', 2.99, 100),
('Coke', 1.99, 150),
('Ice Cream', 3.49, 45),
('Coffee', 2.49, 80),
('Chicken Wings', 9.99, 25);
