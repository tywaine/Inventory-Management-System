package com.inventorymanagementsystem.controller.admin;

import com.inventorymanagementsystem.config.DataBaseManager;
import com.inventorymanagementsystem.model.Supplier;
import com.inventorymanagementsystem.util.MyAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class SuppliersController implements Initializable {
    public TableView<Supplier> tableViewSuppliers;
    public TableColumn<Supplier, Integer> columnSupplierID;
    public TableColumn<Supplier, String> columnName, columnEmail, columnPhoneNumber, columnAddress;
    public TextField txtFirstName, txtLastName, txtContactEmail, txtPhoneNumber;
    public TextArea txtAreaAddress;
    public TextField txtSupplierSearch;
    public Label lblFirstNameError, lblLastNameError, lblEmailError, lblPhoneNumError, lblSupplierID;
    public ComboBox<String> comboBoxSupplierCommand;
    public Button btnSupplierCommand, btnClearSelection, btnClearFields;

    private final ObservableList<Supplier> suppliersList = Supplier.getList();
    private FilteredList<Supplier> filteredSuppliersList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblSupplierID.setVisible(false);
        btnClearSelection.setDisable(true);
        btnSupplierCommand.setDisable(true);

        ObservableList<String> supplierCommandOptions = FXCollections.observableArrayList("Add", "Update", "Delete");
        comboBoxSupplierCommand.setItems(supplierCommandOptions);
        comboBoxSupplierCommand.setValue("Add");

        txtFirstName.setOnKeyReleased(this::handleFirstNameKeyReleased);
        txtLastName.setOnKeyReleased(this::handleLastNameKeyReleased);
        txtContactEmail.setOnKeyReleased(this::handleEmailKeyReleased);
        txtPhoneNumber.setOnKeyReleased(this::handlePhoneNumberKeyReleased);

        txtFirstName.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        txtLastName.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        txtContactEmail.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        txtPhoneNumber.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        txtAreaAddress.textProperty().addListener((observable, oldValue, newValue) -> validateFields());

        columnSupplierID.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        columnName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        columnEmail.setCellValueFactory(cellData -> cellData.getValue().contactEmailProperty());
        columnPhoneNumber.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        columnAddress.setCellValueFactory(cellData -> cellData.getValue().addressProperty());

        filteredSuppliersList = new FilteredList<>(suppliersList, p -> true);
        tableViewSuppliers.setItems(filteredSuppliersList);
        txtSupplierSearch.textProperty().addListener((observable, oldValue, newValue) -> filterSupplierList());

        tableViewSuppliers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> populateFields(newValue));

        comboBoxSupplierCommand.valueProperty().addListener((observable, oldValue, newValue) -> updateSupplierCommand(newValue));
    }

    private void filterSupplierList() {
        String searchText = txtSupplierSearch.getText().toLowerCase().trim();

        filteredSuppliersList.setPredicate(supplier -> {
            if (searchText.isEmpty()) {
                return true;
            }

            return String.valueOf(supplier.ID).contains(searchText)
                    || supplier.getName().toLowerCase().contains(searchText)
                    || supplier.getContactEmail().toLowerCase().contains(searchText)
                    || supplier.getPhoneNumber().contains(searchText)
                    || supplier.getAddress().toLowerCase().contains(searchText);
        });
    }

    private void updateSupplierCommand(String command) {
        Supplier supplier = null;
        String phoneNumber = txtPhoneNumber.getText();
        String email = txtContactEmail.getText();

        if(isSupplierSelected()){
            supplier = tableViewSuppliers.getSelectionModel().getSelectedItem();
        }

        switch (command) {
            case "Add":
                btnSupplierCommand.setText("Add Supplier");
                btnSupplierCommand.setOnAction(event -> addSupplier());
                lblSupplierID.setVisible(false);
                txtContactEmail.setOnKeyReleased(this::handleEmailKeyReleased);
                txtPhoneNumber.setOnKeyReleased(this::handlePhoneNumberKeyReleased);

                if (DataBaseManager.isSupplierPhoneNumTaken(phoneNumber)) {
                    lblPhoneNumError.setText("Phone Number is Taken");
                }
                else {
                    lblPhoneNumError.setText("");
                }

                if (DataBaseManager.isSupplierEmailTaken(email)) {
                    lblEmailError.setText("Email is Taken");
                }
                else if(email.length() > 100){
                    lblEmailError.setText("Email is too long (over 100 characters)");
                }
                else {
                    lblEmailError.setText("");
                }

                validateFields();
                break;
            case "Update":
                btnSupplierCommand.setText("Update Supplier");
                btnSupplierCommand.setOnAction(event -> updateSupplier());
                txtContactEmail.setOnKeyReleased(this::handleEmailKeyReleasedUpdate);
                txtPhoneNumber.setOnKeyReleased(this::handlePhoneNumberKeyReleasedUpdate);
                lblSupplierID.setVisible(true);

                if (Supplier.isValidPhoneNumber(phoneNumber)) {
                    if(supplier != null){
                        if (DataBaseManager.isSupplierPhoneNumTaken(phoneNumber) && !supplier.getPhoneNumber().equals(phoneNumber)) {
                            lblPhoneNumError.setText("Phone Number is Taken");
                        }
                        else {
                            lblPhoneNumError.setText("");
                        }
                    }
                    else{
                        if (DataBaseManager.isSupplierPhoneNumTaken(phoneNumber)) {
                            lblPhoneNumError.setText("Phone Number is Taken");
                        }
                        else {
                            lblPhoneNumError.setText("");
                        }
                    }
                }

                if(Supplier.isValidEmail(email)) {
                    if(supplier != null){
                        if (DataBaseManager.isSupplierEmailTaken(email) && !supplier.getContactEmail().equals(email)) {
                            lblEmailError.setText("Email is Taken");
                        }
                        else if(email.length() > 100){
                            lblEmailError.setText("Email is too long (over 100 characters)");
                        }
                        else {
                            lblEmailError.setText("");
                        }
                    }
                    else{
                        if (DataBaseManager.isSupplierEmailTaken(email)) {
                            lblEmailError.setText("Email is Taken");
                        }
                        else if(email.length() > 100){
                            lblEmailError.setText("Email is too long (over 100 characters)");
                        }
                        else {
                            lblEmailError.setText("");
                        }
                    }
                }

                validateFields();
                break;
            case "Delete":
                btnSupplierCommand.setText("Delete Supplier");
                btnSupplierCommand.setOnAction(event -> deleteSupplier());
                txtContactEmail.setOnKeyReleased(this::handleEmailKeyReleasedUpdate);
                txtPhoneNumber.setOnKeyReleased(this::handlePhoneNumberKeyReleasedUpdate);
                lblSupplierID.setVisible(true);

                if (Supplier.isValidPhoneNumber(phoneNumber)) {
                    if(supplier != null){
                        if (DataBaseManager.isSupplierPhoneNumTaken(phoneNumber) && !supplier.getPhoneNumber().equals(phoneNumber)) {
                            lblPhoneNumError.setText("Phone Number is Taken");
                        }
                        else {
                            lblPhoneNumError.setText("");
                        }
                    }
                    else{
                        if (DataBaseManager.isSupplierPhoneNumTaken(phoneNumber)) {
                            lblPhoneNumError.setText("Phone Number is Taken");
                        }
                        else {
                            lblPhoneNumError.setText("");
                        }
                    }
                }

                if(Supplier.isValidEmail(email)) {
                    if(supplier != null){
                        if (DataBaseManager.isSupplierEmailTaken(email) && !supplier.getContactEmail().equals(email)) {
                            lblEmailError.setText("Email is Taken");
                        }
                        else if(email.length() > 100){
                            lblEmailError.setText("Email is too long (over 100 characters)");
                        }
                        else {
                            lblEmailError.setText("");
                        }
                    }
                    else{
                        if (DataBaseManager.isSupplierEmailTaken(email)) {
                            lblEmailError.setText("Email is Taken");
                        }
                        else if(email.length() > 100){
                            lblEmailError.setText("Email is too long (over 100 characters)");
                        }
                        else {
                            lblEmailError.setText("");
                        }
                    }
                }

                validateFields();
                break;
        }
    }

    private void handleFirstNameKeyReleased(KeyEvent keyEvent){
        nameHandle(txtFirstName, lblFirstNameError);
    }

    private void handleLastNameKeyReleased(KeyEvent keyEvent){
        nameHandle(txtLastName, lblLastNameError);
    }

    private void nameHandle(TextField txtName, Label lblNameError) {
        String name = txtName.getText();

        if(Supplier.isValidName(name)){
            if(name.length() > 50){
                lblNameError.setText("Name Should not be longer that 50 characters");
            }
            else{
                lblNameError.setText("");
            }
        }
        else if(name.isEmpty()){
            lblNameError.setText("Text Field cannot be empty");
        }
        else{
            lblNameError.setText("Invalid Name");
        }

        validateFields();
    }

    private void handleEmailKeyReleasedUpdate(KeyEvent keyEvent) {
        if(!isSupplierSelected()){
            return;
        }

        Supplier supplier = tableViewSuppliers.getSelectionModel().getSelectedItem();
        String email = txtContactEmail.getText();

        if (Supplier.isValidEmail(email)) {
            if (DataBaseManager.isSupplierEmailTaken(email) && !supplier.getContactEmail().equals(email)) {
                lblEmailError.setText("Email is Taken");
            }
            else if(email.length() > 100){
                lblEmailError.setText("Email is too long (over 100 characters)");
            }
            else {
                lblEmailError.setText("");
            }
        }
        else if (email.isEmpty()) {
            lblEmailError.setText("Text Field cannot be empty");
        }
        else {
            lblEmailError.setText("Invalid email format");
        }

        validateFields();
    }

    private void handlePhoneNumberKeyReleasedUpdate(KeyEvent keyEvent) {
        if(!isSupplierSelected()){
            return;
        }

        Supplier supplier = tableViewSuppliers.getSelectionModel().getSelectedItem();
        String phoneNumber = txtPhoneNumber.getText();

        if (Supplier.isValidPhoneNumber(phoneNumber)) {
            if (DataBaseManager.isSupplierPhoneNumTaken(phoneNumber) && !supplier.getPhoneNumber().equals(phoneNumber)) {
                lblPhoneNumError.setText("Phone Number is Taken");
            }
            else {
                lblPhoneNumError.setText("");
            }
        } else if (phoneNumber.isEmpty()) {
            lblPhoneNumError.setText("Text Field cannot be empty");
        } else {
            lblPhoneNumError.setText("Invalid phone number");
        }

        validateFields();
    }

    private void handleEmailKeyReleased(KeyEvent keyEvent) {
        String email = txtContactEmail.getText();

        if (Supplier.isValidEmail(email)) {
            if (DataBaseManager.isSupplierEmailTaken(email)) {
                lblEmailError.setText("Email is Taken");
            }
            else if(email.length() > 100){
                lblEmailError.setText("Email is too long (over 100 characters)");
            }
            else {
                lblEmailError.setText("");
            }
        }
        else if (email.isEmpty()) {
            lblEmailError.setText("Text Field cannot be empty");
        }
        else {
            lblEmailError.setText("Invalid email format");
        }

        validateFields();
    }

    private void handlePhoneNumberKeyReleased(KeyEvent keyEvent) {
        String phoneNumber = txtPhoneNumber.getText();

        if (Supplier.isValidPhoneNumber(phoneNumber)) {
            if (DataBaseManager.isSupplierPhoneNumTaken(phoneNumber)) {
                lblPhoneNumError.setText("Phone Number is Taken");
            }
            else {
                lblPhoneNumError.setText("");
            }
        } else if (phoneNumber.isEmpty()) {
            lblPhoneNumError.setText("Text Field cannot be empty");
        } else {
            lblPhoneNumError.setText("Invalid phone number");
        }

        validateFields();
    }

    private boolean isSupplierSelected() {
        return tableViewSuppliers.getSelectionModel().getSelectedItem() != null;
    }

    private void validateFields() {
        if (comboBoxSupplierCommand.getValue().equals("Add")) {
            btnSupplierCommand.setDisable(!allFieldsValidForAdd());
        }
        else if (comboBoxSupplierCommand.getValue().equals("Update")) {
            btnSupplierCommand.setDisable(!allFieldsValidForUpdate());
        }
        else{
            btnSupplierCommand.setDisable(!allFieldsValidForDelete());
        }
    }

    private boolean allFieldsValidForAdd(){
        return Supplier.isValidName(txtFirstName.getText())
                && Supplier.isValidName(txtLastName.getText())
                && Supplier.isValidEmail(txtContactEmail.getText())
                && !DataBaseManager.isSupplierEmailTaken(txtContactEmail.getText())
                && Supplier.isValidPhoneNumber(txtPhoneNumber.getText())
                && !DataBaseManager.isSupplierPhoneNumTaken(txtPhoneNumber.getText())
                && lblFirstNameError.getText().isEmpty()
                && lblLastNameError.getText().isEmpty()
                && lblEmailError.getText().isEmpty()
                && lblPhoneNumError.getText().isEmpty();
    }

    private boolean allFieldsValidForUpdate(){
        if (!isSupplierSelected()) {
            System.out.println("No Supplier selected.");
            return false;
        }

        Supplier supplier = tableViewSuppliers.getSelectionModel().getSelectedItem();

        String firstName = supplier.getFirstName();
        String lastName = supplier.getLastName();
        String email = supplier.getContactEmail();
        String phoneNumber = supplier.getPhoneNumber();
        String address = supplier.getAddress();

        if (txtFirstName.getText().equals(firstName)
                && txtLastName.getText().equals(lastName)
                && txtContactEmail.getText().equals(email)
                && txtPhoneNumber.getText().equals(phoneNumber)
                && txtAreaAddress.getText().equals(address)) {
            System.out.println("No fields have been changed.");
            return false;
        }

        return Supplier.isValidName(txtFirstName.getText())
                && Supplier.isValidName(txtLastName.getText())
                && Supplier.isValidEmail(txtContactEmail.getText())
                && !(DataBaseManager.isSupplierEmailTaken(txtContactEmail.getText()) && !email.equals(txtContactEmail.getText()))
                && Supplier.isValidPhoneNumber(txtPhoneNumber.getText())
                && !(DataBaseManager.isSupplierPhoneNumTaken(txtPhoneNumber.getText()) && !phoneNumber.equals(txtPhoneNumber.getText()))
                && lblFirstNameError.getText().isEmpty()
                && lblLastNameError.getText().isEmpty()
                && lblEmailError.getText().isEmpty()
                && lblPhoneNumError.getText().isEmpty();
    }

    private boolean allFieldsValidForDelete(){
        if(!isSupplierSelected()){
            return false;
        }

        Supplier supplier = tableViewSuppliers.getSelectionModel().getSelectedItem();

        String firstName = supplier.getFirstName();
        String lastName = supplier.getLastName();
        String email = supplier.getContactEmail();
        String phoneNumber = supplier.getPhoneNumber();
        String address = supplier.getAddress();

        return txtFirstName.getText().equals(firstName) && txtLastName.getText().equals(lastName)
                && txtContactEmail.getText().equals(email) && txtPhoneNumber.getText().equals(phoneNumber)
                && txtAreaAddress.getText().equals(address);
    }

    public void clearFields() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtContactEmail.setText("");
        txtPhoneNumber.setText("");
        txtAreaAddress.setText("");
        lblFirstNameError.setText("");
        lblLastNameError.setText("");
        lblEmailError.setText("");
        lblPhoneNumError.setText("");
    }

    public void clearSelection() {
        tableViewSuppliers.getSelectionModel().clearSelection();
        txtFirstName.setText("");
        txtLastName.setText("");
        txtContactEmail.setText("");
        txtPhoneNumber.setText("");
        txtAreaAddress.setText("");
        lblFirstNameError.setText("");
        lblLastNameError.setText("");
        lblEmailError.setText("");
        lblPhoneNumError.setText("");
        btnClearSelection.setDisable(true);

        lblSupplierID.setText("Supplier ID: ");
    }

    public void addSupplier() {
        String name = txtFirstName.getText().trim() + " " + txtLastName.getText().trim();
        String contactEmail = txtContactEmail.getText().trim();
        String phoneNumber = txtPhoneNumber.getText().trim();
        String address = txtAreaAddress.getText().trim();

        DataBaseManager.addSupplier(name, contactEmail, phoneNumber, address);
        int id = DataBaseManager.getLastSupplierID();
        System.out.println("Supplier ID is: " + id);

        if(btnClearSelection.isDisable()){
            clearFields();
        }
        else{
            clearSelection();
        }

        if(Supplier.contains(id)){
            MyAlert.showAlert(Alert.AlertType.INFORMATION, "Add Supplier",
                    Supplier.get(id).getFirstName() + " " + Supplier.get(id).getLastName() + " with ID: " + id + " has been added.");
        }
    }

    public void updateSupplier() {
        Supplier selectedSupplier = tableViewSuppliers.getSelectionModel().getSelectedItem();

        if (selectedSupplier != null) {
            DataBaseManager.updateSupplier(
                    selectedSupplier,
                    txtFirstName.getText().trim() + " " + txtLastName.getText().trim(),
                    txtContactEmail.getText().trim(),
                    txtPhoneNumber.getText().trim(),
                    txtAreaAddress.getText().trim());

            tableViewSuppliers.refresh();
            validateFields();
            MyAlert.showAlert(Alert.AlertType.INFORMATION, "Update Supplier",
                    "Supplier with ID: " + selectedSupplier.ID + " has been updated.");
        }
    }

    public void deleteSupplier() {
        Supplier selectedSupplier = tableViewSuppliers.getSelectionModel().getSelectedItem();
        if (MyAlert.confirmationDialogAlertIsYes("Delete Supplier?",
                "Are you sure you want to delete this Supplier?\nSupplier ID: " + selectedSupplier.ID)) {
            DataBaseManager.deleteSupplier(selectedSupplier);

            if(!Supplier.contains(selectedSupplier.ID)){
                clearSelection();

                MyAlert.showAlert(Alert.AlertType.INFORMATION, "Deleted Supplier",
                        "Supplier with ID: " + selectedSupplier.ID + " has been deleted.");
            }
        }
    }

    public void populateFields(Supplier supplier){
        if (supplier != null) {
            txtFirstName.setText(supplier.getFirstName());
            txtLastName.setText(supplier.getLastName());
            txtContactEmail.setText(supplier.getContactEmail());
            txtPhoneNumber.setText(supplier.getPhoneNumber());
            txtAreaAddress.setText(supplier.getAddress());
            lblFirstNameError.setText("");
            lblLastNameError.setText("");

            btnClearSelection.setDisable(false);

            lblSupplierID.setText("Supplier ID: " + supplier.ID);

            if(btnSupplierCommand.getText().equals("Update Supplier")){
                btnSupplierCommand.setDisable(true);
                lblEmailError.setText("");
                lblPhoneNumError.setText("");
            }
            else if(btnSupplierCommand.getText().equals("Delete Supplier")){
                btnSupplierCommand.setDisable(false);
                lblEmailError.setText("");
                lblPhoneNumError.setText("");
            }
            else{
                lblEmailError.setText("Email is Taken");
                lblPhoneNumError.setText("Phone Number is Taken");
            }
        }
    }
}
