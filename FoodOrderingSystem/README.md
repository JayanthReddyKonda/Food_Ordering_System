# Food Ordering System

Author: Jayanth

A console-based food ordering application with Java and MySQL. Users can register, manage wallets, and order food. Admins can manage the menu.

## Requirements

- Java JDK 8+
- MySQL Server (running locally or remotely)
- MySQL Client/Shell (`mysql` command-line tool) to run schema file
- MySQL Connector/J JAR file
- Database `food_ordering_system` must exist

## Setup

### 1. Create Database

```sql
CREATE DATABASE food_ordering_system;
```

### 2. Create Tables

Run this command in your terminal to import the tables into the existing `food_ordering_system` database:

```bash
mysql -u root -p food_ordering_system < database/schema.sql
```

### 3. Add MySQL Connector

Download from [MySQL Downloads](https://dev.mysql.com/downloads/connector/j/) and place JAR in `lib/` folder.

### 4. Update Credentials (if needed)

Edit `src/com/foodordering/Database.java`:

```java
URL = "jdbc:mysql://127.0.0.1:3306/food_ordering_system"
USER = "root"
PASSWORD = "jayanth"
```

### 5. Compile

```bash
# Windows
javac -cp "lib\*" -d bin src\com\foodordering\*.java
```

Or use: `.\compile.ps1` (Windows)

### 6. Run

```bash
# Windows
java -cp "bin;lib\*" com.foodordering.App
```

Or use: `.\run.ps1` (Windows)

## Usage

**User Mode:**

- Register/Login
- Add money to wallet
- View menu and place orders
- View order history
- Check balance

**Admin Mode:**

- Password: `admin123`
- Add/update menu items
- Update prices and stock
- View all orders

## Database Connection

- URL: `jdbc:mysql://127.0.0.1:3306/food_ordering_system`
- Username: `root`
- Password: `jayanth`

## Project Structure

```FolderStructure
FoodOrderingSystem/
├── src/com/foodordering/
│   ├── App.java          # Main entry point
│   ├── Database.java     # Connection utility
│   ├── UserMode.java     # User operations
│   └── AdminMode.java    # Admin operations
├── database/
│   └── schema.sql        # Database schema
└── lib/
    └── mysql-connector-*.jar
```
