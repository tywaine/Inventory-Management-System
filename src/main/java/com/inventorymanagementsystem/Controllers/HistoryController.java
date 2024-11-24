package com.inventorymanagementsystem.Controllers;

import com.inventorymanagementsystem.Models.*;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class HistoryController implements Initializable {
    public TableView<InventoryAdjustment> tableViewInventoryAdjustments;
    public TableColumn<InventoryAdjustment, Integer> columnUserId;
    public TableColumn<InventoryAdjustment, String> columnUserRole;
    public TableColumn<InventoryAdjustment, Integer> columnProductId;
    public TableColumn<InventoryAdjustment, String> columnProductName;
    public TableColumn<InventoryAdjustment, String> columnBatchId;
    public TableColumn<InventoryAdjustment, String> columnAdjustmentDatetime;
    public TableColumn<InventoryAdjustment, String> columnAdjustmentType;
    public TableColumn<InventoryAdjustment, String> columnPreviousStock;
    public TableColumn<InventoryAdjustment, String> columnAdjustedStock;
    public TableColumn<InventoryAdjustment, Void> columnDelete;
    public TextField txtAdjustmentSearch;

    private final ObservableList<InventoryAdjustment> adjustmentsList = InventoryAdjustment.getList();
    private FilteredList<InventoryAdjustment> filteredAdjustmentsList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnUserId.setCellValueFactory(cellData -> cellData.getValue().userIdProperty().asObject());
        columnUserRole.setCellValueFactory(cellData -> cellData.getValue().userRoleProperty());
        columnProductId.setCellValueFactory(cellData -> cellData.getValue().productIdProperty().asObject());
        columnProductName.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        columnBatchId.setCellValueFactory(cellData -> cellData.getValue().batchIdStringProperty());
        columnAdjustmentDatetime.setCellValueFactory(cellData -> cellData.getValue().adjustmentDatetimeFormattedProperty());
        columnAdjustmentType.setCellValueFactory(cellData -> cellData.getValue().adjustmentTypeProperty());
        columnPreviousStock.setCellValueFactory(cellData -> cellData.getValue().previous_stockStringProperty());
        columnAdjustedStock.setCellValueFactory(cellData -> cellData.getValue().adjusted_stockStringProperty());
        setupDeleteColumn();

        filteredAdjustmentsList = new FilteredList<>(adjustmentsList, p -> true);
        tableViewInventoryAdjustments.setItems(filteredAdjustmentsList);
        txtAdjustmentSearch.textProperty().addListener((observable, oldValue, newValue) -> filterAdjustmentList());
    }

    private void filterAdjustmentList() {
        String searchText = txtAdjustmentSearch.getText().toLowerCase().trim();

        filteredAdjustmentsList.setPredicate(inventoryAdjustment -> {
            if (searchText.isEmpty()) {
                return true;
            }

            return String.valueOf(inventoryAdjustment.getUserId()).contains(searchText)
                    || inventoryAdjustment.getUserRole().toLowerCase().contains(searchText)
                    || String.valueOf(inventoryAdjustment.getProductId()).contains(searchText)
                    || inventoryAdjustment.getProductName().toLowerCase().contains(searchText)
                    || String.valueOf(inventoryAdjustment.getBatchId()).contains(searchText)
                    || String.valueOf(inventoryAdjustment.getAdjustmentDatetimeFormatted()).contains(searchText)
                    || inventoryAdjustment.getAdjustmentType().toLowerCase().contains(searchText)
                    || String.valueOf(inventoryAdjustment.getPrevious_stock()).contains(searchText)
                    || String.valueOf(inventoryAdjustment.getAdjusted_stock()).contains(searchText);
        });
    }

    private void setupDeleteColumn() {
        columnDelete.setCellFactory(new Callback<>() {
            @Override
            public TableCell<InventoryAdjustment, Void> call(final TableColumn<InventoryAdjustment, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button();

                    {
                        FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.TRASH);
                        deleteIcon.setIconSize(16);
                        deleteButton.setGraphic(deleteIcon);

                        deleteButton.setOnAction(event -> {
                            InventoryAdjustment inventoryAdjustment = getTableView().getItems().get(getIndex());
                            deleteInventoryAdjustment(inventoryAdjustment);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        });
    }

    private void deleteInventoryAdjustment(InventoryAdjustment inventoryAdjustment) {
        User user = Model.getInstance().getCurrentUser();

        if(user.isAdmin()){
            Alert alert = Model.getInstance().getConfirmationDialogAlert("Delete Inventory Adjustment?",
                    "Are you sure you want to delete this Inventory Adjustment?\nInventory Adjustment ID: " + inventoryAdjustment.ID);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                DataBaseManager.deleteInventoryAdjustment(inventoryAdjustment);
                Model.getInstance().showAlert(Alert.AlertType.INFORMATION, "Deleted Inventory Adjustment",
                        "Inventory Adjustment with ID: " +inventoryAdjustment.ID + " has been deleted!");
            }
        }
        else{
            Model.getInstance().showAlert(Alert.AlertType.INFORMATION, "Invalid Access",
                    "STAFF does not have access to delete anything in Inventory History");
        }
    }
}
