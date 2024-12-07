package com.inventorymanagementsystem.Controllers.Admin;

import com.inventorymanagementsystem.Controllers.LoginController;
import com.inventorymanagementsystem.Models.Model;
import com.inventorymanagementsystem.Enums.AdminMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

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
        btnViewInventory.setGraphic(createFontIcon(FontAwesomeSolid.BOXES, 18));
        btnInventoryBatch.setGraphic(createFontIcon(FontAwesomeSolid.CUBES, 18));
        btnAlerts.setGraphic(createFontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE, 18));
        btnManageStaff.setGraphic(createFontIcon(FontAwesomeSolid.USER_COG, 18));
        btnSuppliers.setGraphic(createFontIcon(FontAwesomeSolid.TRUCK, 18));
        btnPurchaseOrders.setGraphic(createFontIcon(FontAwesomeSolid.CLIPBOARD_LIST, 18));
        btnReports.setGraphic(createFontIcon(FontAwesomeSolid.CHART_BAR, 18));
        btnHistory.setGraphic(createFontIcon(FontAwesomeSolid.HISTORY, 18));
        btnAccount.setGraphic(createFontIcon(FontAwesomeSolid.USER_CIRCLE, 18));
        btnSignOut.setGraphic(createFontIcon(FontAwesomeSolid.SIGN_OUT_ALT, 14));
    }

    private FontIcon createFontIcon(FontAwesomeSolid iconType, int size) {
        FontIcon icon = new FontIcon(iconType);
        icon.setIconSize(size);
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
