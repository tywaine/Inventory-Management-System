package com.inventorymanagementsystem.enums;

public enum PreferenceKeys {
    DB_HOST("inventoryManagementSystem_dbHost"),
    DB_PORT("inventoryManagementSystem_dbPort"),
    DB_AUTH("inventoryManagementSystem_dbAuthentication"),
    DB_USERNAME("inventoryManagementSystem_dbUsername"),
    DB_PASSWORD("inventoryManagementSystem_dbPassword"),
    DB_SAVE_PASSWORD("inventoryManagementSystem_dbSavePassword"),
    DB_DATABASE("inventoryManagementSystem_dbDataBase"),
    USER_IDENTITY("inventoryManagementSystem_UserEmail"),
    USER_PASSWORD("inventoryManagementSystem_UserPassword");
    private final String key;

    PreferenceKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
