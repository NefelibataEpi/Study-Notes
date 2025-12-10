package main.java.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.model.Movie;
import main.java.util.ControllerManager;
import main.java.util.DataManager;

import java.util.List;

public class AllMoviesController extends MainController{
    // Button
    @FXML private Button addToWatchlistButton;
    @FXML private Button markAsWatchedButton;

    // Label
    @FXML private Label totalMovies;
    @FXML private Label errorLabel;

    // Table
    @FXML private TableView<Movie> movieTableView;
    @FXML private TableColumn<Movie, String> colID;
    @FXML private TableColumn<Movie, String> colTitle;
    @FXML private TableColumn<Movie, String> colGenre;
    @FXML private TableColumn<Movie, String> colYear;
    @FXML private TableColumn<Movie, String> colRating;

    @FXML
    public void initialize(){
        super.loadLeftData();
        errorLabel.setVisible(false);

        List<Movie> allMovies = DataManager.getAllMovies();

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));

        ObservableList<Movie> movieList = FXCollections.observableList(allMovies);
        movieTableView.setItems(movieList);

        totalMovies.setText("Total: " + allMovies.size() + " movies");
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
    public void markAsWatchedAction(){
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
}
