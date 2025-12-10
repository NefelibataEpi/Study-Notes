package main.java.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public final class ControllerManager {
    private ControllerManager() {};

    // show error message
    public static void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    // show alert
    public static void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
