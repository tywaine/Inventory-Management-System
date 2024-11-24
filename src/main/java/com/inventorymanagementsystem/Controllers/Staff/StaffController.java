package com.inventorymanagementsystem.Controllers.Staff;

import com.inventorymanagementsystem.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class StaffController implements Initializable {
    public BorderPane staffParent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().getStaffSelectedMenuItem().addListener(
                (observableValue, oldVal, newVal) -> {
                    switch(newVal){
                        case INVENTORY_BATCHES -> staffParent.setCenter(Model.getInstance().getViewFactory().getInventoryBatchesView());
                        case ALERTS -> staffParent.setCenter(Model.getInstance().getViewFactory().getAlertsView());
                        case HISTORY -> staffParent.setCenter(Model.getInstance().getViewFactory().getHistoryView());
                        default -> staffParent.setCenter(Model.getInstance().getViewFactory().getViewInventoryView());
                    }
                }
        );
    }
}
