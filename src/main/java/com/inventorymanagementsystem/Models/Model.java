package com.inventorymanagementsystem.Models;

import com.inventorymanagementsystem.Config.DataBaseDriver;
import com.inventorymanagementsystem.Views.ViewFactory;

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
}
