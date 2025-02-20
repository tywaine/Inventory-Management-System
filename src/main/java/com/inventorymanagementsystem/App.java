package com.inventorymanagementsystem;
import com.inventorymanagementsystem.model.Model;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Model.getInstance().getViewFactory().dataBaseConnectionWindow();
    }
}
