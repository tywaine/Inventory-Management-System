package com.inventorymanagementsystem.Controllers.Staff;

import com.inventorymanagementsystem.Controllers.LoginController;
import com.inventorymanagementsystem.Models.Model;
import com.inventorymanagementsystem.Views.StaffMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class StaffMenuController implements Initializable {
    public Button btnViewInventory, btnInventoryBatch, btnAlerts, btnHistory, btnSignOut;
    public Label lblRole;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblRole.setText("ID Number: " + Model.getInstance().getCurrentUser().ID);
        setIcons();
        addListeners();
        btnViewInventory.getStyleClass().add("button-selected");
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

    private void addListeners() {
        addButtonListener(btnViewInventory, StaffMenuOptions.VIEW_INVENTORY);
        addButtonListener(btnInventoryBatch, StaffMenuOptions.INVENTORY_BATCHES);
        addButtonListener(btnAlerts, StaffMenuOptions.ALERTS);
        addButtonListener(btnHistory, StaffMenuOptions.HISTORY);
        btnSignOut.setOnAction(event -> onSignOut());
    }

    private void addButtonListener(Button button, StaffMenuOptions menuOption) {
        button.setOnAction(event -> {
            clearButtonStyles();
            button.getStyleClass().add("button-selected");
            Model.getInstance().getViewFactory().getStaffSelectedMenuItem().set(menuOption);
        });
    }

    private void clearButtonStyles() {
        btnViewInventory.getStyleClass().remove("button-selected");
        btnInventoryBatch.getStyleClass().remove("button-selected");
        btnAlerts.getStyleClass().remove("button-selected");
        btnHistory.getStyleClass().remove("button-selected");
    }

    private void onSignOut(){
        Stage stage = (Stage) btnViewInventory.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().resetViewFactory();
        LoginController.removeCredentials();
        Model.getInstance().getViewFactory().showLoginWindow();
    }
}
