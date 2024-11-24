package com.inventorymanagementsystem.Controllers.Admin;

import com.inventorymanagementsystem.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    public BorderPane adminParent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().addListener(
                (observableValue, oldVal, newVal) -> {
                    switch(newVal){
                        case INVENTORY_BATCHES -> adminParent.setCenter(Model.getInstance().getViewFactory().getInventoryBatchesView());
                        case ALERTS -> adminParent.setCenter(Model.getInstance().getViewFactory().getAlertsView());
                        case MANAGE_STAFF -> adminParent.setCenter(Model.getInstance().getViewFactory().getManageStaffView());
                        case SUPPLIERS -> adminParent.setCenter(Model.getInstance().getViewFactory().getSuppliersView());
                        case PURCHASE_ORDERS -> adminParent.setCenter(Model.getInstance().getViewFactory().getPurchaseOrdersView());
                        case REPORTS -> adminParent.setCenter(Model.getInstance().getViewFactory().getReportsView());
                        case HISTORY -> adminParent.setCenter(Model.getInstance().getViewFactory().getHistoryView());
                        case ACCOUNT -> adminParent.setCenter(Model.getInstance().getViewFactory().getAccountView());
                        default -> adminParent.setCenter(Model.getInstance().getViewFactory().getViewInventoryView());
                    }
                }
        );
    }
}
