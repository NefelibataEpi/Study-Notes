package main.java.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.model.Movie;
import main.java.model.recommendation.RecommendationStrategy;
import main.java.model.recommendation.advanced.DecadeRating;
import main.java.model.recommendation.advanced.GenreRating;
import main.java.model.recommendation.basic.DecadeRecommendation;
import main.java.model.recommendation.basic.GenreRecommendation;
import main.java.model.recommendation.basic.RatingRecommendation;
import main.java.model.user.BasicUser;
import main.java.model.user.PremiumUser;
import main.java.util.ControllerManager;
import main.java.util.DataManager;

import java.util.List;

public class RecommendationController extends MainController{
    // Button
    @FXML private Button genreButton;
    @FXML private Button decadeButton;
    @FXML private Button ratingButton;
    @FXML private Button advancedGenreButton;
    @FXML private Button advancedDecadeButton;
    @FXML private Button refreshButton;
    @FXML private Button addToWatchlistButton;
    @FXML private Button markAsWatchedButton;

    // Label
    @FXML private Label currentStrategyLabel;
    @FXML private Label errorLabel;
    @FXML private Label numberLabel;

    // Table
    @FXML private TableView<Movie> movieTableView;
    @FXML private TableColumn<Movie, String> colID;
    @FXML private TableColumn<Movie, String> colTitle;
    @FXML private TableColumn<Movie, String> colGenre;
    @FXML private TableColumn<Movie, String> colYear;
    @FXML private TableColumn<Movie, String> colRating;

    @FXML private TextField recommendationNumField;

    private RecommendationStrategy currentStrategy;

    @FXML
    public void initialize(){
        super.loadLeftData();
        // visible
        errorLabel.setVisible(false);
        if (currentUser instanceof BasicUser) {
            numberLabel.setVisible(false);
            recommendationNumField.setVisible(false);
            advancedGenreButton.setVisible(false);
            advancedDecadeButton.setVisible(false);
            refreshButton.setVisible(false);
        }

        currentStrategy = new GenreRecommendation();

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));

        getRecommendList(5);

        // text
        currentStrategyLabel.setText("Current Strategy: Genre");
    }

    @FXML
    public void addToWatchlistAction(){
        Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();

        if (selectedMovie == null){
            ControllerManager.showError(errorLabel, "Please select a movie first!");
            return;
        }

        String movieId = selectedMovie.getId();

        if (currentUser.hasInWatchlist(movieId)){
            ControllerManager.showError(errorLabel, "This movie is already in the watchlist!");
            return;
        }

        currentUser.addWatchlist(movieId);
        DataManager.refreshUserCSV(allUsers);
        ControllerManager.showSuccessAlert("Success!", "Movie added to watchlist!");
    }

    @FXML
    public void markAsWatchedAction() {
        Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();

        if (selectedMovie == null){
            ControllerManager.showError(errorLabel, "Please select a movie first!");
            return;
        }

        String movieId = selectedMovie.getId();

        currentUser.addHistory(movieId);
        DataManager.refreshUserCSV(allUsers);
        ControllerManager.showSuccessAlert("Success!", "Movie marked as watched!");
    }

    @FXML
    public void genreAction() {
        currentStrategy = new GenreRecommendation();
        currentStrategyLabel.setText("Current Strategy: Genre");

        int number = 5;
        if (currentUser instanceof PremiumUser) {
            number = readRecommendationNum();
            if (number <= 0) {
                number = 5;
            }
        }
        getRecommendList(number);
    }

    @FXML
    public void decadeAction() {
        currentStrategy = new DecadeRecommendation();
        currentStrategyLabel.setText("Current Strategy: Decade");

        int number = 5;
        if (currentUser instanceof PremiumUser) {
            number = readRecommendationNum();
            if (number <= 0) {
                number = 5;
            }
        }
        getRecommendList(number);
    }

    @FXML
    public void ratingAction() {
        currentStrategy = new RatingRecommendation();
        currentStrategyLabel.setText("Current Strategy: Rating");

        int number = 5;
        if (currentUser instanceof PremiumUser) {
            number = readRecommendationNum();
            if (number <= 0) {
                number = 5;
            }
        }
        getRecommendList(number);
    }

    @FXML
    public void advancedGenreAction() {
        currentStrategy = new GenreRating();
        currentStrategyLabel.setText("Current Strategy: Advanced Genre");

        int number = readRecommendationNum();
        if (number <= 0) {
            number = 5;
        }
        getRecommendList(number);
    }

    @FXML
    public void advancedDecadeAction(){
        currentStrategy = new DecadeRating();
        currentStrategyLabel.setText("Current Strategy: Advanced Decade");

        int number = readRecommendationNum();
        if (number <= 0) {
            number = 5;
        }
        getRecommendList(number);
    }

    @FXML
    public void refreshAction(){
        int number = readRecommendationNum();
        if (number <= 0) {
            number = 5;
        }
        getRecommendList(number);
    }

    private void getRecommendList(int number) {
        List<Movie> recommendList = currentStrategy.recommend(currentUser, number);
        ObservableList<Movie> movieList = FXCollections.observableList(recommendList);
        movieTableView.setItems(movieList);
    }

    private int readRecommendationNum() {
        String number = recommendationNumField.getText().trim();

        String regex = "[0-9]+";
        if (number.matches(regex)) {
            return Integer.parseInt(number);
        }

        return -1;
    }
}
