package com.inventorymanagementsystem.Models;

import com.inventorymanagementsystem.Config.DataBaseDriver;
import com.inventorymanagementsystem.Views.ViewFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Model {
    private static Model model;
    private ViewFactory viewFactory;
    private final DataBaseDriver dataBaseDriver;
    private User user;

    private Model(){
        this.viewFactory = new ViewFactory();
        dataBaseDriver = new DataBaseDriver();
    }

    public static synchronized Model getInstance(){
        if(model == null){
            model = new Model();
        }

        return model;
    }

    public User getCurrentUser() {
        return user;
    }

    public void setCurrentUser(User user) {
        this.user = user;
    }

    public ViewFactory getViewFactory(){
        return viewFactory;
    }

    public void resetViewFactory(){
        viewFactory = new ViewFactory();
    }

    public DataBaseDriver getDataBaseDriver() {
        return dataBaseDriver;
    }

    public void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(content);
        alert.showAndWait();
    }

    public Alert getConfirmationDialogAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(content);

        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        return alert;
    }
}
