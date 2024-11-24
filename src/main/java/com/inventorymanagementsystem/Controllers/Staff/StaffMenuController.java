package com.inventorymanagementsystem.Controllers.Staff;

import com.inventorymanagementsystem.Controllers.LoginController;
import com.inventorymanagementsystem.Models.Model;
import com.inventorymanagementsystem.Views.StaffMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class StaffMenuController implements Initializable {
    public Button btnViewInventory, btnInventoryBatch, btnAlerts, btnHistory, btnSignOut;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setIcons();
        addListeners();
    }

    private void setIcons() {
        btnViewInventory.setGraphic(createFontIcon(FontAwesomeSolid.BOXES, 16));
        btnInventoryBatch.setGraphic(createFontIcon(FontAwesomeSolid.CUBES, 16));
        btnAlerts.setGraphic(createFontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE, 16));
        btnHistory.setGraphic(createFontIcon(FontAwesomeSolid.HISTORY, 16));
        btnSignOut.setGraphic(createFontIcon(FontAwesomeSolid.SIGN_OUT_ALT, 14));
    }

    private FontIcon createFontIcon(FontAwesomeSolid iconType, int size) {
        FontIcon icon = new FontIcon(iconType);
        icon.setIconSize(size);
        icon.getStyleClass().add("icon");
        return icon;
    }

    private void addListeners(){
        btnViewInventory.setOnAction(event -> onViewInventory());
        btnInventoryBatch.setOnAction(event -> onInventoryBatch());
        btnAlerts.setOnAction(event -> onAlerts());
        btnHistory.setOnAction(event -> onHistory());
        btnSignOut.setOnAction(event -> onSignOut());
    }

    private void onViewInventory(){
        Model.getInstance().getViewFactory().getStaffSelectedMenuItem().set(StaffMenuOptions.VIEW_INVENTORY);
    }

    private void onInventoryBatch(){
        Model.getInstance().getViewFactory().getStaffSelectedMenuItem().set(StaffMenuOptions.INVENTORY_BATCHES);
    }

    private void onAlerts(){
        Model.getInstance().getViewFactory().getStaffSelectedMenuItem().set(StaffMenuOptions.ALERTS);
    }

    private void onHistory(){
        Model.getInstance().getViewFactory().getStaffSelectedMenuItem().set(StaffMenuOptions.HISTORY);
    }

    private void onSignOut(){
        Stage stage = (Stage) btnViewInventory.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().resetViewFactory();
        LoginController.removeCredentials();
        Model.getInstance().getViewFactory().showLoginWindow();
    }
}
