package main.java.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.model.Movie;
import main.java.util.ControllerManager;
import main.java.util.DataManager;

import java.util.List;

public class WatchlistController extends MainController{
    // Button
    @FXML private Button removeButton;
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

        List<Movie> watchlist = currentUser.getWatchlist();

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));

        ObservableList<Movie> movieList = FXCollections.observableList(watchlist);
        movieTableView.setItems(movieList);

        totalMovies.setText("Total: " + watchlist.size() + " movies");
    }

    @FXML
    private void removeAction(ActionEvent event) {
        Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();

        if (selectedMovie == null){
            ControllerManager.showError(errorLabel, "Please select a movie first!");
            return;
        }

        String movieId = selectedMovie.getId();

        currentUser.removeWatchlist(movieId);
        DataManager.refreshUserCSV(allUsers);
        switchToWatchlist(event);
        ControllerManager.showSuccessAlert("Success!", "Movie removed successfully!");
    }

    @FXML
    private void markAsWatchedAction(ActionEvent event) {
        Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();

        if (selectedMovie == null){
            ControllerManager.showError(errorLabel, "Please select a movie first!");
            return;
        }

        String movieId = selectedMovie.getId();

        currentUser.addHistory(movieId);
        DataManager.refreshUserCSV(allUsers);
        switchToWatchlist(event);
        ControllerManager.showSuccessAlert("Success!", "Movie marked as watched!");
    }
}
