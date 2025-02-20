module com.inventorymanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires mysql.connector.j;
    requires java.sql;
    requires jbcrypt;
    requires java.prefs;
    requires de.jensd.fx.glyphs.fontawesome;
    requires jakarta.mail;
    requires jakarta.activation;
    requires com.github.librepdf.openpdf;

    opens com.inventorymanagementsystem to javafx.fxml;
    exports com.inventorymanagementsystem;
    exports com.inventorymanagementsystem.controller;
    exports com.inventorymanagementsystem.controller.admin;
    exports com.inventorymanagementsystem.controller.staff;
    exports com.inventorymanagementsystem.model;
    exports com.inventorymanagementsystem.view;
    exports com.inventorymanagementsystem.config;
    exports com.inventorymanagementsystem.util;
    exports com.inventorymanagementsystem.enums;
}