package com.inventorymanagementsystem.controller.staff;

import com.inventorymanagementsystem.controller.LoginController;
import com.inventorymanagementsystem.model.Model;
import com.inventorymanagementsystem.enums.StaffMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

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
        btnViewInventory.setGraphic(createFontIcon(FontAwesomeIcon.ARCHIVE, 16));
        btnInventoryBatch.setGraphic(createFontIcon(FontAwesomeIcon.CUBES, 16));
        btnAlerts.setGraphic(createFontIcon(FontAwesomeIcon.EXCLAMATION_TRIANGLE, 16));
        btnHistory.setGraphic(createFontIcon(FontAwesomeIcon.HISTORY, 16));
        btnSignOut.setGraphic(createFontIcon(FontAwesomeIcon.SIGN_OUT, 14));
    }

    private FontAwesomeIconView createFontIcon(FontAwesomeIcon iconType, int size) {
        FontAwesomeIconView icon = new FontAwesomeIconView(iconType);
        icon.setSize(String.valueOf(size));
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
