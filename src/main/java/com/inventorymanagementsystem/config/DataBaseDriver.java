package com.inventorymanagementsystem.config;

import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseDriver {
    private Connection connection;

    public DataBaseDriver(){}

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection){
        this.connection = connection;
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
}
