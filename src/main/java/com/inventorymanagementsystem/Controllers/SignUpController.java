package com.inventorymanagementsystem.Controllers;

import com.inventorymanagementsystem.Config.DataBaseManager;
import com.inventorymanagementsystem.Models.Model;
import com.inventorymanagementsystem.Models.User;
import com.inventorymanagementsystem.Utils.MyAlert;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    public Button btnSignUp, btnDBConnection;
    public TextField txtName, txtEmail, txtEmailPassword, txtPassword, txtConfirmPassword;
    public Label lblNameError, lblEmailError;
    public CheckBox chkEmailPasswordVisible, chkPasswordVisible;
    public PasswordField pwdEmailPassword, pwdPassword, pwdConfirmPassword;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtEmailPassword.setVisible(false);
        txtConfirmPassword.setVisible(false);
        txtPassword.setVisible(false);
        btnSignUp.setDisable(true);

        txtName.setOnKeyReleased(this::handleNameKeyReleased);
        txtEmail.setOnKeyReleased(this::handleEmailKeyReleased);

        txtName.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        txtConfirmPassword.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        pwdPassword.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        pwdConfirmPassword.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
    }

    private void handleNameKeyReleased(KeyEvent keyEvent) {
        String name = txtName.getText().trim();

        if(User.isValidName(name)){
            if(name.length() > 50){
                lblNameError.setText("Name Should not be longer that 50 characters");
            }
            else{
                lblNameError.setText("");
            }
        }
        else{
            lblNameError.setText("Invalid Name");
        }

        validateFields();
    }

    private void handleEmailKeyReleased(KeyEvent keyEvent) {
        String email = txtEmail.getText().trim();

        if(User.isValidEmail(email)){
            lblEmailError.setText("");
        }
        else{
            lblEmailError.setText("Invalid Email");
        }

        validateFields();
    }

    private void validateFields() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = (txtPassword.isVisible()) ? txtPassword.getText() : pwdPassword.getText();
        String confirmPassword = (txtConfirmPassword.isVisible()) ? txtConfirmPassword.getText() : pwdConfirmPassword.getText();

        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            btnSignUp.setDisable(true);
            return;
        }

        if(!lblNameError.getText().isEmpty() || !lblEmailError.getText().isEmpty()){
            btnSignUp.setDisable(true);
            return;
        }

        btnSignUp.setDisable(false);
    }

    public void signUp(){
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String emailPassword = (txtEmailPassword.isVisible()) ? txtEmailPassword.getText() : pwdEmailPassword.getText();
        String password = (txtPassword.isVisible()) ? txtPassword.getText() : pwdPassword.getText();
        String confirmPassword = (txtConfirmPassword.isVisible()) ? txtConfirmPassword.getText() : pwdConfirmPassword.getText();

        if(!password.equals(confirmPassword)){
            MyAlert.showAlert(AlertType.ERROR, "Password doesn't match", "Password and Confirm Password do not match...");
            return;
        }

        if(password.length() < 8){
            MyAlert.showAlert(AlertType.ERROR, "Password too short", "Password must be at least 8 characters long");
            return;
        }

        DataBaseManager.addUser(name, password, email);
        int id = DataBaseManager.getLastUserID();
        DataBaseManager.addAdmin(id, (emailPassword.isEmpty()) ? "" : emailPassword);
        Stage stage = (Stage) lblNameError.getScene().getWindow();
        MyAlert.showAlert(AlertType.INFORMATION, "Successfully created User", "User was created successfully");
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showLoginWindow();
        MyAlert.showAlert(AlertType.INFORMATION, "Login Information", "Your Login information is:\n" +
                "ID Number: " + id + "\nEmail: " + email + "\nPassword: <password you entered>");
    }

    public void backToDBConnection(ActionEvent actionEvent) {
        DataBaseManager.removeInfo();
        Stage stage = (Stage) lblNameError.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showDataBaseConnectionWindow();
    }

    public void passwordVisible(ActionEvent actionEvent) {
        if(chkPasswordVisible.isSelected()){
            txtPassword.setText(pwdPassword.getText());
            txtConfirmPassword.setText(pwdConfirmPassword.getText());
            pwdPassword.setVisible(false);
            pwdConfirmPassword.setVisible(false);
            txtPassword.setVisible(true);
            txtConfirmPassword.setVisible(true);
        }
        else{
            pwdPassword.setText(txtPassword.getText());
            pwdConfirmPassword.setText(txtConfirmPassword.getText());
            pwdPassword.setVisible(true);
            pwdConfirmPassword.setVisible(true);
            txtPassword.setVisible(false);
            txtConfirmPassword.setVisible(false);
        }
    }

    public void emailPasswordVisible(ActionEvent actionEvent) {
        if(chkEmailPasswordVisible.isSelected()){
            txtEmailPassword.setText(pwdEmailPassword.getText());
            pwdEmailPassword.setVisible(false);
            txtEmailPassword.setVisible(true);
        }
        else{
            pwdEmailPassword.setText(txtEmailPassword.getText());
            pwdEmailPassword.setVisible(true);
            txtEmailPassword.setVisible(false);
        }
    }
}
