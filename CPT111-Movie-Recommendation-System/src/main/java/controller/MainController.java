package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import main.java.commend.Main;
import main.java.model.user.PremiumUser;
import main.java.model.user.User;
import main.java.util.DataManager;

import java.io.IOException;
import java.util.List;

// This class is used to switch to different Scenes in Main part
public class MainController {
    // Button
    @FXML public Button homeButton;
    @FXML public Button allMoviesButton;
    @FXML public Button watchlistButton;
    @FXML public Button historyButton;
    @FXML public Button recommendationButton;
    @FXML public Button upgradeButton;

    // left side
    @FXML public Label usernameLabel;
    @FXML public Label typeLabel;

    // user
    public static User currentUser;
    public static List<User> allUsers;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public void setAllUsers(List<User> users) {
        allUsers = users;
    }

    public void loadLeftData() {
        // state
        String username = currentUser.getUsername();
        usernameLabel.setText(username);

        if (currentUser instanceof PremiumUser) {
            typeLabel.setText("Premium");
        } else {
            typeLabel.setText("Basic");
        }

        if (currentUser instanceof PremiumUser) {
            upgradeButton.setVisible(false);
        }
    }

    @FXML
    public void switchTo(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            root = loader.load();

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToHome(ActionEvent event) {
        switchTo(event, "/view/Home.fxml");
    }

    @FXML
    public void switchToAllMovies(ActionEvent event) {
        switchTo(event, "/view/AllMovies.fxml");
    }

    @FXML
    public void switchToWatchlist(ActionEvent event) {
        switchTo(event, "/view/Watchlist.fxml");
    }

    @FXML
    public void switchToHistory(ActionEvent event) {
        switchTo(event, "/view/History.fxml");
    }

    @FXML
    public void switchToRecommendation(ActionEvent event) {
        switchTo(event, "/view/Recommendation.fxml");
    }

    @FXML
    public void upgradeButton(ActionEvent event) {
        currentUser = Main.upgradeToVIP(currentUser, allUsers);
        DataManager.refreshUserCSV(allUsers);

        // refresh current stage -> go to Home page again
        switchToHome(event);
    }
}
