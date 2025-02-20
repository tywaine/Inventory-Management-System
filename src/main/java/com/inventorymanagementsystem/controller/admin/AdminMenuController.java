package com.inventorymanagementsystem.controller.admin;

import com.inventorymanagementsystem.controller.LoginController;
import com.inventorymanagementsystem.model.Model;
import com.inventorymanagementsystem.enums.AdminMenuOptions;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable {
    public Button btnViewInventory, btnInventoryBatch, btnAlerts, btnManageStaff, btnSuppliers,
            btnPurchaseOrders, btnReports, btnHistory, btnAccount, btnSignOut;
    public Label lblRole;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblRole.setText("ID Number: " + Model.getInstance().getCurrentUser().ID);
        setIcons();
        addListeners();
        btnViewInventory.getStyleClass().add("button-selected");
    }

    private void setIcons() {
        btnViewInventory.setGraphic(createFontIcon(FontAwesomeIcon.ARCHIVE, 18));
        btnInventoryBatch.setGraphic(createFontIcon(FontAwesomeIcon.CUBES, 18));
        btnAlerts.setGraphic(createFontIcon(FontAwesomeIcon.EXCLAMATION_TRIANGLE, 18));
        btnManageStaff.setGraphic(createFontIcon(FontAwesomeIcon.USERS, 18));
        btnSuppliers.setGraphic(createFontIcon(FontAwesomeIcon.TRUCK, 18));
        btnPurchaseOrders.setGraphic(createFontIcon(FontAwesomeIcon.CLIPBOARD, 18));
        btnReports.setGraphic(createFontIcon(FontAwesomeIcon.BAR_CHART, 18));
        btnHistory.setGraphic(createFontIcon(FontAwesomeIcon.HISTORY, 18));
        btnAccount.setGraphic(createFontIcon(FontAwesomeIcon.USER_CIRCLE, 18));
        btnSignOut.setGraphic(createFontIcon(FontAwesomeIcon.SIGN_OUT, 14));
    }

    private FontAwesomeIconView createFontIcon(FontAwesomeIcon iconType, int size) {
        FontAwesomeIconView icon = new FontAwesomeIconView(iconType);
        icon.setSize(String.valueOf(size));
        icon.getStyleClass().add("icon");
        return icon;
    }

    private void addListeners() {
        addButtonListener(btnViewInventory, AdminMenuOptions.VIEW_INVENTORY);
        addButtonListener(btnInventoryBatch, AdminMenuOptions.INVENTORY_BATCHES);
        addButtonListener(btnAlerts, AdminMenuOptions.ALERTS);
        addButtonListener(btnManageStaff, AdminMenuOptions.MANAGE_STAFF);
        addButtonListener(btnSuppliers, AdminMenuOptions.SUPPLIERS);
        addButtonListener(btnPurchaseOrders, AdminMenuOptions.PURCHASE_ORDERS);
        addButtonListener(btnReports, AdminMenuOptions.REPORTS);
        addButtonListener(btnHistory, AdminMenuOptions.HISTORY);
        addButtonListener(btnAccount, AdminMenuOptions.ACCOUNT);
        btnSignOut.setOnAction(event -> onSignOut());
    }

    private void addButtonListener(Button button, AdminMenuOptions menuOption) {
        button.setOnAction(event -> {
            clearButtonStyles();
            button.getStyleClass().add("button-selected");
            Model.getInstance().getViewFactory().getAdminSelectedMenuItem().set(menuOption);
        });
    }

    private void clearButtonStyles() {
        btnViewInventory.getStyleClass().remove("button-selected");
        btnInventoryBatch.getStyleClass().remove("button-selected");
        btnAlerts.getStyleClass().remove("button-selected");
        btnManageStaff.getStyleClass().remove("button-selected");
        btnSuppliers.getStyleClass().remove("button-selected");
        btnPurchaseOrders.getStyleClass().remove("button-selected");
        btnReports.getStyleClass().remove("button-selected");
        btnHistory.getStyleClass().remove("button-selected");
        btnAccount.getStyleClass().remove("button-selected");
    }

    private void onSignOut(){
        Stage stage = (Stage) btnViewInventory.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().resetViewFactory();
        LoginController.removeCredentials();
        Model.getInstance().getViewFactory().showLoginWindow();
    }
}
