package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import main.java.model.user.PremiumUser;

public class HomeController extends MainController{

    // right side
    @FXML private Label welcomeLabel;
    @FXML private Label userTypeLabel;
    @FXML private Label watchlistCountLabel;
    @FXML private Label historyCountLabel;

    @FXML
    public void initialize(){
        if (currentUser != null){
            loadUserData();
        }
    }

    public void loadUserData() {
        super.loadLeftData();

        // state
        String username = currentUser.getUsername();
        int watchlistSize = currentUser.getWatchlist().size();
        int historySize = currentUser.getHistory().size();

        // define text
        if (currentUser instanceof PremiumUser) {
            userTypeLabel.setText("Premium");
        } else {
            userTypeLabel.setText("Basic");
        }

        welcomeLabel.setText("Welcome back, " + username + " !");

        watchlistCountLabel.setText("Watchlist Count: " + watchlistSize + " movies");
        historyCountLabel.setText("History Count: " + historySize + " movies");
    }
}
