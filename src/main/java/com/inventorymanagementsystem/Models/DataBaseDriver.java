package com.inventorymanagementsystem.Models;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseDriver {
    private Connection connection;
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/Inventory_Management_System_2";
    private static final String MYSQL_USERNAME = "root";
    private static final String MYSQL_PASSWORD = "Itsincorrectidiotjeez1%%";

    public DataBaseDriver(){
        try{
            this.connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD);
        }catch(SQLException e){
            System.err.println("DatabaseDriver Error: " + e.getMessage());
            showAlert(AlertType.ERROR, "No Database Connection",
                    "No Database Connection Available");
            System.exit(1);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection(){
        try{
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean connectionIsNotNull(){
        return connection != null;
    }

    public boolean connectionIsClosed(){
        try{
            return connection.isClosed();
        }catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    public void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(content);
        alert.showAndWait();
    }
}
