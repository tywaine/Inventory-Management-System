package com.inventorymanagementsystem.model;

import com.inventorymanagementsystem.config.DataBaseDriver;
import com.inventorymanagementsystem.view.ViewFactory;

import java.time.LocalDateTime;

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

    public void setCurrentUser(int userId, String name, String role, String email, LocalDateTime createdAt){
        this.user = new User(userId, name, role, email, createdAt);
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
}
