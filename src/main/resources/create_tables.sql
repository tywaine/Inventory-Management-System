CREATE TABLE IF NOT EXISTS Products (
                          product_id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          category VARCHAR(50),
                          unit_price DECIMAL(10, 2) NOT NULL,
                          low_stock_amount INT NOT NULL
);

CREATE TABLE IF NOT EXISTS Suppliers (
                           supplier_id INT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           contact_email VARCHAR(100),
                           phone_number VARCHAR(20),
                           address TEXT
);

CREATE TABLE IF NOT EXISTS Batches (
                         batch_id INT AUTO_INCREMENT PRIMARY KEY,
                         product_id INT NOT NULL,
                         current_stock INT NOT NULL,
                         expiration_date DATE NOT NULL,
                         FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

CREATE TABLE IF NOT EXISTS InventoryAdjustments (
                                      adjustment_id INT AUTO_INCREMENT PRIMARY KEY,
                                      user_id INT,
                                      user_role ENUM('ADMIN', 'STAFF') DEFAULT 'ADMIN',
                                      product_id INT NOT NULL,
                                      product_name VARCHAR(100),
                                      batch_id INT NULL,
                                      adjustment_datetime DATETIME NOT NULL,
                                      adjustment_type ENUM('ADDITION', 'DELETION', 'RESTOCK', 'SALE', 'ADJUSTMENT', 'UPDATE') NOT NULL,
                                      previous_stock INT NULL,
                                      adjusted_stock INT NULL
);

CREATE TABLE IF NOT EXISTS PurchaseOrders (
                                order_id INT AUTO_INCREMENT PRIMARY KEY,
                                order_date DATE NOT NULL,
                                supplier_id INT,
                                supplier_name VARCHAR(100) NOT NULL,
                                product_name VARCHAR(100) NOT NULL,
                                quantity INT NOT NULL,
                                total_amount DECIMAL(10, 2) DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS Sales (
                       sale_id INT AUTO_INCREMENT PRIMARY KEY,
                       product_id INT NOT NULL,
                       product_name VARCHAR(100) NOT NULL,
                       sale_date DATE NOT NULL,
                       quantity_sold INT NOT NULL,
                       sale_price DECIMAL(10, 2)
);

CREATE TABLE IF NOT EXISTS Users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role ENUM('ADMIN', 'STAFF') DEFAULT 'ADMIN',
                       email VARCHAR(100) NOT NULL UNIQUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Admins (
                        admin_id INT PRIMARY KEY AUTO_INCREMENT,
                        user_id INT NOT NULL UNIQUE,
                        email_password_encrypted VARCHAR(255),
                        FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

