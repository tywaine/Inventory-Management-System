package com.inventorymanagementsystem.Controllers;

import com.inventorymanagementsystem.Config.DataBaseManager;
import com.inventorymanagementsystem.Models.*;
import com.inventorymanagementsystem.Utils.MyAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ViewInventoryController implements Initializable {
    public TableView<Product> tableViewInventory;
    public TableColumn<Product, Integer> columnProductID;
    public TableColumn<Product, String> columnName;
    public TableColumn<Product, String> columnCategory;
    public TableColumn<Product, BigDecimal> columnUnitPrice;
    public TableColumn<Product, Integer> columnStockCount;
    public TableColumn<Product, Integer> columnLowStockAmount;
    public TextField txtInventorySearch, txtProductName, txtUnitPrice, txtLowStockAmount;
    public Button btnInventoryCommand, btnClearFields, btnClearSelection;
    public ComboBox<String> comboBoxInventoryCommand, comboBoxCategory;
    public Label lblProductID, lblNameError, lblCategoryError, lblUnitPriceError, lblProductCommandError, lblLowStockError;

    private final ObservableList<Product> productList = Product.getList();
    private FilteredList<Product> filteredProductList;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblProductID.setVisible(false);
        btnClearSelection.setDisable(true);
        btnInventoryCommand.setDisable(true);
        comboBoxCategory.setItems(Product.getCategoryList());

        ObservableList<String> staffCommandOptions = FXCollections.observableArrayList("Add", "Update", "Delete");
        comboBoxInventoryCommand.setItems(staffCommandOptions);
        comboBoxInventoryCommand.setValue("Add");

        txtProductName.setOnKeyReleased(this::handleProductNameKeyReleased);
        comboBoxCategory.getEditor().setOnKeyReleased(this::handleCategoryKeyReleased);
        txtUnitPrice.setOnKeyReleased(this::handleUnitPriceKeyReleased);
        txtLowStockAmount.setOnKeyReleased(this::handleLowStockKeyReleased);

        txtProductName.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        comboBoxCategory.getEditor().textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        txtUnitPrice.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        txtLowStockAmount.textProperty().addListener((observable, oldValue, newValue) -> validateFields());

        columnProductID.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        columnName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        columnCategory.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        columnUnitPrice.setCellValueFactory(cellData -> cellData.getValue().unitPriceProperty());
        columnStockCount.setCellValueFactory(cellData -> cellData.getValue().stockCountProperty().asObject());
        columnLowStockAmount.setCellValueFactory(cellData -> cellData.getValue().lowStockAmountProperty().asObject());

        filteredProductList = new FilteredList<>(productList, p -> true);
        tableViewInventory.setItems(filteredProductList);
        txtInventorySearch.textProperty().addListener((observable, oldValue, newValue) -> filterInventoryList());
        tableViewInventory.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> populateFields(newValue));

        comboBoxInventoryCommand.valueProperty().addListener((observable, oldValue, newValue) -> updateInventoryCommand(newValue));
    }

    public void filterInventoryList(){
        String searchText = txtInventorySearch.getText().toLowerCase().trim();

        filteredProductList.setPredicate(product -> {
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

    private void handleProductNameKeyReleased(KeyEvent keyEvent) {
        String productName = txtProductName.getText();

        if(Product.containsName(productName)){
            lblNameError.setText("Product Name is already taken");
        }
        else{
            lblNameError.setText("");
        }

        validateFields();
    }

    private void handleProductNameUpdateKeyReleased(KeyEvent keyEvent) {
        if(!isInventorySelected()){
            return;
        }

        Product product = tableViewInventory.getSelectionModel().getSelectedItem();
        String productName = txtProductName.getText();

        if(Product.containsName(productName) && !product.getName().equals(productName)){
            lblNameError.setText("Product Name is already taken");
        }
        else{
            lblNameError.setText("");
        }

        validateFields();
    }

    private void handleCategoryKeyReleased(KeyEvent keyEvent) {
        validateFields();
    }

    private void handleUnitPriceKeyReleased(KeyEvent keyEvent) {
        String unitPrice = txtUnitPrice.getText();

        if(Product.isValidUnitPrice(unitPrice)){
            lblUnitPriceError.setText("");
        }
        else{
            lblUnitPriceError.setText("Invalid unit price");
        }

        validateFields();
    }

    private void handleLowStockKeyReleased(KeyEvent keyEvent){
        String lowStockAmount = txtLowStockAmount.getText();

        try{
            int num = Integer.parseInt(lowStockAmount);

            if(num > 0){
                lblLowStockError.setText("");
            }
            else{
                lblLowStockError.setText("Low Stock Amount cannot be less than 1");
            }
        }catch(NumberFormatException _){
            lblLowStockError.setText("Not a valid number");
        }

        validateFields();
    }

    private boolean isInventorySelected() {
        return tableViewInventory.getSelectionModel().getSelectedItem() != null;
    }

    private void updateInventoryCommand(String command) {
        Product product = null;

        if(isInventorySelected()){
            product = tableViewInventory.getSelectionModel().getSelectedItem();
        }

        switch (command) {
            case "Add":
                btnInventoryCommand.setText("Add Product");
                btnInventoryCommand.setOnAction(event -> addProduct());
                lblProductID.setVisible(false);
                txtProductName.setOnKeyReleased(this::handleProductNameKeyReleased);

                if(Product.containsName(txtProductName.getText())){
                    lblNameError.setText("Product Name is already taken");
                }
                else{
                    lblNameError.setText("");
                }

                validateFields();
                break;
            case "Update":
                btnInventoryCommand.setText("Update Product");
                btnInventoryCommand.setOnAction(event -> updateProduct());
                lblProductID.setVisible(true);
                txtProductName.setOnKeyReleased(this::handleProductNameUpdateKeyReleased);

                if(Product.containsName(txtProductName.getText()) && !product.getName().equals(txtProductName.getText())){
                    lblNameError.setText("Product Name is already taken");
                }
                else{
                    lblNameError.setText("");
                }

                validateFields();
                break;
            case "Delete":
                btnInventoryCommand.setText("Delete Product");
                btnInventoryCommand.setOnAction(event -> deleteProduct());
                lblProductID.setVisible(true);
                txtProductName.setOnKeyReleased(this::handleProductNameUpdateKeyReleased);

                if(Product.containsName(txtProductName.getText()) && !product.getName().equals(txtProductName.getText())){
                    lblNameError.setText("Product Name is already taken");
                }
                else{
                    lblNameError.setText("");
                }

                validateFields();
                break;
        }
    }

    public void validateFields(){
        if (comboBoxInventoryCommand.getValue().equals("Add")) {
            btnInventoryCommand.setDisable(!allFieldsValidForAdd());
        }
        else if (comboBoxInventoryCommand.getValue().equals("Update")) {
            btnInventoryCommand.setDisable(!allFieldsValidForUpdate());
        }
        else{
            btnInventoryCommand.setDisable(!allFieldsValidForDelete());
        }
    }

    private boolean allFieldsValidForAdd() {
        return !Product.containsName(txtProductName.getText())
                && !txtProductName.getText().isEmpty()
                && !comboBoxCategory.getEditor().getText().isEmpty()
                && !txtUnitPrice.getText().isEmpty()
                && !txtLowStockAmount.getText().isEmpty()
                && lblNameError.getText().isEmpty()
                && lblUnitPriceError.getText().isEmpty()
                && lblLowStockError.getText().isEmpty();
    }

    private boolean allFieldsValidForUpdate() {
        if(!isInventorySelected()){
            return false;
        }

        Product product = tableViewInventory.getSelectionModel().getSelectedItem();

        String productName = product.getName();
        String category = product.getCategory();
        String unitPrice = String.valueOf(product.getUnitPrice());
        String lowStockAmount = String.valueOf(product.getLowStockAmount());

        if (txtProductName.getText().equals(productName) && comboBoxCategory.getEditor().getText().equals(category)
                && txtUnitPrice.getText().equals(unitPrice) && txtLowStockAmount.getText().equals(lowStockAmount)) {
            System.out.println("No fields have been changed.");
            return false;
        }

        return !(Product.containsName(txtProductName.getText()) && !productName.equals(txtProductName.getText()))
                && !txtProductName.getText().isEmpty()
                && !comboBoxCategory.getEditor().getText().isEmpty()
                && !txtUnitPrice.getText().isEmpty()
                && !txtLowStockAmount.getText().isEmpty()
                && lblNameError.getText().isEmpty()
                && lblUnitPriceError.getText().isEmpty()
                && lblLowStockError.getText().isEmpty();
    }

    private boolean allFieldsValidForDelete() {
        if(!isInventorySelected()){
            return false;
        }

        Product product = tableViewInventory.getSelectionModel().getSelectedItem();
        String name = product.getName();
        String category = product.getCategory();
        String unitPrice = String.valueOf(product.getUnitPrice());
        String lowStockAmount = String.valueOf(product.getLowStockAmount());

        return txtProductName.getText().equals(name) && comboBoxCategory.getEditor().getText().equals(category)
                && txtUnitPrice.getText().equals(unitPrice) && txtLowStockAmount.getText().equals(lowStockAmount);
    }


    public void addProduct() {
        String name = txtProductName.getText();
        String category = comboBoxCategory.getValue();
        BigDecimal unitPrice = BigDecimal.valueOf(Float.parseFloat(txtUnitPrice.getText())).setScale(2, RoundingMode.HALF_UP);
        int lowStockAmount = Integer.parseInt(txtLowStockAmount.getText());
        DataBaseManager.addProduct(name, category, unitPrice, lowStockAmount);
        int id = Product.getLastAddedProductID();
        DataBaseManager.addInventoryAdjustment(
                Model.getInstance().getCurrentUser().ID,
                Model.getInstance().getCurrentUser().getRole(),
                id,
                name,
                -1,
                LocalDateTime.now(),
                "ADDITION",
                -1,
                -1
        );

        if(btnClearSelection.isDisable()){
            clearFields();
        }
        else{
            clearSelection();
        }

        AlertsController.refreshTableView();
        MyAlert.showAlert(Alert.AlertType.INFORMATION, "Added Product",
                "Product with ID: " + id + " has been added.");
    }

    public void updateProduct(){
        Product product = tableViewInventory.getSelectionModel().getSelectedItem();
        String name = txtProductName.getText();
        String category = comboBoxCategory.getValue();
        BigDecimal unitPrice = BigDecimal.valueOf(Float.parseFloat(txtUnitPrice.getText()))
                .setScale(2, RoundingMode.HALF_UP);
        int lowStockAmount = Integer.parseInt(txtLowStockAmount.getText());

        DataBaseManager.updateProduct(product, name, category, unitPrice, lowStockAmount);
        DataBaseManager.addInventoryAdjustment(
                Model.getInstance().getCurrentUser().ID,
                Model.getInstance().getCurrentUser().getRole(),
                product.ID,
                name,
                -1,
                LocalDateTime.now(),
                "UPDATE",
                -1,
                -1
        );

        AlertsController.refreshTableView();
        tableViewInventory.refresh();
        validateFields();
        MyAlert.showAlert(Alert.AlertType.INFORMATION, "Updated Product",
                "Product with ID: " + product.ID + " has been updated.");
    }

    public void deleteProduct(){
        Product product = tableViewInventory.getSelectionModel().getSelectedItem();

        if(product.getStockCount() != 0){
            MyAlert.showAlert(Alert.AlertType.ERROR, "Cannot Delete Product",
                    "Product with ID: " + product.ID + " cannot be deleted since the stock amount is not 0.");
            return;
        }

        if (MyAlert.confirmationDialogAlertIsYes("Delete Product?",
                "Are you sure you want to delete this Product?\nProduct ID: " + product.ID)) {
            DataBaseManager.deleteProduct(product);
            DataBaseManager.addInventoryAdjustment(
                    Model.getInstance().getCurrentUser().ID,
                    Model.getInstance().getCurrentUser().getRole(),
                    product.ID,
                    product.getName(),
                    -1,
                    LocalDateTime.now(),
                    "DELETION",
                    -1,
                    -1
            );

            if(!Product.contains(product.ID)){
                clearSelection();
                AlertsController.refreshTableView();
                MyAlert.showAlert(Alert.AlertType.INFORMATION, "Deleted Product",
                        "Product with ID: " + product.ID + " has been deleted.");
            }
        }
    }

    public void clearFields() {
        txtProductName.setText("");
        comboBoxCategory.setValue("");
        txtUnitPrice.setText("");
        txtLowStockAmount.setText("");
        lblNameError.setText("");
        lblCategoryError.setText("");
        lblUnitPriceError.setText("");
        lblLowStockError.setText("");
    }

    public void clearSelection() {
        tableViewInventory.getSelectionModel().clearSelection();
        txtProductName.setText("");
        comboBoxCategory.setValue("");
        txtUnitPrice.setText("");
        txtLowStockAmount.setText("");
        lblNameError.setText("");
        lblCategoryError.setText("");
        lblUnitPriceError.setText("");
        lblLowStockError.setText("");
        btnClearSelection.setDisable(true);

        lblProductID.setText("Product ID: ");
    }

    private void populateFields(Product product) {
        if (product != null) {
            txtProductName.setText(product.getName());
            comboBoxCategory.setValue(product.getCategory());
            txtUnitPrice.setText(String.valueOf(product.getUnitPrice()));
            txtLowStockAmount.setText(String.valueOf(product.getLowStockAmount()));
            lblProductID.setText("Product ID: " + product.ID);
            btnClearSelection.setDisable(false);

            if(btnInventoryCommand.getText().equals("Add Product")){
                lblNameError.setText("Product Name is already taken");
            }
        }
        else{
            txtProductName.setText("");
            comboBoxCategory.setValue("");
            txtUnitPrice.setText("");
            txtLowStockAmount.setText("");
            lblProductID.setText("Product ID: ");
            btnClearSelection.setDisable(true);
        }
    }
}
