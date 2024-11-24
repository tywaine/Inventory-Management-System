package com.inventorymanagementsystem.Controllers;

import com.inventorymanagementsystem.Models.Batch;
import com.inventorymanagementsystem.Models.DataBaseManager;
import com.inventorymanagementsystem.Models.Model;
import com.inventorymanagementsystem.Models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class InventoryBatchesController implements Initializable {
    public TableView<Product> tableViewProducts;
    public TableColumn<Product, Integer> columnProductId;
    public TableColumn<Product, String> columnProductName;
    public TableColumn<Product, BigDecimal> columnUnitPrice;

    public TableView<Batch> tableViewBatches;
    public TableColumn<Batch, Integer> columnBatchId;
    public TableColumn<Batch, Integer> columnCurrentStock;
    public TableColumn<Batch, LocalDate> columnExpirationDate;

    public TextField txtProductSearch, txtBatchesSearch, txtProductName, txtUnitPrice, txtStockAmount;
    public Button btnBatchCommand, btnClearSelection;
    public DatePicker dateExpirationDate;
    public ComboBox<String> comboBoxBatchCommands;
    public Label lblProductId, lblBatchId, lblExpirationDateError, lblStockAmountError, lblExpirationDate;

    private final ObservableList<Product> productList = Product.getList();
    private FilteredList<Product> filteredProductList;

    private final ObservableList<Batch> batchList = FXCollections.observableArrayList();
    private FilteredList<Batch> filteredBatchList;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblBatchId.setVisible(false);
        btnClearSelection.setDisable(true);
        btnBatchCommand.setDisable(true);

        ObservableList<String> staffCommandOptions = FXCollections.observableArrayList("Add", "Update", "Sell", "Delete");
        comboBoxBatchCommands.setItems(staffCommandOptions);
        comboBoxBatchCommands.setValue("Add");

        txtStockAmount.setOnKeyReleased(this::handleStockAmountKeyReleased);
        dateExpirationDate.getEditor().setOnKeyReleased(this::handleExpirationDateKeyReleased);

        txtStockAmount.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        dateExpirationDate.getEditor().textProperty().addListener((observable, oldValue, newValue) -> validateFields());

        columnProductId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        columnProductName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        columnUnitPrice.setCellValueFactory(cellData -> cellData.getValue().unitPriceProperty());

        columnBatchId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        columnCurrentStock.setCellValueFactory(cellData -> cellData.getValue().currentStockProperty().asObject());
        columnExpirationDate.setCellValueFactory(cellData -> cellData.getValue().expirationDateProperty());

        filteredProductList = new FilteredList<>(productList, p -> true);
        tableViewProducts.setItems(filteredProductList);
        txtProductSearch.textProperty().addListener((observable, oldValue, newValue) -> filterProductList());
        tableViewProducts.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> populateProductFields(newValue));

        filteredBatchList = new FilteredList<>(batchList, b -> true);
        tableViewBatches.setItems(filteredBatchList);
        txtBatchesSearch.textProperty().addListener((observable, oldValue, newValue) -> filterBatchList());
        tableViewBatches.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> populateFields(newValue));

        comboBoxBatchCommands.valueProperty().addListener((observable, oldValue, newValue) -> updateBatchCommand(newValue));
    }

    public void filterProductList(){
        String searchText = txtProductSearch.getText().toLowerCase().trim();

        filteredProductList.setPredicate(product -> {
            if (searchText.isEmpty()) {
                return true;
            }

            return String.valueOf(product.ID).contains(searchText)
                    || product.getName().toLowerCase().contains(searchText)
                    || product.getCategory().toLowerCase().contains(searchText)
                    || String.valueOf(product.getUnitPrice()).contains(searchText)
                    || String.valueOf(product.getLowStockAmount()).contains(searchText);
        });
    }

    public void filterBatchList(){
        String searchText = txtBatchesSearch.getText().toLowerCase().trim();

        filteredBatchList.setPredicate(batch -> {
            if (searchText.isEmpty()) {
                return true;
            }

            return String.valueOf(batch.ID).contains(searchText)
                    || String.valueOf(batch.getCurrentStock()).contains(searchText)
                    || String.valueOf(batch.getExpirationDate()).contains(searchText);
        });
    }

    private void handleStockAmountKeyReleased(KeyEvent keyEvent) {
        String stockAmount = txtStockAmount.getText();

        try{
            int num = Integer.parseInt(stockAmount);

            if(num > 0){
                lblStockAmountError.setText("");
            }
            else{
                lblStockAmountError.setText("Stock Amount cannot be less than 1");
            }
        }catch(NumberFormatException _){
            lblStockAmountError.setText("Not a valid number");
        }

        validateFields();
    }

    private void handleExpirationDateKeyReleased(KeyEvent keyEvent) {
        String expirationDate = dateExpirationDate.getEditor().getText();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            LocalDate date = LocalDate.parse(expirationDate, formatter);

            if (date.isAfter(LocalDate.now())) {
                lblExpirationDateError.setText("");
                dateExpirationDate.setValue(date);
            } else {
                lblExpirationDateError.setText("Date is before or equal to the current date.");
            }
        } catch (DateTimeException e) {
            System.err.println("Error: " + e.getMessage());
            lblExpirationDateError.setText("Not a valid date.");
        }

        validateFields();
    }


    private boolean isProductSelected() {
        return tableViewProducts.getSelectionModel().getSelectedItem() != null;
    }

    private boolean isBatchSelected() {
        return tableViewBatches.getSelectionModel().getSelectedItem() != null;
    }

    private void updateBatchCommand(String command) {
        switch (command) {
            case "Add":
                btnBatchCommand.setText("Add Batch");
                btnBatchCommand.setOnAction(event -> addBatch());
                lblExpirationDate.setVisible(true);
                dateExpirationDate.setVisible(true);
                dateExpirationDate.setDisable(false);
                lblExpirationDateError.setVisible(true);
                lblBatchId.setVisible(false);
                validateFields();
                break;
            case "Update":
                btnBatchCommand.setText("Update Batch");
                btnBatchCommand.setOnAction(event -> updateBatch());
                lblExpirationDate.setVisible(true);
                dateExpirationDate.setVisible(true);
                dateExpirationDate.setDisable(false);
                lblExpirationDateError.setVisible(true);
                lblBatchId.setVisible(true);
                validateFields();
                break;
            case "Sell":
                btnBatchCommand.setText("Sell Batch/es");
                btnBatchCommand.setOnAction(event -> sellBatch());
                lblExpirationDate.setVisible(false);
                dateExpirationDate.setVisible(false);
                lblExpirationDateError.setVisible(false);
                lblBatchId.setVisible(false);
                validateFields();
                break;
            case "Delete":
                btnBatchCommand.setText("Delete Batch");
                btnBatchCommand.setOnAction(event -> deleteBatch());
                lblExpirationDate.setVisible(true);
                dateExpirationDate.setVisible(true);
                dateExpirationDate.setDisable(false);
                lblExpirationDateError.setVisible(true);
                lblBatchId.setVisible(true);
                validateFields();
                break;
        }
    }

    private void validateFields() {
        if (comboBoxBatchCommands.getValue().equals("Add")) {
            btnBatchCommand.setDisable(!allFieldsValidForAdd());
        }
        else if (comboBoxBatchCommands.getValue().equals("Update")) {
            btnBatchCommand.setDisable(!allFieldsValidForUpdate());
        }
        else if(comboBoxBatchCommands.getValue().equals("Sell")){
            btnBatchCommand.setDisable(!allFieldsValidForSell());
        }
        else{
            btnBatchCommand.setDisable(!allFieldsValidForDelete());
        }
    }

    private boolean allFieldsValidForAdd() {
        return isProductSelected() && !txtStockAmount.getText().isEmpty()
                && !dateExpirationDate.getEditor().getText().isEmpty()
                && lblExpirationDateError.getText().isEmpty()
                && lblStockAmountError.getText().isEmpty();
    }

    private boolean allFieldsValidForUpdate() {
        if (!isProductSelected() || !isBatchSelected()) {
            return false;
        }

        Batch batch = tableViewBatches.getSelectionModel().getSelectedItem();
        String stockAmount = String.valueOf(batch.getCurrentStock());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String expirationDate = batch.getExpirationDate().format(formatter);
        String txtExpirationDate = dateExpirationDate.getEditor().getText();

        if (txtStockAmount.getText().equals(stockAmount) && txtExpirationDate.equals(expirationDate)) {
            System.out.println("No fields have been changed.");
            return false;
        }

        return !txtStockAmount.getText().isEmpty() && !dateExpirationDate.getEditor().getText().isEmpty()
                && lblExpirationDateError.getText().isEmpty() && lblStockAmountError.getText().isEmpty();
    }


    private boolean allFieldsValidForSell(){
        return isProductSelected() && !txtStockAmount.getText().isEmpty();
    }

    private boolean allFieldsValidForDelete() {
        if(!isProductSelected() || !isBatchSelected()){
            return false;
        }

        Batch batch = tableViewBatches.getSelectionModel().getSelectedItem();
        String currentStock = String.valueOf(batch.getCurrentStock());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String expirationDate = batch.getExpirationDate().format(formatter);
        String txtExpirationDate = dateExpirationDate.getEditor().getText();

        return txtStockAmount.getText().equals(currentStock) && txtExpirationDate.equals(expirationDate);
    }

    public void addBatch(){
        Product product = tableViewProducts.getSelectionModel().getSelectedItem();
        int currentStock = Integer.parseInt(txtStockAmount.getText());
        LocalDate expirationDate = dateExpirationDate.getValue();
        DataBaseManager.addBatch(product.ID, currentStock, expirationDate);
        int id = Batch.getLastAddedBatchID();
        DataBaseManager.addInventoryAdjustment(
                Model.getInstance().getCurrentUser().ID,
                Model.getInstance().getCurrentUser().getRole(),
                product.ID,
                product.getName(),
                id,
                LocalDateTime.now(),
                "RESTOCK",
                product.getStockCount(),
                product.calculateStockCount()
        );

        if(btnClearSelection.isDisable()){
            clearFields();
        }
        else{
            clearSelection();
        }

        batchList.add(Batch.getList().getLast());
        AlertsController.refreshTableView();
        Model.getInstance().showAlert(Alert.AlertType.INFORMATION, "Added Batch",
                "Batch with ID: " + id + " has been added.");
    }

    public void updateBatch(){
        Product product = tableViewProducts.getSelectionModel().getSelectedItem();
        Batch batch = tableViewBatches.getSelectionModel().getSelectedItem();
        int currentStock = Integer.parseInt(txtStockAmount.getText());
        LocalDate expirationDate = dateExpirationDate.getValue();

        DataBaseManager.updateBatch(batch, product.ID, currentStock, expirationDate);
        DataBaseManager.addInventoryAdjustment(
                Model.getInstance().getCurrentUser().ID,
                Model.getInstance().getCurrentUser().getRole(),
                product.ID,
                product.getName(),
                batch.ID,
                LocalDateTime.now(),
                "UPDATE",
                product.getStockCount(),
                product.calculateStockCount()
        );

        tableViewBatches.refresh();
        validateFields();
        AlertsController.refreshTableView();
        Model.getInstance().showAlert(Alert.AlertType.INFORMATION, "Updated Batch",
                "Batch with ID: " + batch.ID + " has been updated.");
    }

    public void sellBatch(){
        Product product = tableViewProducts.getSelectionModel().getSelectedItem();
        int currentStock = Integer.parseInt(txtStockAmount.getText());
        LocalDate currentDate = LocalDate.now();

        if(product.getStockCount() < currentStock){
            Model.getInstance().showAlert(Alert.AlertType.ERROR, "Entered Amount is greater than Product Stock Count",
                    "Enter stock amount to sell is greater than product stock Count\n" +
                            "Product Stock Count: " + product.getStockCount());
            return;
        }

        int count = 0;
        int stockAmountSold = 0;

        while(!batchList.isEmpty() && currentStock >= batchList.getFirst().getCurrentStock()){
            if(batchList.getFirst().getExpirationDate().isBefore(currentDate)){
                Model.getInstance().showAlert(Alert.AlertType.ERROR, "Cannot Sell Batch",
                        "Batch with ID: " + batchList.getFirst().ID + " cannot be sold since the expiration date has passed.\n" +
                                "Update the batch if you entered the wrong information or delete it!!");
                break;
            }

            Batch batch = batchList.removeFirst();
            currentStock =- batch.getCurrentStock();
            stockAmountSold += batch.getCurrentStock();
            DataBaseManager.deleteBatch(batch);
            count++;
        }

        if(!batchList.isEmpty() && currentStock < batchList.getFirst().getCurrentStock() &&
                batchList.getFirst().getExpirationDate().isAfter(currentDate)){
            Batch batch = batchList.getFirst();
            DataBaseManager.updateBatch(batch, product.ID, batch.getCurrentStock() - currentStock, batch.getExpirationDate());
            stockAmountSold += currentStock;
            count++;
        }

        if(count > 0){
            DataBaseManager.addSale(
                    product.ID,
                    product.getName(),
                    currentDate,
                    stockAmountSold,
                    product.getUnitPrice());

            DataBaseManager.addInventoryAdjustment(
                    Model.getInstance().getCurrentUser().ID,
                    Model.getInstance().getCurrentUser().getRole(),
                    product.ID,
                    product.getName(),
                    -1,
                    LocalDateTime.now(),
                    "SALE",
                    product.getStockCount(),
                    product.calculateStockCount()
            );

            tableViewBatches.setItems(product.getBatchList());
            tableViewBatches.refresh();
            validateFields();
            clearSelection();
            AlertsController.refreshTableView();
            Model.getInstance().showAlert(Alert.AlertType.INFORMATION, "Sold Batch/es",
                    stockAmountSold + " Batch/es has been sold.");
        }
    }

    public void deleteBatch(){
        Product product = tableViewProducts.getSelectionModel().getSelectedItem();
        Batch batch = tableViewBatches.getSelectionModel().getSelectedItem();
        Alert alert = Model.getInstance().getConfirmationDialogAlert("Delete Batch?",
                "Are you sure you want to delete this Batch?\nBatch ID: " + batch.ID);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            DataBaseManager.deleteBatch(batch);
            DataBaseManager.addInventoryAdjustment(
                    Model.getInstance().getCurrentUser().ID,
                    Model.getInstance().getCurrentUser().getRole(),
                    product.ID,
                    product.getName(),
                    batch.ID,
                    LocalDateTime.now(),
                    "DELETION",
                    product.getStockCount(),
                    product.calculateStockCount()
            );

            if(!Batch.contains(batch.ID)){
                clearSelection();
                batchList.remove(batch);
                AlertsController.refreshTableView();
                Model.getInstance().showAlert(Alert.AlertType.INFORMATION, "Deleted Batch",
                        "Batch with ID: " + batch.ID + " has been deleted.");
            }
        }
    }

    public void clearFields(){
        txtStockAmount.setText("");
        dateExpirationDate.setValue(null);
    }

    public void clearSelection(){
        tableViewBatches.getSelectionModel().clearSelection();
        txtStockAmount.setText("");
        dateExpirationDate.setValue(null);
        btnClearSelection.setDisable(true);

        lblBatchId.setText("Batch ID: ");
    }

    private void populateProductFields(Product product) {
        if (product != null) {
            batchList.setAll(product.getBatchList());
            txtProductName.setText(product.getName());
            txtUnitPrice.setText(String.valueOf(product.getUnitPrice()));
            lblProductId.setText("Product ID: " + product.ID);
            validateFields();
        }
        else{
            txtProductName.setText("");
            txtUnitPrice.setText("");
            lblProductId.setText("Product ID: ");
            batchList.clear();
        }
    }

    private void populateFields(Batch batch) {
        if (batch != null) {
            txtStockAmount.setText(String.valueOf(batch.getCurrentStock()));
            dateExpirationDate.setValue(batch.getExpirationDate());
            lblBatchId.setText("Batch ID: " + batch.ID);
            btnClearSelection.setDisable(false);
            validateFields();
        }
        else{
            txtStockAmount.setText("");
            dateExpirationDate.setValue(null);
            lblBatchId.setText("Batch ID: ");
            btnClearSelection.setDisable(true);
        }
    }
}
