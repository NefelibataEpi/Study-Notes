package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.commend.Login;
import main.java.model.user.User;
import main.java.util.ControllerManager;
import main.java.util.DataManager;

import java.io.IOException;
import java.util.List;

public class LoginController {
    // Button
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Button forgetPasswordButton;
    @FXML private Button refreshButton;

    // Field
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField verifyCodeField;

    // Label
    @FXML private Label codeLabel;
    @FXML private Label errorLabel;

    private String currentVerificationCode;

    @FXML
    public void initialize() {
        generateCode();
        errorLabel.setVisible(false);
    }

    @FXML
    public void loginButtonAction(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String verifyCode = verifyCodeField.getText().trim();
        List<User> allUsers = DataManager.getAllUsers();

        if (username.isEmpty()) {
            ControllerManager.showError(errorLabel, "Username cannot be empty!");
            return;
        }

        if (password.isEmpty()) {
            ControllerManager.showError(errorLabel, "Password cannot be empty!");
            return;
        }

        if (verifyCode.isEmpty()) {
            ControllerManager.showError(errorLabel, "Verification code cannot be empty!");
            return;
        }

        if (!verifyCode.equalsIgnoreCase(currentVerificationCode)) {
            ControllerManager.showError(errorLabel, "Incorrect Verification Code!");

            // generate new code
            generateCode();
            verifyCodeField.clear();
            return;
        }

        User userInfo = new User(username, password);
        User user = Login.findUser(allUsers, userInfo);
        if (user == null) {
            ControllerManager.showError(errorLabel, "Invalid username or password!");

            // clear
            usernameField.clear();
            passwordField.clear();
            verifyCodeField.clear();
            generateCode();

            return;
        };

        startMainView(user);
    }

    @FXML
    public void registerButtonAction(ActionEvent event) {
        switchScene("/view/Register.fxml");
    }

    @FXML
    public void forgetPasswordButtonAction(ActionEvent event) {
        switchScene("/view/ForgetPassword.fxml");
    }

    @FXML
    public void refreshButtonAction(ActionEvent event) {
        generateCode();
        verifyCodeField.clear();
        errorLabel.setVisible(false);
    }

    // generate verification code
    private void generateCode() {
        currentVerificationCode = Login.getCode();
        codeLabel.setText(currentVerificationCode);
    }

    // login successfully and go to the Main part
    private void startMainView(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Home.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.setCurrentUser(user);
            mainController.setAllUsers(DataManager.getAllUsers());

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Movie Recommendation System");
            stage.setResizable(false);
            stage.show();

            HomeController homeController = loader.getController();
            homeController.loadUserData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // switch to the target Scene
    private void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
