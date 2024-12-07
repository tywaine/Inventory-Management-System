package com.inventorymanagementsystem.Controllers;

import com.inventorymanagementsystem.Config.DataBaseManager;
import com.inventorymanagementsystem.Utils.EncryptionUtils;
import com.inventorymanagementsystem.Models.Model;
import com.inventorymanagementsystem.Enums.PreferenceKeys;
import com.inventorymanagementsystem.Utils.MyAlert;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javafx.scene.control.Alert.AlertType;

import java.net.URL;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class DBConnectionController implements Initializable {
    public TextField txtHost, txtPort, txtUser;
    public ChoiceBox<String> choiceBoxAuth, choiceBoxSavePwd;
    public PasswordField password;
    public ComboBox<String> comboBoxDB;
    public Button btnTestConnection, btnContinue;
    public Label lblUser, lblPassword, lblSave, lblDataBase;
    private Preferences preferences = Preferences.userNodeForPackage(DBConnectionController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choiceBoxAuth.getItems().addAll("User & Password", "No Authentication");
        choiceBoxAuth.setValue("User & Password");

        choiceBoxSavePwd.getItems().addAll("Forever", "Forget");
        choiceBoxSavePwd.setValue("Forever");

        loadCredentials();
        loadDatabaseNames();

        choiceBoxAuth.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("No Authentication".equals(newValue)) {
                comboBoxDB.setLayoutY(175);

                txtUser.setVisible(false);
                password.setVisible(false);
                choiceBoxSavePwd.setVisible(false);
                lblUser.setText("Database: ");
                lblPassword.setVisible(false);
                lblSave.setVisible(false);
                lblDataBase.setVisible(false);
            }
            else {
                comboBoxDB.setLayoutY(275);

                txtUser.setVisible(true);
                password.setVisible(true);
                choiceBoxSavePwd.setVisible(true);
                lblUser.setText("User: ");
                lblPassword.setVisible(true);
                lblSave.setVisible(true);
                lblDataBase.setVisible(true);
            }
        });
    }

    public void shouldShow() {
        Stage stage = (Stage) txtUser.getScene().getWindow();

        String Url = "jdbc:mysql://" + txtHost.getText() + ':' + txtPort.getText() + "/" + comboBoxDB.getValue();
        String username = (txtUser.isVisible()) ? txtUser.getText() : "";
        String pwd = (password.isVisible()) ? password.getText() : "";

        try{
            Connection connection = DriverManager.getConnection(Url, username, pwd);
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getDataBaseDriver().setConnection(connection);

            if(DataBaseManager.doesAdminExists()){
                DataBaseManager.loadInfo();
                Model.getInstance().getViewFactory().loginWindow();
            }
            else{
                DataBaseManager.ensureTablesExist();
                showSignUpWindow();
            }
        } catch (SQLException _) {
            LoginController.removeCredentials();
            stage.show();
        }
    }

    private void saveCredentials() {
        preferences.put(PreferenceKeys.DB_HOST.getKey(), txtHost.getText());
        preferences.put(PreferenceKeys.DB_PORT.getKey(), txtPort.getText());
        preferences.put(PreferenceKeys.DB_AUTH.getKey(), choiceBoxAuth.getValue());

        if (choiceBoxAuth.getValue().equals("User & Password")) {
            preferences.put(PreferenceKeys.DB_USERNAME.getKey(), txtUser.getText());

            try {
                // Encrypt the password before storing it
                String encryptedPassword = EncryptionUtils.encrypt(password.getText());
                preferences.put(PreferenceKeys.DB_PASSWORD.getKey(), encryptedPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }

            preferences.put(PreferenceKeys.DB_SAVE_PASSWORD.getKey(), choiceBoxSavePwd.getValue());
        } else {
            preferences.put(PreferenceKeys.DB_USERNAME.getKey(), "");
            preferences.put(PreferenceKeys.DB_PASSWORD.getKey(), "");
            preferences.put(PreferenceKeys.DB_SAVE_PASSWORD.getKey(), "");
        }

        preferences.put(PreferenceKeys.DB_DATABASE.getKey(), comboBoxDB.getValue());
    }

    private void clearPassword() {
        preferences.remove(PreferenceKeys.DB_PASSWORD.getKey());
    }

    private void loadCredentials() {
        String host = preferences.get(PreferenceKeys.DB_HOST.getKey(), "");
        String port = preferences.get(PreferenceKeys.DB_PORT.getKey(), "");
        String auth = preferences.get(PreferenceKeys.DB_AUTH.getKey(), "");
        String username = preferences.get(PreferenceKeys.DB_USERNAME.getKey(), "");
        String encryptedPwd = preferences.get(PreferenceKeys.DB_PASSWORD.getKey(), "");
        String savePass = preferences.get(PreferenceKeys.DB_SAVE_PASSWORD.getKey(), "");
        String database = preferences.get(PreferenceKeys.DB_DATABASE.getKey(), "");

        if (!host.isEmpty()) {
            txtHost.setText(host);
        }

        if (!port.isEmpty()) {
            txtPort.setText(port);
        }

        if (!auth.isEmpty()) {
            choiceBoxAuth.setValue(auth);
        }

        if (!username.isEmpty()) {
            txtUser.setText(username);
        }

        if (!encryptedPwd.isEmpty()) {
            try {
                // Decrypt the password before using it
                String decryptedPassword = EncryptionUtils.decrypt(encryptedPwd);
                password.setText(decryptedPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!savePass.isEmpty()) {
            choiceBoxSavePwd.setValue(savePass);
        }

        if (!database.isEmpty()) {
            comboBoxDB.setValue(database);
        }
    }

    private void loadDatabaseNames() {
        String currentVal = comboBoxDB.getValue();
        comboBoxDB.getItems().clear();
        comboBoxDB.setValue(currentVal);
        String url = "jdbc:mysql://" + txtHost.getText() + ':' + txtPort.getText() + "/";
        String username = (txtUser.isVisible()) ? txtUser.getText() : "";
        String pwd = (password.isVisible()) ? password.getText() : "";

        try (Connection connection = DriverManager.getConnection(url, username, pwd);
             ResultSet resultSet = connection.getMetaData().getCatalogs()) {

            while (resultSet.next()) {
                String dbName = resultSet.getString(1);

                if(!dbName.equals("sys") && !dbName.equals("information_schema") &&
                        !dbName.equals("performance_schema") && !dbName.equals("mysql")){
                    comboBoxDB.getItems().add(dbName); // Add database name to ComboBox
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void testConnection(){
        String databaseName = (comboBoxDB.getValue() == null) ? "" : comboBoxDB.getValue();

        String url = "jdbc:mysql://" + txtHost.getText() + ':' + txtPort.getText() + "/" + databaseName;
        String username = (txtUser.isVisible()) ? txtUser.getText() : "";
        String pwd = (password.isVisible()) ? password.getText() : "";

        try(Connection connection = DriverManager.getConnection(url, username, pwd)){
            loadDatabaseNames();
            MyAlert.showAlert(Alert.AlertType.INFORMATION, "Connection Successful", "Connection to the database was successful");
        } catch (SQLException e) {
            MyAlert.showAlert(Alert.AlertType.ERROR, "Connection Unsuccessful", "Connection to the database was unsuccessful\n" + e.getMessage());
        }
    }

    public void handleContinue(){
        String url = "jdbc:mysql://" + txtHost.getText() + ':' + txtPort.getText() + "/" + comboBoxDB.getValue();
        String username = (txtUser.isVisible()) ? txtUser.getText() : "";
        String pwd = (password.isVisible()) ? password.getText() : "";

        try{
            Connection connection = DriverManager.getConnection(url, username, pwd);
            Model.getInstance().getDataBaseDriver().setConnection(connection);
            saveCredentials();

            if(!choiceBoxSavePwd.getValue().equals("Forever")) {
                clearPassword();
            }

            Stage stage = (Stage) txtUser.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            DataBaseManager.ensureTablesExist();
            DataBaseManager.loadInfo();

            if(DataBaseManager.doesAdminExists()){
                Model.getInstance().getViewFactory().showLoginWindow();
            }
            else{
                showSignUpWindow();
            }
        } catch (SQLException e) {
            url = "jdbc:mysql://" + txtHost.getText() + ':' + txtPort.getText() + "/";

            try{
                Connection connection = DriverManager.getConnection(url, username, pwd);

                if (MyAlert.confirmationDialogAlertIsYes("Connection Successful but database doesn't exist",
                        "The connection was successful but the database doesn't exist. Do you wish to create it?")) {
                    createDatabase(connection, comboBoxDB.getValue());
                    url = "jdbc:mysql://" + txtHost.getText() + ':' + txtPort.getText() + "/" + comboBoxDB.getValue();
                    connection = DriverManager.getConnection(url, username, pwd);
                    Model.getInstance().getDataBaseDriver().setConnection(connection);
                    DataBaseManager.ensureTablesExist();
                    Stage stage = (Stage) txtUser.getScene().getWindow();
                    Model.getInstance().getViewFactory().closeStage(stage);
                    saveCredentials();

                    if(!choiceBoxSavePwd.getValue().equals("Forever")) {
                        clearPassword();
                    }

                    showSignUpWindow();
                }
            }catch(SQLException t){
                MyAlert.showAlert(Alert.AlertType.ERROR, "Connection Unsuccessful", "Connection to the database was unsuccessful\n" + e.getMessage());
            }}
    }

    public void showSignUpWindow(){
        Model.getInstance().getViewFactory().showSignUpWindow();
        MyAlert.showAlert(AlertType.INFORMATION, "First time creating a User account",
                """
                        Since there is no user registered in the system you will create an Account.
                        Make sure to remember the details, specifically the EMAIL and obviously the PASSWORD.
                        You will need them to login. THERE IS NO RECOVERY SYSTEM (As of now)
                        You can change the password later if you like""");
    }

    public void createDatabase(Connection connection, String dbName) {
        String query = "CREATE DATABASE " + dbName;

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            MyAlert.showAlert(AlertType.INFORMATION, "Database created Successfully", "Database created successfully!!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String generateSecretKey(int keyLength) {
        byte[] secretKey = new byte[keyLength];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(secretKey);

        return Base64.getEncoder().encodeToString(secretKey);
    }
}
