package com.inventorymanagementsystem.Controllers.Admin;

import com.inventorymanagementsystem.Models.*;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class PurchaseOrdersController implements Initializable{
    public TableView<PurchaseOrder> tableViewPurchaseOrders;
    public TableColumn<PurchaseOrder, Integer> columnOrderID;
    public TableColumn<PurchaseOrder, LocalDate> columnDate;
    public TableColumn<PurchaseOrder, Integer> columnSupplierID;
    public TableColumn<PurchaseOrder, String> columnSupplierName, columnProductName;
    public TableColumn<PurchaseOrder, Integer> columnQuantity;
    public TableColumn<PurchaseOrder, BigDecimal> columnTotalAmount;
    public TableColumn<PurchaseOrder, Void> columnDelete;

    public TableView<Supplier> tableViewSuppliers;
    public TableColumn<Supplier, Integer> columnSupplierID2;
    public TableColumn<Supplier, String> columnSupplierName2;

    public TextField txtSupplierName, txtSupplierEmail, txtQuantity, txtUnitPrice, txtTotalAmount;
    public TextField txtPurchaseOrderSearch, txtSupplierSearch;
    public Label lblUnitPriceError, lblSupplierID;
    public Button btnSendPurchaseOrder;
    public ComboBox<String> comboBoxProductName;

    private final ObservableList<PurchaseOrder> purchaseOrdersList = PurchaseOrder.getList();
    private FilteredList<PurchaseOrder> filteredPurchaseOrdersList;

    private final ObservableList<Supplier> suppliersList = Supplier.getList();
    private FilteredList<Supplier> filteredSuppliersList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnSendPurchaseOrder.setDisable(true);
        comboBoxProductName.setItems(Product.getNameList());

        columnOrderID.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        columnDate.setCellValueFactory(cellData -> cellData.getValue().orderDateProperty());
        columnSupplierID.setCellValueFactory(cellData -> cellData.getValue().supplierIdProperty().asObject());
        columnSupplierName.setCellValueFactory(cellData -> cellData.getValue().supplierNameProperty());
        columnProductName.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        columnQuantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        columnTotalAmount.setCellValueFactory(cellData -> cellData.getValue().totalAmountProperty());
        setupDeleteColumn();

        columnSupplierID2.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        columnSupplierName2.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        filteredSuppliersList = new FilteredList<>(suppliersList, p -> true);
        tableViewSuppliers.setItems(filteredSuppliersList);
        txtSupplierSearch.textProperty().addListener((observable, oldValue, newValue) -> filterSupplierList());
        tableViewSuppliers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> populateSupplierFields(newValue));

        filteredPurchaseOrdersList = new FilteredList<>(purchaseOrdersList, p -> true);
        tableViewPurchaseOrders.setItems(filteredPurchaseOrdersList);
        txtPurchaseOrderSearch.textProperty().addListener((observable, oldValue, newValue) -> filterPurchaseOrderList());

        onlyDigits(txtQuantity);
        txtUnitPrice.setOnKeyReleased(this::handleUnitPriceKeyReleased);
        comboBoxProductName.getEditor().setOnKeyReleased(this::handleProductNameKeyReleased);

        txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        txtUnitPrice.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        comboBoxProductName.getEditor().textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        comboBoxProductName.valueProperty().addListener((observable, oldValue, newValue) -> validateFields());
        txtSupplierName.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        txtSupplierEmail.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
    }

    private void onlyDigits(TextField textField){
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("\\D", ""));
            }
        });
    }

    private void filterSupplierList() {
        String searchText = txtSupplierSearch.getText().toLowerCase().trim();

        filteredSuppliersList.setPredicate(supplier -> {
            if (searchText.isEmpty()) {
                return true;
            }

            return String.valueOf(supplier.ID).contains(searchText)
                    || supplier.getName().toLowerCase().contains(searchText)
                    || supplier.getContactEmail().toLowerCase().contains(searchText);
        });
    }

    private void setupDeleteColumn() {
        columnDelete.setCellFactory(new Callback<>() {
            @Override
            public TableCell<PurchaseOrder, Void> call(final TableColumn<PurchaseOrder, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button();

                    {
                        FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.TRASH);
                        deleteIcon.setIconSize(16);
                        deleteButton.setGraphic(deleteIcon);

                        deleteButton.setOnAction(event -> {
                            PurchaseOrder selectedPurchaseOrder = getTableView().getItems().get(getIndex());
                            deletePurchaseOrder(selectedPurchaseOrder);
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

    private void deletePurchaseOrder(PurchaseOrder purchaseOrder) {
        Alert alert = Model.getInstance().getConfirmationDialogAlert("Delete Purchase Order?",
                "Are you sure you want to delete this purchase order?\nPurchase Order ID: " + purchaseOrder.ID);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            DataBaseManager.deletePurchaseOrder(purchaseOrder);
            Model.getInstance().showAlert(Alert.AlertType.INFORMATION, "Deleted Purchase Order",
                    "Purchase order with ID: " +purchaseOrder.ID + " has been deleted!");
        }
    }

    private void filterPurchaseOrderList(){
        String searchText = txtPurchaseOrderSearch.getText().toLowerCase().trim();

        filteredPurchaseOrdersList.setPredicate(purchaseOrder -> {
            if (searchText.isEmpty()) {
                return true;
            }

            return String.valueOf(purchaseOrder.ID).contains(searchText)
                    || purchaseOrder.getOrderDate().toString().contains(searchText)
                    || String.valueOf(purchaseOrder.getSupplierId()).contains(searchText)
                    || purchaseOrder.getSupplierName().toLowerCase().contains(searchText)
                    || purchaseOrder.getProductName().toLowerCase().contains(searchText)
                    || String.valueOf(purchaseOrder.getQuantity()).contains(searchText)
                    || purchaseOrder.getTotalAmount().toString().contains(searchText);
        });
    }

    private void handleUnitPriceKeyReleased(KeyEvent keyEvent) {
        String unitPrice = txtUnitPrice.getText();

        if(PurchaseOrder.isValidUnitPrice(unitPrice)){
            lblUnitPriceError.setText("");
        }
        else{
            lblUnitPriceError.setText("Invalid unit price");
        }

        validateFields();
    }

    private void handleProductNameKeyReleased(KeyEvent keyEvent) {
        validateFields();
    }

    public void populateSupplierFields(Supplier supplier){
        if (supplier != null) {
            txtSupplierName.setText(supplier.getName());
            txtSupplierEmail.setText(supplier.getContactEmail());
            lblSupplierID.setText("Supplier ID: " + supplier.ID);
        }
        else{
            txtSupplierName.setText("");
            txtSupplierEmail.setText("");
            lblSupplierID.setText("Supplier ID: ");
        }
    }

    private void validateFields() {
        if (!txtQuantity.getText().isEmpty() && PurchaseOrder.isValidUnitPrice(txtUnitPrice.getText())) {
            float quantity = Float.parseFloat(txtQuantity.getText());
            float unitPrice = Float.parseFloat(txtUnitPrice.getText());
            float totalAmount = quantity * unitPrice;
            txtTotalAmount.setText(String.format("%.2f", totalAmount));
        }
        else {
            txtTotalAmount.clear();
        }
        btnSendPurchaseOrder.setDisable(!allFieldsValid());
    }

    public void clearFields() {
        txtQuantity.setText("");
        txtUnitPrice.setText("");
        comboBoxProductName.setValue("");
        lblUnitPriceError.setText("");
    }

    private boolean allFieldsValid(){
        if(tableViewSuppliers.getSelectionModel().getSelectedItem() == null){
            return false;
        }

        String quantity = txtQuantity.getText();
        String productName = comboBoxProductName.getEditor().getText();

        return !quantity.isEmpty() && !productName.isEmpty() &&
                !txtTotalAmount.getText().isEmpty() && lblUnitPriceError.getText().isEmpty();
    }

    public void sendPurchaseOrder(){
        if(comboBoxProductName.getValue().isEmpty()){
            Model.getInstance().showAlert(Alert.AlertType.WARNING, "Empty Product Name", "Product Name cannot be empty");
            return;
        }

        Supplier supplier = tableViewSuppliers.getSelectionModel().getSelectedItem();
        String productName = comboBoxProductName.getValue();
        int quantity = Integer.parseInt(txtQuantity.getText());
        BigDecimal totalAmount = BigDecimal.valueOf(Float.parseFloat(txtTotalAmount.getText())).setScale(2, RoundingMode.HALF_UP);
        User admin = Model.getInstance().getCurrentUser();
        String adminEmailPassword = DataBaseManager.getAdminEmailPassword(admin);
        String purchaseOrderMessage = String.format(
                """
                        Dear %s,
                        
                        We would like to place a new purchase order with your company.
                        
                        Order Details:
                        \tProduct: %s
                        \tQuantity: %d
                        \tTotal Amount: $%.2f
                        
                        Please process this order at your earliest convenience and feel free to contact us with any questions.
                        
                        Thank you for your continued partnership.
                        
                        Best regards,
                        %s
                        """,
                supplier.getName(),
                productName,
                quantity,
                totalAmount,
                admin.getName()
        );

        if(MyEmail.sendEmail(admin.getEmail(), adminEmailPassword, supplier.getContactEmail(),
                "New Purchase Order", purchaseOrderMessage)){
            DataBaseManager.addPurchaseOrder(
                    LocalDate.now(),
                    supplier.ID,
                    supplier.getName(),
                    productName,
                    quantity,
                    totalAmount);
            int id = DataBaseManager.getLastPurchaseOrderID();

            if(PurchaseOrder.contains(id)){
                Model.getInstance().showAlert(Alert.AlertType.INFORMATION, "Purchase Order Added",
                        "Purchase Order has been added\n\nID: " + id);
            }

            tableViewSuppliers.getSelectionModel().clearSelection();
            clearFields();
            btnSendPurchaseOrder.setDisable(true);
        }
    }
}