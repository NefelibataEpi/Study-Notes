package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.java.commend.Login;
import main.java.exception.InvalidCharacterException;
import main.java.exception.LengthException;
import main.java.exception.UsernameFormatException;
import main.java.model.user.User;
import main.java.util.ControllerManager;
import main.java.util.DataManager;

import java.io.IOException;
import java.util.List;

public class ForgetPasswordController {
    // Button
    @FXML private Button submitButton;
    @FXML private Button backButton;

    // Field
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;

    // Label
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    public void submitButtonAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm = confirmField.getText().trim();
        List<User> allUsers = DataManager.getAllUsers();

        // check isEmpty
        if (username.isEmpty()) {
            ControllerManager.showError(errorLabel, "Username cannot be empty!");
            return;
        }
        if (password.isEmpty()) {
            ControllerManager.showError(errorLabel, "Password cannot be empty!");
            return;
        }
        if (confirm.isEmpty()) {
            ControllerManager.showError(errorLabel, "Please confirm your password!");
            return;
        }

        // check both password
        if (!password.equals(confirm)) {
            ControllerManager.showError(errorLabel, "Passwords do not match!");
            return;
        }

        // check if the username is existed
        if (!Login.containUsername(allUsers, username)) {
            ControllerManager.showError(errorLabel, "Account not found! Please register first");
            return;
        }



        // check username and password format
        try {
            Login.mainCheck(password);
            Login.checkUsername(username);
        } catch (LengthException | InvalidCharacterException | UsernameFormatException e) {
            ControllerManager.showError(errorLabel, e.getMessage());
            return;
        }

        // find the user
        int index = Login.findIndex(allUsers, username);
        User user = allUsers.get(index);

        // set new password
        user.setPassword(password);

        DataManager.refreshUserCSV(allUsers);

        ControllerManager.showSuccessAlert("Success!", "Your password has been updated!");

        backLogin();
    }

    @FXML
    public void backButtonAction() {
        backLogin();
    }

    private void backLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login System");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
