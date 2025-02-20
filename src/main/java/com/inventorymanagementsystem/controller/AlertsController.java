package com.inventorymanagementsystem.controller;

import com.inventorymanagementsystem.model.Product;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AlertsController implements Initializable {
    public TableView<Product> tableViewAlerts;
    public TableColumn<Product, Integer> columnProductId;
    public TableColumn<Product, String> columnProductName;
    public TableColumn<Product, String> columnCategory;
    public TableColumn<Product, Integer> columnCurrentStock;
    public TableColumn<Product, Integer> columnMinimumStock;
    public TextField txtAlertSearch;

    private static final ObservableList<Product> alertList = Product.getLowStockProducts();
    private FilteredList<Product> filteredAlertList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnProductId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        columnProductName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        columnCategory.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        columnCurrentStock.setCellValueFactory(cellData -> cellData.getValue().stockCountProperty().asObject());
        columnMinimumStock.setCellValueFactory(cellData -> cellData.getValue().lowStockAmountProperty().asObject());

        filteredAlertList = new FilteredList<>(alertList, p -> true);
        tableViewAlerts.setItems(filteredAlertList);
        txtAlertSearch.textProperty().addListener((_, _, _) -> filterAlertList());
    }

    private void filterAlertList() {
        String searchText = txtAlertSearch.getText().toLowerCase().trim();

        filteredAlertList.setPredicate(product -> {
            if (searchText.isEmpty()) {
                return true;
            }

            return String.valueOf(product.ID).contains(searchText)
                    || product.getName().toLowerCase().contains(searchText)
                    || product.getCategory().toLowerCase().contains(searchText)
                    || String.valueOf(product.getUnitPrice()).contains(searchText)
                    || String.valueOf(product.getStockCount()).contains(searchText)
                    || String.valueOf(product.getLowStockAmount()).contains(searchText);
        });
    }

    public static void refreshTableView(){
        alertList.setAll(Product.getLowStockProducts());
    }
}
