package com.inventorymanagementsystem.Models;

import javafx.scene.control.Alert.AlertType;

import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

public class DataBaseManager {

    public static boolean userEmailExists(String email){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT COUNT(*) FROM Users WHERE email = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                resultSet.close();
                return count > 0;
            }

            resultSet.close();
        } catch (SQLException e) {
            System.err.println("Error checking Email: " + e.getMessage());
        }

        return false;
    }

    public static boolean userIdentityExist(String identity){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query;

        if(isInteger(identity)){
            query = "SELECT COUNT(*) FROM Users WHERE user_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, Integer.parseInt(identity));
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    resultSet.close();
                    return count > 0;
                }

                resultSet.close();
            } catch (SQLException e){
                e.printStackTrace();
                System.err.println("SQL Error: Could not check user existence.");
            }
        }
        else{
            query = "SELECT COUNT(*) FROM Users WHERE email = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, identity);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    resultSet.close();
                    return count > 0;
                }

                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("SQL Error: Could not check user existence.");
            }
        }

        return false;
    }

    public static boolean validUserIdentity(String identity, String password){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query;

        if(isInteger(identity)){
            query = "SELECT password_hash FROM Users WHERE user_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, Integer.parseInt(identity));
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String passwordHashFromDb = resultSet.getString("password_hash");
                    resultSet.close();
                    return MyBCrypt.isPasswordEqual(password, passwordHashFromDb);
                }

                resultSet.close();
            } catch (SQLException e){
                e.printStackTrace();
                System.err.println("SQL Error: Could not verify password.");
            }
        }
        else{
            query = "SELECT password_hash FROM Users WHERE email = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, identity);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String passwordHashFromDb = resultSet.getString("password_hash");
                    resultSet.close();
                    return MyBCrypt.isPasswordEqual(password, passwordHashFromDb);
                }

                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("SQL Error: Could not verify password.");
            }
        }

        return false;
    }

    private static boolean isInteger(String identity){
        try{
            int identity2 = Integer.parseInt(identity);
            return true;
        }catch(NumberFormatException _){
            return false;
        }
    }

    public static boolean validUser(String email, String password){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT password_hash FROM Users WHERE email = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String passwordHashFromDb = resultSet.getString("password_hash");
                resultSet.close();
                return MyBCrypt.isPasswordEqual(password, passwordHashFromDb);
            }
            else {
                resultSet.close();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
        }

        return false;
    }

    public static User getUserFromIdentity(String identity){
        if(isInteger(identity)){
            return getUser(Integer.parseInt(identity));
        }
        else{
            return getUser(identity);
        }
    }

    public static User getUser(String email){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT * FROM Users WHERE email = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                int userId = resultSet.getInt("user_id");
                String name = resultSet.getString("name");
                String role = resultSet.getString("role");
                LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                resultSet.close();
                return new User(userId, name, role, email, createdAt);
            }

            resultSet.close();
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public static User getUser(int userId){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT * FROM Users WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                String name = resultSet.getString("name");
                String role = resultSet.getString("role");
                String email = resultSet.getString("email");
                LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                resultSet.close();
                return new User(userId, name, role, email, createdAt);
            }

            resultSet.close();
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public static String getAdminEmailPassword(User user) {
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT email_password_encrypted FROM Admins WHERE user_id = ?";
        String decryptedPassword = null;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.ID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String encryptedPassword = resultSet.getString("email_password_encrypted");
                decryptedPassword = EncryptionUtils.decrypt(encryptedPassword);
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }

        return decryptedPassword;
    }

    public static boolean isSupplierEmailTaken(String email) {
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT COUNT(*) FROM Suppliers WHERE contact_email = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                resultSet.close();
                return count > 0;
            }

            resultSet.close();
        } catch (SQLException e) {
            System.err.println("Error checking Email: " + e.getMessage());
        }
        return false;
    }

    public static boolean isSupplierPhoneNumTaken(String phoneNumber) {
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT COUNT(*) FROM Suppliers WHERE phone_number = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, phoneNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                resultSet.close();
                return count > 0;
            }

            resultSet.close();
        } catch (SQLException e) {
            System.err.println("Error checking PhoneNumber: " + e.getMessage());
        }
        return false;
    }

    private static void loadStaff(){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT * FROM Users WHERE role = 'STAFF'";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while(resultSet.next()){
                int userId = resultSet.getInt("user_id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();

                new User(userId, name, "STAFF", email, createdAt);
            }

        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error loading Staff",
                    e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loadBatches(){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT * FROM Batches";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while(resultSet.next()){
                int batchId = resultSet.getInt("batch_id");
                int productId = resultSet.getInt("product_id");
                int currentStock = resultSet.getInt("current_stock");
                LocalDate expirationDate = resultSet.getDate("expiration_date").toLocalDate();

                new Batch(batchId, productId, currentStock, expirationDate);
            }

        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error loading Batches",
                    e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loadInventoryAdjustments() {
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT * FROM InventoryAdjustments";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int adjustmentId = resultSet.getInt("adjustment_id");
                int user_id = resultSet.getInt("user_id");
                String user_role = resultSet.getString("user_role");
                int productId = resultSet.getInt("product_id");
                String productName = resultSet.getString("product_name");

                int batchId = resultSet.getInt("batch_id");
                if (resultSet.wasNull()) {
                    batchId = -1;
                }

                LocalDateTime adjustmentDate = resultSet.getTimestamp("adjustment_datetime").toLocalDateTime();
                String adjustmentType = resultSet.getString("adjustment_type");

                int previousStock = resultSet.getInt("previous_stock");
                if (resultSet.wasNull()) {
                    previousStock = -1;
                }

                int adjustedStock = resultSet.getInt("adjusted_stock");
                if (resultSet.wasNull()) {
                    adjustedStock = -1;
                }

                new InventoryAdjustment(adjustmentId, user_id, user_role, productId, productName,
                        batchId, adjustmentDate, adjustmentType, previousStock, adjustedStock);
            }

        } catch (SQLException e) {
            Model.getInstance().showAlert(AlertType.ERROR, "Error loading Inventory Adjustments",
                    e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loadProducts(){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT * FROM Products";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while(resultSet.next()){
                int productId = resultSet.getInt("product_id");
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                BigDecimal unitPrice = resultSet.getBigDecimal("unit_price");
                int lowStockAmount = resultSet.getInt("low_stock_amount");

                new Product(productId, name, category, unitPrice, lowStockAmount);
            }

        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error loading Products",
                    e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loadPurchaseOrders(){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT * FROM PurchaseOrders";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while(resultSet.next()){
                int orderId = resultSet.getInt("order_id");
                LocalDate orderDate = resultSet.getDate("order_date").toLocalDate();
                int supplierId = resultSet.getInt("supplier_id");
                String supplierName = resultSet.getString("supplier_name");
                String productName = resultSet.getString("product_name");
                int quantity = resultSet.getInt("quantity");
                BigDecimal totalAmount = resultSet.getBigDecimal("total_amount");

                new PurchaseOrder(orderId, orderDate, supplierId, supplierName, productName, quantity, totalAmount);
            }

        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error loading Purchase Orders",
                    e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loadSales(){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT * FROM Sales";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while(resultSet.next()){
                int saleId = resultSet.getInt("sale_id");
                int productId = resultSet.getInt("product_id");
                String productName = resultSet.getString("product_name");
                LocalDate saleDate = resultSet.getDate("sale_date").toLocalDate();
                int quantitySold = resultSet.getInt("quantity_sold");
                BigDecimal salePrice = resultSet.getBigDecimal("sale_price");

                new Sale(saleId, productId, productName, saleDate, quantitySold, salePrice);
            }

        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error loading Sales",
                    e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loadSuppliers(){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT * FROM Suppliers";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while(resultSet.next()){
                int saleId = resultSet.getInt("supplier_id");
                String name = resultSet.getString("name");
                String contactEmail = resultSet.getString("contact_email");
                String phoneNumber = resultSet.getString("phone_number");
                String address = resultSet.getString("address");

                new Supplier(saleId, name, contactEmail, phoneNumber, address);
            }
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error loading Suppliers",
                    e.getMessage());
            e.printStackTrace();
        }
    }


    // For adding to database
    public static void addAdmin(int userId, String emailPassword) {
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "INSERT INTO Admins (user_id, email_password_encrypted) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setString(2, EncryptionUtils.encrypt(emailPassword));

            int rowsAdded = statement.executeUpdate();
            addMessage("Admin", rowsAdded);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error: Could not add admin.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: Encryption failed.");
        }
    }

    public static void addBatch(int productId, int currentStock, LocalDate expirationDate){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "INSERT INTO Batches (product_id, current_stock, expiration_date) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            statement.setInt(2, currentStock);
            statement.setDate(3, Date.valueOf(expirationDate));
            int rowsAdded = statement.executeUpdate();
            int batchId = -1;

            if(rowsAdded > 0){
                batchId = getLastBatchID();
                new Batch(batchId, productId, currentStock, expirationDate);
            }

            addMessage("Batch", batchId, rowsAdded);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error adding Batch",
                    e.getMessage());
        }
    }

    public static void addInventoryAdjustment(int userId, String userRole, int productId, String productName, int batchId,
                                              LocalDateTime adjustmentDatetime, String adjustmentType, int previous_stock, int adjusted_stock){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "INSERT INTO InventoryAdjustments (user_id, user_role, product_id, product_name, batch_id, adjustment_datetime, adjustment_type, previous_stock, adjusted_stock)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setString(2, userRole);
            statement.setInt(3, productId);
            statement.setString(4, productName);

            if(batchId == -1){
                statement.setNull(5, Types.INTEGER);
            }
            else{
                statement.setInt(5, batchId);
            }

            statement.setTimestamp(6, Timestamp.valueOf(adjustmentDatetime));
            statement.setString(7, adjustmentType);

            if(previous_stock == -1){
                statement.setNull(8, Types.INTEGER);
            }
            else{
                statement.setInt(8, previous_stock);
            }

            if(adjusted_stock == -1){
                statement.setNull(9, Types.INTEGER);
            }
            else{
                statement.setInt(9, adjusted_stock);
            }

            int rowsAdded = statement.executeUpdate();
            int adjustment_id = -1;

            if(rowsAdded > 0){
                adjustment_id = getLastInventoryAdjustmentID();
                new InventoryAdjustment(adjustment_id, userId, userRole, productId, productName, batchId,
                        adjustmentDatetime, adjustmentType, previous_stock, adjusted_stock);
            }

            addMessage("Inventory Adjustment", adjustment_id, rowsAdded);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error adding Inventory Adjustment",
                    e.getMessage());
        }
    }

    public static void addProduct(String name, String category, BigDecimal unitPrice, int lowStockAmount){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "INSERT INTO Products (name, category, unit_price, low_stock_amount) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, category);
            statement.setBigDecimal(3, unitPrice);
            statement.setInt(4, lowStockAmount);
            int rowsAdded = statement.executeUpdate();
            int productId = -1;

            if(rowsAdded > 0){
                productId = getLastProductID();
                new Product(productId, name, category, unitPrice, lowStockAmount);
            }

            addMessage("Product", productId, rowsAdded);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error adding Product",
                    e.getMessage());
        }
    }

    public static void addPurchaseOrder(LocalDate orderDate, int supplierId, String supplierName,
                                        String productName, int quantity, BigDecimal totalAmount){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "INSERT INTO PurchaseOrders (order_date, supplier_id, supplier_name," +
                " product_name, quantity, total_amount) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(orderDate));
            statement.setInt(2, supplierId);
            statement.setString(3, supplierName);
            statement.setString(4, productName);
            statement.setInt(5, quantity);
            statement.setBigDecimal(6, totalAmount);
            int rowsAdded = statement.executeUpdate();
            int orderID = -1;

            if(rowsAdded > 0){
                orderID = getLastPurchaseOrderID();
                new PurchaseOrder(orderID, orderDate, supplierId, supplierName, productName, quantity, totalAmount);
            }

            addMessage("Purchase Order", orderID, rowsAdded);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error adding Purchase Order",
                    e.getMessage());
        }
    }

    public static void addSale(int productId, String productName, LocalDate saleDate, int quantitySold, BigDecimal salePrice){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "INSERT INTO Sales (product_id, product_name, sale_date, quantity_sold, sale_price) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            statement.setString(2, productName);
            statement.setDate(3, Date.valueOf(saleDate));
            statement.setInt(4, quantitySold);
            statement.setBigDecimal(5, salePrice);
            int rowsAdded = statement.executeUpdate();
            int saleId = -1;

            if(rowsAdded > 0){
                saleId = getLastSaleID();
                new Sale(saleId, productId, productName, saleDate, quantitySold, salePrice);
            }

            addMessage("Sale", saleId, rowsAdded);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error adding Sale",
                    e.getMessage());
        }
    }

    public static void addSupplier(String name, String contactEmail, String phoneNumber, String address){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "INSERT INTO Suppliers (name, contact_email, phone_number, address) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, contactEmail);
            statement.setString(3, phoneNumber);
            statement.setString(4, address);
            int rowsAdded = statement.executeUpdate();
            int supplierId = -1;

            if(rowsAdded > 0){
                supplierId = getLastSupplierID();
                new Supplier(supplierId, name, contactEmail, phoneNumber, address);
            }

            addMessage("Supplier", supplierId, rowsAdded);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error adding Supplier",
                    e.getMessage());
        }
    }

    public static void addUser(String name, String role, String password, String email){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "INSERT INTO Users (name, password_hash, role, email) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, MyBCrypt.hashPassword(password));
            statement.setString(3, role);
            statement.setString(4, email);
            int rowsAdded = statement.executeUpdate();
            int userId = -1;

            if(rowsAdded > 0){
                userId = getLastUserID();
                new User(userId, name, role, email, getUserCreated_at(userId));
            }

            addMessage("User", userId, rowsAdded);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error adding User",
                    e.getMessage());
        }
    }

    public static void addUser(String name, String password, String email){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "INSERT INTO Users (name, password_hash, email) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, MyBCrypt.hashPassword(password));
            statement.setString(3, email);
            int rowsAdded = statement.executeUpdate();
            int userId = -1;

            if(rowsAdded > 0){
                userId = getLastUserID();
                new User(userId, name, "ADMIN", email, getUserCreated_at(userId));
            }

            addMessage("User", userId, rowsAdded);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error adding User",
                    e.getMessage());
        }
    }


    // For update info to the database
    public static void updateAdmin(User user, String emailPassword){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "UPDATE Admins SET email_password_encrypted = ? WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, EncryptionUtils.encrypt(emailPassword));
            statement.setInt(2, user.ID);
            int rowsUpdated = statement.executeUpdate();

            updateMessage("Batch", user.ID, rowsUpdated);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error updating Admin",
                    e.getMessage());
        }
    }


    public static void updateBatch(Batch batch, int productId, int currentStock, LocalDate expirationDate){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "UPDATE Batches SET product_id = ?, current_stock = ?, expiration_date = ? WHERE batch_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            statement.setInt(2, currentStock);
            statement.setDate(3, Date.valueOf(expirationDate));
            statement.setInt(4, batch.ID);
            int rowsUpdated = statement.executeUpdate();

            if(rowsUpdated > 0){
                Batch.update(batch, productId, currentStock, expirationDate);
            }

            updateMessage("Batch", batch.ID, rowsUpdated);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error updating Batch",
                    e.getMessage());
        }
    }

    public static void updateProduct(Product product, String name, String category, BigDecimal unitPrice, int lowStockAmount){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "UPDATE Products SET name = ?, category = ?, unit_price = ?, low_stock_amount = ? WHERE product_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, category);
            statement.setBigDecimal(3, unitPrice);
            statement.setInt(4, lowStockAmount);
            statement.setInt(5, product.ID);
            int rowsUpdated = statement.executeUpdate();

            if(rowsUpdated > 0){
                Product.update(product, name, category, unitPrice, lowStockAmount);
            }

            updateMessage("Product", product.ID, rowsUpdated);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error updating Product",
                    e.getMessage());
        }
    }

    public static void updateSupplier(Supplier supplier, String name, String contactEmail, String phoneNumber, String address){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "UPDATE Suppliers SET name = ?, contact_email = ?, phone_number = ?, address = ? WHERE supplier_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, contactEmail);
            statement.setString(3, phoneNumber);
            statement.setString(4, address);
            statement.setInt(5, supplier.ID);
            int rowsUpdated = statement.executeUpdate();

            if(rowsUpdated > 0){
                Supplier.update(supplier, name, contactEmail, phoneNumber, address);
            }

            updateMessage("Supplier", supplier.ID, rowsUpdated);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error updating Supplier",
                    e.getMessage());
        }
    }

    public static void updateUser(User user, String name, String email){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "UPDATE Users SET name = ?,  email = ? WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setInt(3, user.ID);
            int rowsUpdated = statement.executeUpdate();

            if(rowsUpdated > 0){
                User.update(user, name, email);
            }

            updateMessage("User", user.ID, rowsUpdated);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error updating User",
                    e.getMessage());
        }
    }

    public static void updateUser(User user, String name, String password, String email){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "UPDATE Users SET name = ?, password_hash = ?, email = ? WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, MyBCrypt.hashPassword(password));
            statement.setString(3, email);
            statement.setInt(4, user.ID);
            int rowsUpdated = statement.executeUpdate();

            if(rowsUpdated > 0){
                User.update(user, name, email);
            }

            updateMessage("User", user.ID, rowsUpdated);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error updating User",
                    e.getMessage());
        }
    }


    // For deleting info from the database
    public static void deleteBatch(Batch batch){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "DELETE FROM Batches WHERE batch_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, batch.ID);
            int rowsDeleted = statement.executeUpdate();

            if(rowsDeleted > 0){
                Batch.remove(batch);
            }

            deleteMessage("Batch", batch.ID, rowsDeleted);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error deleting Batch",
                    e.getMessage());
        }
    }

    public static void deleteInventoryAdjustment(InventoryAdjustment inventoryAdjustment){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "DELETE FROM InventoryAdjustments WHERE adjustment_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, inventoryAdjustment.ID);
            int rowsDeleted = statement.executeUpdate();

            if(rowsDeleted > 0){
                InventoryAdjustment.remove(inventoryAdjustment);
            }

            deleteMessage("Inventory Adjustment", inventoryAdjustment.ID, rowsDeleted);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error deleting Inventory Adjustment",
                    e.getMessage());
        }
    }

    public static void deleteProduct(Product product){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "DELETE FROM Products WHERE product_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, product.ID);
            int rowsDeleted = statement.executeUpdate();

            if(rowsDeleted > 0){
                Product.remove(product);
            }

            deleteMessage("Product", product.ID, rowsDeleted);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error deleting Product",
                    e.getMessage());
        }
    }

    public static void deletePurchaseOrder(PurchaseOrder purchaseOrder){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "DELETE FROM PurchaseOrders WHERE order_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, purchaseOrder.ID);
            int rowsDeleted = statement.executeUpdate();

            if(rowsDeleted > 0){
                PurchaseOrder.remove(purchaseOrder);
            }

            deleteMessage("Purchase Order", purchaseOrder.ID, rowsDeleted);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error deleting Purchase Order",
                    e.getMessage());
        }
    }

    public static void deleteSale(Sale sale){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "DELETE FROM Sales WHERE sale_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, sale.ID);
            int rowsDeleted = statement.executeUpdate();

            if(rowsDeleted > 0){
                Sale.remove(sale);
            }

            deleteMessage("Sale", sale.ID, rowsDeleted);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error deleting Sale",
                    e.getMessage());
        }
    }

    public static void deleteSupplier(Supplier supplier){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "DELETE FROM Suppliers WHERE supplier_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, supplier.ID);
            int rowsDeleted = statement.executeUpdate();

            if(rowsDeleted > 0){
                Supplier.remove(supplier);
            }

            deleteMessage("Supplier", supplier.ID, rowsDeleted);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error deleting Supplier",
                    e.getMessage());
        }
    }

    public static void deleteStaff(User staff){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "DELETE FROM Users WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, staff.ID);
            int rowsDeleted = statement.executeUpdate();

            if(rowsDeleted > 0){
                User.removeStaff(staff);
            }

            deleteMessage("User with role: Staff", staff.ID, rowsDeleted);
        }catch(SQLException e){
            Model.getInstance().showAlert(AlertType.ERROR, "Error deleting Supplier",
                    e.getMessage());
        }
    }

    public static int getLastBatchID() {
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT batch_id FROM Batches ORDER BY batch_id DESC LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt("batch_id");
            }
            else {
                System.out.println("No Batches found in the database.");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getLastInventoryAdjustmentID() {
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT adjustment_id FROM InventoryAdjustments ORDER BY adjustment_id DESC LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt("adjustment_id");
            }
            else {
                System.out.println("No Inventory Adjustments found in the database.");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getLastProductID() {
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT product_id FROM Products ORDER BY product_id DESC LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt("product_id");
            }
            else {
                System.out.println("No Products found in the database.");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getLastPurchaseOrderID() {
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT order_id FROM PurchaseOrders ORDER BY order_id DESC LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt("order_id");
            }
            else {
                System.out.println("No Purchase Orders found in the database.");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getLastSaleID() {
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT sale_id FROM Sales ORDER BY sale_id DESC LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt("sale_id");
            }
            else {
                System.out.println("No Sales found in the database.");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getLastSupplierID() {
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT supplier_id FROM Suppliers ORDER BY supplier_id DESC LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt("supplier_id");
            }
            else {
                System.out.println("No Suppliers found in the database.");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getLastUserID() {
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT user_id FROM Users ORDER BY user_id DESC LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            }
            else {
                System.out.println("No Users found in the database.");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static LocalDateTime getUserCreated_at(int userId){
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT created_at FROM Users WHERE user_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                LocalDateTime createAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                resultSet.close();
                return createAt;
            }
            else {
                System.out.println("No Users found in the database.");
            }

            resultSet.close();
        } catch (SQLException e) {
            Model.getInstance().showAlert(AlertType.ERROR, "Error with User Created_at", e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public static boolean doesAdminExists() {
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();
        String query = "SELECT COUNT(*) FROM Users WHERE role = 'ADMIN'";

        try(PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()){

            if (resultSet.next()) {
                int userCount = resultSet.getInt(1);
                return userCount > 0;
            }
        } catch (SQLException e) {
            System.err.println("Does Admin Exist Error: " + e.getMessage());
        }

        return false;
    }

    public static void ensureTablesExist() {
        Connection connection = Model.getInstance().getDataBaseDriver().getConnection();

        try (InputStream inputStream = DataBaseManager.class.getResourceAsStream("/create_tables.sql")) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 Statement statement = connection.createStatement()) {

                StringBuilder sqlBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                        continue;
                    }

                    sqlBuilder.append(line.trim()).append(" ");

                    if (line.trim().endsWith(";")) {
                        statement.execute(sqlBuilder.toString());
                        sqlBuilder.setLength(0);
                    }
                }

                if (!sqlBuilder.isEmpty()) {
                    statement.execute(sqlBuilder.toString());
                }

            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addMessage(String tableName, int rowsAdded){
        if (rowsAdded > 0) {
            System.out.println(tableName + " was added");
        }
        else {
            System.out.println(tableName + " was not added");
        }
    }

    private static void addMessage(String tableName, int id, int rowsAdded){
        if (rowsAdded > 0) {
            System.out.println(tableName + " with ID Number " + id + " was added");
        }
        else {
            System.out.println(tableName + " with ID Number " + id + " was not added");
        }
    }

    private static void updateMessage(String tableName, int id, int rowsUpdated){
        if (rowsUpdated > 0) {
            System.out.println(tableName + " with ID " + id + " updated successfully.");
        }
        else {
            System.out.println("No " + tableName + " found with ID " + id);
        }
    }

    private static void deleteMessage(String tableName, int Id, int rowsDeleted){
        if (rowsDeleted > 0) {
            System.out.println(tableName + " with ID " + Id + " deleted successfully.");
        }
        else {
            System.out.println("No " + tableName + " found with ID " + Id);
        }
    }

    public static void loadInfo() {
        loadBatches();
        loadInventoryAdjustments();
        loadProducts();
        loadPurchaseOrders();
        loadSales();
        loadSuppliers();
        loadStaff();
    }
    
}