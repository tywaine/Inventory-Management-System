package com.inventorymanagementsystem.Controllers.Admin;

import com.inventorymanagementsystem.Config.DataBaseManager;
import com.inventorymanagementsystem.Models.Model;
import com.inventorymanagementsystem.Models.User;
import com.inventorymanagementsystem.Utils.MyAlert;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountController implements Initializable {
    public TextField txtName, txtEmail, txtOldPassword, txtConfirmPassword, txtNewPassword, txtEmailPassword;
    public Button btnUpdate, btnVisibleOldPwd, btnVisibleConfirmPwd, btnVisibleNewPwd, btnSubmit;
    public PasswordField pwdOldPassword, pwdConfirmPassword, pwdNewPassword, pwdEmailPassword;
    public CheckBox chkBoxShowEmailPwd;
    public Label lblNameError, lblEmailError, lblOldPasswordError, lblConfirmPasswordError, lblNewPasswordError, lblPasswordError;
    public Label lblCreatedAt, lblUserId;

    private final User user = Model.getInstance().getCurrentUser();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtEmailPassword.setVisible(false);
        txtOldPassword.setVisible(false);
        txtConfirmPassword.setVisible(false);
        txtNewPassword.setVisible(false);
        btnUpdate.setDisable(true);
        btnSubmit.setDisable(true);
        lblUserId.setText(lblUserId.getText() + user.ID);
        lblCreatedAt.setText(lblCreatedAt.getText() + user.getCreatedAtFormatted());

        txtName.setText(user.getName());
        txtEmail.setText(user.getEmail());

        txtName.setOnKeyReleased(this::handleNameKeyReleased);
        txtEmail.setOnKeyReleased(this::handleEmailKeyReleased);
        txtNewPassword.setOnKeyReleased(this::handleNewPasswordKeyReleased);
        pwdNewPassword.setOnKeyReleased(this::handleNewPasswordKeyReleased);
        txtOldPassword.setOnKeyReleased(this::handleOldPasswordKeyReleased);
        pwdOldPassword.setOnKeyReleased(this::handleOldPasswordKeyReleased);
        txtConfirmPassword.setOnKeyReleased(this::handleConfirmPasswordKeyReleased);
        pwdConfirmPassword.setOnKeyReleased(this::handleConfirmPasswordKeyReleased);

        txtName.textProperty().addListener((observable, oldValue, newValue) -> validateDetailFields());
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> validateDetailFields());
        txtEmailPassword.textProperty().addListener((observable, oldValue, newValue) -> validateDetailFields());
        pwdEmailPassword.textProperty().addListener((observable, oldValue, newValue) -> validateDetailFields());

        txtOldPassword.textProperty().addListener((observable, oldValue, newValue) -> validatePasswordFields());
        txtConfirmPassword.textProperty().addListener((observable, oldValue, newValue) -> validatePasswordFields());
        txtNewPassword.textProperty().addListener((observable, oldValue, newValue) -> validatePasswordFields());
        pwdOldPassword.textProperty().addListener((observable, oldValue, newValue) -> validatePasswordFields());
        pwdConfirmPassword.textProperty().addListener((observable, oldValue, newValue) -> validatePasswordFields());
        pwdConfirmPassword.textProperty().addListener((observable, oldValue, newValue) -> validatePasswordFields());

        btnVisibleOldPwd.setOnAction(event -> {
            changeVisibility(txtOldPassword, pwdOldPassword, btnVisibleOldPwd);
        });

        btnVisibleConfirmPwd.setOnAction(event -> {
            changeVisibility(txtConfirmPassword, pwdConfirmPassword, btnVisibleConfirmPwd);
        });

        btnVisibleNewPwd.setOnAction(event -> {
            changeVisibility(txtNewPassword, pwdNewPassword, btnVisibleNewPwd);
        });
    }

    private void validateDetailFields() {
        btnUpdate.setDisable(!allFieldsValidForUpdate());
    }

    private void validatePasswordFields(){
        btnSubmit.setDisable(!allFieldsValidForPassword());
    }

    private boolean allFieldsValidForUpdate() {
        String name = user.getName();
        String email = user.getEmail();
        String emailPassword = txtEmailPassword.isVisible() ? txtEmailPassword.getText() : pwdEmailPassword.getText();

        if (txtName.getText().equals(name) && txtEmail.getText().equals(email) && emailPassword.isEmpty()) {
            System.out.println("No fields have been changed.");
            return false;
        }

        return User.isValidName(txtName.getText())
                && User.isValidEmail(txtEmail.getText())
                && !(DataBaseManager.userEmailExists(txtEmail.getText()) && !email.equals(txtEmail.getText()))
                && !emailPassword.isEmpty()
                && lblNameError.getText().isEmpty()
                && lblEmailError.getText().isEmpty();
    }

    private boolean allFieldsValidForPassword(){
        String oldPassword = (txtOldPassword.isVisible()) ? txtOldPassword.getText() : pwdOldPassword.getText();
        String confirmPassword = (txtConfirmPassword.isVisible()) ? txtConfirmPassword.getText() : pwdConfirmPassword.getText();
        String newPassword = (txtNewPassword.isVisible()) ? txtNewPassword.getText() : pwdNewPassword.getText();

        return User.isValidPasswordLength(oldPassword) && User.isValidPasswordLength(confirmPassword)
                && User.isValidPasswordLength(newPassword) && lblOldPasswordError.getText().isEmpty()
                && lblConfirmPasswordError.getText().isEmpty();
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

        validateDetailFields();
    }

    private void handleEmailKeyReleased(KeyEvent keyEvent) {
        String email = txtEmail.getText();

        if (User.isValidEmail(email)) {
            if (DataBaseManager.userEmailExists(email) && !user.getEmail().equals(email)) {
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

        validateDetailFields();
    }

    private void handleNewPasswordKeyReleased(KeyEvent keyEvent){
        passwordValidity(pwdNewPassword, txtNewPassword, lblNewPasswordError);
    }

    private void handleOldPasswordKeyReleased(KeyEvent keyEvent){
        passwordValidity(pwdOldPassword, txtOldPassword, lblOldPasswordError);
    }

    private void handleConfirmPasswordKeyReleased(KeyEvent keyEvent){
        passwordValidity(pwdConfirmPassword, txtConfirmPassword, lblConfirmPasswordError);
    }

    private void passwordValidity(PasswordField pwdConfirmPassword, TextField txtConfirmPassword, Label lblPasswordError) {
        String password = (pwdConfirmPassword.isVisible()) ? pwdConfirmPassword.getText() : txtConfirmPassword.getText();

        if(User.isValidPasswordLength(password) || password.isEmpty()){
            lblPasswordError.setText("");
        }
        else{
            lblPasswordError.setText("Password should be between 7 and 51 characters");
        }

        validatePasswordFields();
    }

    private void changeVisibility(TextField textField, PasswordField passwordField, Button button){
        if(textField.isVisible()){
            button.setText("Show");
            passwordField.setText(textField.getText());
            textField.setVisible(false);
            passwordField.setVisible(true);
        }
        else{
            button.setText("Hide");
            textField.setText(passwordField.getText());
            passwordField.setVisible(false);
            textField.setVisible(true);
        }
    }

    public void EmailPasswordVisible() {
        if (chkBoxShowEmailPwd.isSelected()) {
            txtEmailPassword.setText(pwdEmailPassword.getText());
            pwdEmailPassword.setVisible(false);
            txtEmailPassword.setVisible(true);
        } else {
            pwdEmailPassword.setText(txtEmailPassword.getText());
            pwdEmailPassword.setVisible(true);
            txtEmailPassword.setVisible(false);
        }
    }

    public void changePasswordSubmit(ActionEvent actionEvent) {
        String oldPassword = (txtOldPassword.isVisible()) ? txtOldPassword.getText() : pwdOldPassword.getText();
        String confirmOldPassword = (txtConfirmPassword.isVisible()) ? txtConfirmPassword.getText() : pwdConfirmPassword.getText();
        String newPassword = (txtNewPassword.isVisible()) ? txtNewPassword.getText() : pwdNewPassword.getText();

        if(DataBaseManager.validUser(user.getEmail(), oldPassword)){
            if(oldPassword.equals(confirmOldPassword)){
                DataBaseManager.updateUser(user, txtName.getText(), newPassword, txtEmail.getText());
                txtOldPassword.setText("");
                txtConfirmPassword.setText("");
                txtNewPassword.setText("");
                pwdOldPassword.setText("");
                pwdConfirmPassword.setText("");
                pwdNewPassword.setText("");
                btnSubmit.setDisable(true);
                MyAlert.showAlert(Alert.AlertType.INFORMATION, "Change Password", "Password was successfully changed");
                return;
            }
            else{
                lblConfirmPasswordError.setText("Confirm Password and old Password is not the same");
            }
        }
        else{
            lblOldPasswordError.setText("Password is Incorrect");

            if(!oldPassword.equals(confirmOldPassword)){
                lblConfirmPasswordError.setText("Confirm Password and old Password is not the same");
            }
        }

        btnSubmit.setDisable(true);
    }

    public void updateDetails(){
        User.update(user, txtName.getText(), txtEmail.getText());
        String emailPassword = txtEmailPassword.isVisible() ? txtEmailPassword.getText() : pwdEmailPassword.getText();

        if(emailPassword.isEmpty()){
            MyAlert.showAlert(Alert.AlertType.INFORMATION, "Updated Details", "Your Details were successfully updated");
        }
        else{
            DataBaseManager.updateAdmin(user, emailPassword);
            MyAlert.showAlert(Alert.AlertType.INFORMATION, "Updated Details", "Your Details were successfully updated\n" +
                    "Email Password was also updated");
        }

        txtEmailPassword.setText("");
        pwdEmailPassword.setText("");
        btnUpdate.setDisable(true);
    }
}
