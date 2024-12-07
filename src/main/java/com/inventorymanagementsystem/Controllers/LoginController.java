package com.inventorymanagementsystem.Controllers;

import com.inventorymanagementsystem.Config.DataBaseManager;
import com.inventorymanagementsystem.Models.Model;
import com.inventorymanagementsystem.Enums.PreferenceKeys;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import javafx.scene.control.*;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LoginController implements Initializable {
    @FXML
    public TextField txtIdentity, txtVisiblePassword;
    @FXML
    public PasswordField pwdPassword;
    @FXML
    public Button btnLogin, btnVisible;
    @FXML
    public Label lblError;
    @FXML
    public CheckBox chkSaveCredentials;

    private final FontIcon visibleIcon = createFontIcon(FontAwesomeSolid.EYE);
    private final FontIcon invisibleIcon = createFontIcon(FontAwesomeSolid.EYE_SLASH);
    private static final Preferences preferences = Preferences.userNodeForPackage(LoginController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnVisible.setGraphic(visibleIcon);
        txtVisiblePassword.setManaged(false);
        txtVisiblePassword.setVisible(false);
        txtVisiblePassword.textProperty().bindBidirectional(pwdPassword.textProperty());
        btnVisible.setOnAction(event -> togglePasswordVisibility());
    }

    private FontIcon createFontIcon(FontAwesomeSolid iconType) {
        FontIcon icon = new FontIcon(iconType);
        icon.getStyleClass().add("icon");
        return icon;
    }

    public void login(){
        String identity = txtIdentity.getText();
        String password = (pwdPassword.isVisible()) ? pwdPassword.getText() : txtVisiblePassword.getText();

        if(identity.isEmpty() || password.isEmpty()){
            lblError.setText("Text field/s cannot be empty");
        }
        else if(DataBaseManager.userIdentityExist(identity)){
            if(DataBaseManager.validUserIdentity(identity, password)){
                Model.getInstance().setCurrentUser(DataBaseManager.getUserFromIdentity(identity));
                String role = Model.getInstance().getCurrentUser().getRole();
                lblError.setText("");
                System.out.println("User was found in the database: " + role);
                Stage stage = (Stage) lblError.getScene().getWindow();
                showCorrectWindow(identity, password, stage, role);
            }
            else{
                lblError.setText("Password is incorrect");
            }
        }
        else{
            lblError.setText("Email or Id Number does not exist");
        }
    }

    private void togglePasswordVisibility() {
        if (txtVisiblePassword.isVisible()) {
            btnVisible.setGraphic(visibleIcon);

            txtVisiblePassword.setManaged(false);
            txtVisiblePassword.setVisible(false);
            pwdPassword.setManaged(true);
            pwdPassword.setVisible(true);
        }
        else {
            btnVisible.setGraphic(invisibleIcon);

            txtVisiblePassword.setManaged(true);
            txtVisiblePassword.setVisible(true);
            pwdPassword.setManaged(false);
            pwdPassword.setVisible(false);
        }
    }

    public static void removeCredentials(){
        preferences.put(PreferenceKeys.USER_IDENTITY.getKey(), "");
        preferences.put(PreferenceKeys.USER_PASSWORD.getKey(), "");
    }

    public void shouldShow() {
        String identity = preferences.get(PreferenceKeys.USER_IDENTITY.getKey(), "");
        String password = preferences.get(PreferenceKeys.USER_PASSWORD.getKey(), "");
        Stage stage = (Stage) lblError.getScene().getWindow();

        if(DataBaseManager.userIdentityExist(identity) && DataBaseManager.validUserIdentity(identity, password)){
            Model.getInstance().setCurrentUser(DataBaseManager.getUserFromIdentity(identity));
            String role = Model.getInstance().getCurrentUser().getRole();
            System.out.println("User was found in the database: " + role);
            lblError.setText("");
            showCorrectWindow(identity, password, stage, role);
        }
        else{
            removeCredentials();
            stage.show();
        }
    }

    private void showCorrectWindow(String email, String password, Stage stage, String role) {
        Model.getInstance().getViewFactory().closeStage(stage);

        if(role.equals("ADMIN")){
            if(chkSaveCredentials.isSelected()){
                preferences.put(PreferenceKeys.USER_IDENTITY.getKey(), email);
                preferences.put(PreferenceKeys.USER_PASSWORD.getKey(), password);
            }

            Model.getInstance().getViewFactory().showAdminWindow();
        }
        else{
            if(role.equals("STAFF")){
                if(chkSaveCredentials.isSelected()){
                    preferences.put(PreferenceKeys.USER_IDENTITY.getKey(), email);
                    preferences.put(PreferenceKeys.USER_PASSWORD.getKey(), password);
                }

                Model.getInstance().getViewFactory().showStaffWindow();
            }
            else{
                Model.getInstance().showAlert(AlertType.ERROR, "There is no User other than Admin and Staff", "How did this happen?????");
                System.exit(1);
            }
        }
    }

}
