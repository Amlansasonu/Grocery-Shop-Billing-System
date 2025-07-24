# Grocery Shop Billing System 🛒

This is a **Java Swing GUI application** for a supermarket/grocery shop billing system. It uses **MySQL** as a database and generates PDF receipts using the **iText** library.

---

## ✨ Features

- 🧾 Add items to bill with quantity and price calculation
- 📦 Manage products (CRUD operations)
- 📉 Low stock warning
- 📊 Real-time billing display
- 🧑‍💼 Login screen (admin access)
- 📄 PDF receipt generation using iText

---

## 🛠️ Technologies Used

- Java (Swing)
- MySQL (JDBC)
- iText PDF (v5.5.13)
- Git + GitHub

---

## 🔧 Setup Instructions

1. Install MySQL and create the database:

```sql
CREATE DATABASE grocery_store;

USE grocery_store;

CREATE TABLE users (
  username VARCHAR(50) PRIMARY KEY,
  password VARCHAR(50) NOT NULL
);

INSERT INTO users VALUES ('admin', 'admin123');

CREATE TABLE products (
  product_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  price DOUBLE NOT NULL,
  stock INT NOT NULL
);

INSERT INTO products (name, price, stock) VALUES
('Rice', 50.0, 100),
('Sugar', 40.0, 50),
('Oil', 120.0, 80),
('Milk', 25.0, 60),
('Bread', 30.0, 70);
