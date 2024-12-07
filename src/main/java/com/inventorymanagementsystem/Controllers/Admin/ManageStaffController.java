package com.inventorymanagementsystem.Controllers.Admin;

import com.inventorymanagementsystem.Config.DataBaseManager;
import com.inventorymanagementsystem.Models.*;
import com.inventorymanagementsystem.Utils.MyEmail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageStaffController implements Initializable {
    public TableView<User> tableViewStaff;
    public TableColumn<User, Integer> columnStaffID;
    public TableColumn<User, String> columnName, columnEmail;
    public TableColumn<User, String> columnCreatedAt;
    public TextField txtName, txtEmail, txtPassword;
    public PasswordField pwdPassword;
    public TextField txtStaffSearch;
    public CheckBox chkBoxShowPassword;
    public Label lblNameError, lblEmailError, lblPasswordError, lblStaffID;
    public ComboBox<String> comboBoxStaffCommand;
    public Button btnStaffCommand, btnClearSelection, btnClearFields;

    private final ObservableList<User> staffList = User.getStaffList();
    private FilteredList<User> filteredStaffList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtPassword.setVisible(false);
        lblStaffID.setVisible(false);
        btnClearSelection.setDisable(true);
        btnStaffCommand.setDisable(true);

        ObservableList<String> staffCommandOptions = FXCollections.observableArrayList("Add", "Update", "Delete");
        comboBoxStaffCommand.setItems(staffCommandOptions);
        comboBoxStaffCommand.setValue("Add");

        txtName.setOnKeyReleased(this::handleNameKeyReleased);
        txtEmail.setOnKeyReleased(this::handleEmailKeyReleased);
        txtPassword.setOnKeyReleased(this::handlePasswordKeyReleased);
        pwdPassword.setOnKeyReleased(this::handlePasswordKeyReleased);

        txtName.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        pwdPassword.textProperty().addListener((observable, oldValue, newValue) -> validateFields());

        columnStaffID.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        columnName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        columnEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        columnCreatedAt.setCellValueFactory(cellData -> cellData.getValue().createdAtFormattedProperty());

        filteredStaffList = new FilteredList<>(staffList, p -> true);
        tableViewStaff.setItems(filteredStaffList);
        txtStaffSearch.textProperty().addListener((observable, oldValue, newValue) -> filterStaffList());
        tableViewStaff.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> populateFields(newValue));

        comboBoxStaffCommand.valueProperty().addListener((observable, oldValue, newValue) -> updateStaffCommand(newValue));
    }

    private void filterStaffList() {
        String searchText = txtStaffSearch.getText().toLowerCase().trim();

        filteredStaffList.setPredicate(staff -> {
            if (searchText.isEmpty()) {
                return true;
            }

            return String.valueOf(staff.ID).contains(searchText)
                    || staff.getName().contains(searchText)
                    || staff.getEmail().toLowerCase().contains(searchText)
                    || String.valueOf(staff.getCreatedAtFormatted()).contains(searchText);
        });
    }

    private void updateStaffCommand(String command) {
        User staff = null;
        String email = txtEmail.getText();

        if(isStaffSelected()){
            staff = tableViewStaff.getSelectionModel().getSelectedItem();
        }

        switch (command) {
            case "Add":
                btnStaffCommand.setText("Add Staff");
                btnStaffCommand.setOnAction(event -> addStaff());
                lblStaffID.setVisible(false);
                txtEmail.setOnKeyReleased(this::handleEmailKeyReleased);
                txtPassword.setDisable(false);
                pwdPassword.setDisable(false);
                lblPasswordError.setVisible(true);

                if (DataBaseManager.userEmailExists(email)) {
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
                btnStaffCommand.setText("Update Staff");
                btnStaffCommand.setOnAction(event -> updateStaff());
                txtEmail.setOnKeyReleased(this::handleEmailKeyReleasedUpdate);
                lblStaffID.setVisible(true);
                txtPassword.setDisable(false);
                pwdPassword.setDisable(false);
                lblPasswordError.setVisible(true);

                if(User.isValidEmail(email)) {
                    if(staff != null){
                        if(DataBaseManager.userEmailExists(email) && !staff.getEmail().equals(email)) {
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
                        if (DataBaseManager.userEmailExists(email)) {
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
                btnStaffCommand.setText("Delete Staff");
                btnStaffCommand.setOnAction(event -> deleteStaff());
                txtEmail.setOnKeyReleased(this::handleEmailKeyReleasedUpdate);
                lblStaffID.setVisible(true);
                txtPassword.setDisable(true);
                pwdPassword.setDisable(true);
                lblPasswordError.setVisible(false);

                if(User.isValidEmail(email)) {
                    if(staff != null){
                        if (DataBaseManager.userEmailExists(email) && !staff.getEmail().equals(email)) {
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
                        if (DataBaseManager.userEmailExists(email)) {
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

    private void handleNameKeyReleased(KeyEvent keyEvent){
        String name = txtName.getText();

        if(User.isValidName(name)){
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

    private void handleEmailKeyReleased(KeyEvent keyEvent) {
        String email = txtEmail.getText();

        if (User.isValidEmail(email)) {
            if (DataBaseManager.userEmailExists(email)) {
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

    private void handleEmailKeyReleasedUpdate(KeyEvent keyEvent) {
        if(!isStaffSelected()){
            return;
        }

        User staff = tableViewStaff.getSelectionModel().getSelectedItem();
        String email = txtEmail.getText();

        if (User.isValidEmail(email)) {
            if (DataBaseManager.userEmailExists(email) && !staff.getEmail().equals(email)) {
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

    private void handlePasswordKeyReleased(KeyEvent keyEvent){
        String password = (pwdPassword.isVisible()) ? pwdPassword.getText() : txtPassword.getText();

        if(User.isValidPasswordLength(password) || password.isEmpty()){
            lblPasswordError.setText("");
        }
        else{
            lblPasswordError.setText("Password should be between 7 and 51 characters");
        }

        validateFields();
    }

    private boolean isStaffSelected() {
        return tableViewStaff.getSelectionModel().getSelectedItem() != null;
    }

    public void passwordVisible() {
        if (chkBoxShowPassword.isSelected()) {
            txtPassword.setText(pwdPassword.getText());
            pwdPassword.setVisible(false);
            txtPassword.setVisible(true);
        } else {
            pwdPassword.setText(txtPassword.getText());
            pwdPassword.setVisible(true);
            txtPassword.setVisible(false);
        }
    }

    private void validateFields() {
        if (comboBoxStaffCommand.getValue().equals("Add")) {
            btnStaffCommand.setDisable(!allFieldsValidForAdd());
        }
        else if (comboBoxStaffCommand.getValue().equals("Update")) {
            btnStaffCommand.setDisable(!allFieldsValidForUpdate());
        }
        else{
            btnStaffCommand.setDisable(!allFieldsValidForDelete());
        }
    }

    private boolean allFieldsValidForAdd(){
        String password = (pwdPassword.isVisible()) ? pwdPassword.getText() : txtPassword.getText();

        return User.isValidName(txtName.getText())
                && User.isValidEmail(txtEmail.getText())
                && !DataBaseManager.userEmailExists(txtEmail.getText())
                && User.isValidPasswordLength(password)
                && lblNameError.getText().isEmpty()
                && lblEmailError.getText().isEmpty()
                && lblPasswordError.getText().isEmpty();
    }

    private boolean allFieldsValidForUpdate(){
        if (!isStaffSelected()) {
            System.out.println("No Staff selected.");
            return false;
        }

        User staff = tableViewStaff.getSelectionModel().getSelectedItem();
        String name = staff.getName();
        String email = staff.getEmail();
        String password = (pwdPassword.isVisible()) ? pwdPassword.getText() : txtPassword.getText();

        if (txtName.getText().equals(name) && txtEmail.getText().equals(email) && password.isEmpty()) {
            System.out.println("No fields have been changed.");
            return false;
        }

        if(!password.isEmpty() && !User.isValidPasswordLength(password)){
            return false;
        }

        return User.isValidName(txtName.getText())
                && User.isValidEmail(txtEmail.getText())
                && !(DataBaseManager.userEmailExists(txtEmail.getText()) && !email.equals(txtEmail.getText()))
                && lblNameError.getText().isEmpty()
                && lblEmailError.getText().isEmpty()
                && lblPasswordError.getText().isEmpty();
    }

    private boolean allFieldsValidForDelete(){
        if(!isStaffSelected()){
            return false;
        }

        User staff = tableViewStaff.getSelectionModel().getSelectedItem();

        String name = staff.getName();
        String email = staff.getEmail();

        return txtName.getText().equals(name) && txtEmail.getText().equals(email);
    }

    public void clearSelection() {
        tableViewStaff.getSelectionModel().clearSelection();
        txtName.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        pwdPassword.setText("");
        lblNameError.setText("");
        lblEmailError.setText("");
        lblPasswordError.setText("");
        btnClearSelection.setDisable(true);

        lblStaffID.setText("Staff ID: ");
    }

    public void clearFields() {
        txtName.setText("");
        txtEmail.setText("");
        lblNameError.setText("");
        lblEmailError.setText("");
    }

    public void addStaff() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.isVisible() ? txtPassword.getText() : pwdPassword.getText();
        DataBaseManager.addUser(name, "STAFF", password, email);
        int id = DataBaseManager.getLastUserID();
        System.out.println("Staff ID is: " + id);

        if(btnClearSelection.isDisable()){
            clearFields();
        }
        else{
            clearSelection();
            txtPassword.setText("");
            pwdPassword.setText("");
            lblPasswordError.setText("");
        }

        if(User.containsStaff(id)){
            Model.getInstance().showAlert(Alert.AlertType.INFORMATION, "Add Staff",
                    User.getStaff(id).getName() + " with ID: " + id + " has been added.");
        }

        User admin = Model.getInstance().getCurrentUser();
        String adminEmailPassword = DataBaseManager.getAdminEmailPassword(admin);
        MyEmail.sendEmail(
                admin.getEmail(),
                adminEmailPassword,
                email,
                "Inventory Management System: Added Staff Member",
                "You have been added to the Inventory Management System\n" +
                        "Id Number: " + DataBaseManager.getLastUserID() + "\nPassword: " + password);
    }

    public void updateStaff() {
        User selectedStaff = tableViewStaff.getSelectionModel().getSelectedItem();
        String password = txtPassword.isVisible() ? txtPassword.getText() : pwdPassword.getText();

        if(selectedStaff != null){
            if(!password.isEmpty()){
                Alert alert = Model.getInstance().getConfirmationDialogAlert("Update Staff?",
                        "Are you sure you want to change the password for this staff?\nStaff ID: " + selectedStaff.ID);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    DataBaseManager.updateUser(
                            selectedStaff,
                            txtName.getText().trim(),
                            password,
                            txtEmail.getText().trim());

                    User admin = Model.getInstance().getCurrentUser();
                    String adminEmailPassword = DataBaseManager.getAdminEmailPassword(admin);
                    MyEmail.sendEmail(
                            admin.getEmail(),
                            adminEmailPassword,
                            selectedStaff.getEmail().trim(),
                            "Inventory Management System: Updated Staff Login Credentials",
                            "Your information have been updated in the Inventory Management System\n" +
                                    "Id Number: " + selectedStaff.ID + "\nPassword: " + password);

                }
                else{
                    return;
                }
            }
            else{
                DataBaseManager.updateUser(
                        selectedStaff,
                        txtName.getText().trim(),
                        txtEmail.getText().trim());
            }

            tableViewStaff.refresh();
            txtPassword.setText("");
            pwdPassword.setText("");
            lblPasswordError.setText("");
            validateFields();
            Model.getInstance().showAlert(Alert.AlertType.INFORMATION, "Updated Staff",
                    "Staff with ID: " + selectedStaff.ID + " has been updated.");
        }
    }

    public void deleteStaff() {
        User selectedStaff = tableViewStaff.getSelectionModel().getSelectedItem();
        Alert alert = Model.getInstance().getConfirmationDialogAlert("Delete Staff?",
                "Are you sure you want to delete this Staff?\nStaff ID: " + selectedStaff.ID);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            DataBaseManager.deleteStaff(selectedStaff);

            if(!User.containsStaff(selectedStaff.ID)){
                clearSelection();

                Model.getInstance().showAlert(Alert.AlertType.INFORMATION, "Deleted Staff",
                        "Staff with ID: " + selectedStaff.ID + " has been deleted.");

                User admin = Model.getInstance().getCurrentUser();
                String adminEmailPassword = DataBaseManager.getAdminEmailPassword(admin);
                MyEmail.sendEmail(
                        admin.getEmail(),
                        adminEmailPassword,
                        selectedStaff.getEmail().trim(),
                        "Inventory Management System: Updated Staff Login Credentials",
                        "ID Number: " + selectedStaff.ID + "\nYour information have been deleted from the Inventory Management System\n" +
                                "You can no longer login");

            }
        }
    }

    public void populateFields(User staff){
        if (staff != null) {
            txtName.setText(staff.getName());
            txtEmail.setText(staff.getEmail());

            btnClearSelection.setDisable(false);

            lblStaffID.setText("Staff ID: " + staff.ID);

            if(btnStaffCommand.getText().equals("Update Staff")){
                btnStaffCommand.setDisable(true);
                lblEmailError.setText("");
            }
            else if(btnStaffCommand.getText().equals("Delete Staff")){
                btnStaffCommand.setDisable(false);
                lblEmailError.setText("");
            }
            else{
                lblEmailError.setText("Email is Taken");
            }
        }
    }
}
