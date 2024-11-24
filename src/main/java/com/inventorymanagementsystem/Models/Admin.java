package com.inventorymanagementsystem.Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Optional;

public class Admin {
    private final int ID;
    private final IntegerProperty userId;

    // Constructor
    public Admin(int adminId, int userId) {
        ID = adminId;
        this.userId = new SimpleIntegerProperty(userId);
    }


    public int getUserId() {
        return userId.get();
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }
}

