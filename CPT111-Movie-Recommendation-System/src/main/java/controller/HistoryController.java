package main.java.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.model.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HistoryController extends MainController{
    // Label
    @FXML private Label totalMovies;
    @FXML private Label selectedLabel;
    @FXML private Label timesLabel;

    // Table
    @FXML private TableView<Movie> movieTableView;
    @FXML private TableColumn<Movie, String> colID;
    @FXML private TableColumn<Movie, String> colTitle;
    @FXML private TableColumn<Movie, String> colGenre;
    @FXML private TableColumn<Movie, String> colYear;
    @FXML private TableColumn<Movie, String> colRating;

    // List
    @FXML private ListView<String> movieListView;

    private Map<Movie, List<String>> history;

    @FXML
    public void initialize(){
        super.loadLeftData();

        history = currentUser.getHistory();
        List<Movie> historyList = new ArrayList<>();
        Set<Map.Entry<Movie, List<String>>> entries = history.entrySet();
        for (Map.Entry<Movie, List<String>> entry : entries) {
            Movie movie = entry.getKey();
            historyList.add(movie);
        }

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));

        ObservableList<Movie> movieList = FXCollections.observableList(historyList);
        movieTableView.setItems(movieList);

        totalMovies.setText("Total: " + history.size() + " movies");

        movieTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Movie>() {
            @Override
            public void changed(ObservableValue<? extends Movie> observableValue, Movie movie, Movie t1) {
                if (t1 != null) {
                    showMovieDetails(t1);
                }
            }
        });
    }

    private void showMovieDetails(Movie movie) {
        List<String> times = history.get(movie);

        selectedLabel.setText("Selected Movie: " + movie.getTitle());
        timesLabel.setText("Watched: " + times.size() + " time(s)");

        movieListView.setItems(FXCollections.observableList(times));
    }
}
