package com.inventorymanagementsystem.view;

import com.inventorymanagementsystem.controller.admin.AdminController;
import com.inventorymanagementsystem.controller.DBConnectionController;
import com.inventorymanagementsystem.controller.LoginController;
import com.inventorymanagementsystem.controller.staff.StaffController;
import com.inventorymanagementsystem.enums.AdminMenuOptions;
import com.inventorymanagementsystem.enums.StaffMenuOptions;
import com.inventorymanagementsystem.model.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewFactory {
    //Admin Views
    private final ObjectProperty<AdminMenuOptions> adminSelectedMenuItem;
    private AnchorPane manageStaffView;
    private AnchorPane suppliersView;
    private AnchorPane purchaseOrdersView;
    private AnchorPane reportsView;
    private AnchorPane accountView;

    //Staff View
    private final ObjectProperty<StaffMenuOptions> staffSelectedMenuItem;

    //shared Views
    private AnchorPane viewInventoryView;
    private AnchorPane inventoryBatchesView;
    private AnchorPane alertsView;
    private AnchorPane historyView;

    private final Image image = new Image(getClass().getResourceAsStream("/img/Inventory-Management-System_Icon.png"));

    public ViewFactory(){
        this.adminSelectedMenuItem = new SimpleObjectProperty<>();
        this.staffSelectedMenuItem = new SimpleObjectProperty<>();
    }

    public ObjectProperty<AdminMenuOptions> getAdminSelectedMenuItem() {
        return adminSelectedMenuItem;
    }

    public ObjectProperty<StaffMenuOptions> getStaffSelectedMenuItem(){
        return staffSelectedMenuItem;
    }

    public AnchorPane getViewInventoryView(){
        if(viewInventoryView == null){
            try{
                viewInventoryView = new FXMLLoader(getClass().getResource("/fxml/viewInventoryView.fxml")).load();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        return viewInventoryView;
    }

    public AnchorPane getInventoryBatchesView(){
        if(inventoryBatchesView == null){
            try{
                inventoryBatchesView = new FXMLLoader(getClass().getResource("/fxml/inventoryBatchesView.fxml")).load();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        return inventoryBatchesView;
    }

    public AnchorPane getAlertsView(){
        if(alertsView == null){
            try{
                alertsView = new FXMLLoader(getClass().getResource("/fxml/alertsView.fxml")).load();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        return alertsView;
    }

    public AnchorPane getManageStaffView(){
        if(manageStaffView == null){
            try{
                manageStaffView = new FXMLLoader(getClass().getResource("/fxml/admin/manageStaffView.fxml")).load();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        return manageStaffView;
    }

    public AnchorPane getSuppliersView(){
        if(suppliersView == null){
            try{
                suppliersView = new FXMLLoader(getClass().getResource("/fxml/admin/suppliersView.fxml")).load();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        return suppliersView;
    }

    public AnchorPane getPurchaseOrdersView(){
        if(purchaseOrdersView == null){
            try{
                purchaseOrdersView = new FXMLLoader(getClass().getResource("/fxml/admin/purchaseOrdersView.fxml")).load();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        return purchaseOrdersView;
    }

    public AnchorPane getReportsView(){
        if(reportsView == null){
            try{
                reportsView = new FXMLLoader(getClass().getResource("/fxml/admin/reportsView.fxml")).load();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        return reportsView;
    }

    public AnchorPane getHistoryView(){
        if(historyView == null){
            try{
                historyView = new FXMLLoader(getClass().getResource("/fxml/history.fxml")).load();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        return historyView;
    }

    public AnchorPane getAccountView(){
        if(accountView == null){
            try{
                accountView = new FXMLLoader(getClass().getResource("/fxml/admin/account.fxml")).load();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        return accountView;
    }

    public void showAdminWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/admin.fxml"));
        AdminController adminController = new AdminController();
        loader.setController(adminController);
        createStage(loader);
    }

    public void showStaffWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/staff/staff.fxml"));
        StaffController staffController = new StaffController();
        loader.setController(staffController);
        createStage(loader);
    }

    public void showSignUpWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signUpView.fxml"));
        createStage(loader, "Sign Up");
    }

    public void showLoginWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/loginView.fxml"));
        createStage(loader);
    }

    public void showDataBaseConnectionWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DBConnection.fxml"));
        createStage(loader);
    }

    public void dataBaseConnectionWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DBConnection.fxml"));
        DBConnectionController dbController = null;

        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Database Connection");
            stage.setResizable(false);

            stage.getIcons().add(image);

            stage.setOnCloseRequest(event -> {
                onExit();
            });

            dbController = loader.getController();
            dbController.shouldShow();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/loginView.fxml"));
        LoginController loginController = null;

        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Inventory Management");
            stage.setResizable(false);

            stage.getIcons().add(image);

            stage.setOnCloseRequest(event -> {
                onExit();
            });

            loginController = loader.getController();
            loginController.shouldShow();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createStage(FXMLLoader loader){
        try{
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Inventory Management");

            stage.getIcons().add(image);

            stage.setOnCloseRequest(event -> {
                onExit();
            });

            stage.show();
            stage.setResizable(false);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void createStage(FXMLLoader loader, String title){
        try{
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(title);

            stage.getIcons().add(image);

            stage.setOnCloseRequest(event -> {
                onExit();
            });

            stage.show();
            stage.setResizable(false);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void onExit() {
        System.out.println("Application is closing...");

        if (Model.getInstance().getDataBaseDriver().connectionIsNotNull() && !Model.getInstance().getDataBaseDriver().connectionIsClosed()) {
            Model.getInstance().getDataBaseDriver().closeConnection();
            System.out.println("Connection was not null and successfully closed!!");
        }
        else if(Model.getInstance().getDataBaseDriver().connectionIsNotNull() && Model.getInstance().getDataBaseDriver().connectionIsClosed()){
            System.out.println("Connection was not null and was already closed!!");
        }
        else{
            System.out.println("Connection was equal to null...");
        }
    }

    public void closeStage(Stage stage){
        stage.close();
    }
}
