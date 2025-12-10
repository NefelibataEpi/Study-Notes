package main.java.model.user;

import main.java.model.Movie;
import main.java.util.DataManager;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

public class User {
    // state
    private String username;
    private String password;
    private List<Movie> watchlist;
    private Map<Movie, List<String>> history;

    // constructor
    public User(){}
    // read the original User data
    public User(String username, String password, String watchlistStr, String historyStr) {
        this.username = username;
        this.password = password;

        // watchlist
        this.watchlist = new ArrayList<>();
        if (watchlistStr != null) {
            String[] watchlistArr = watchlistStr.split(";");
            for (String s : watchlistArr) {
                if (s != null && !s.trim().isEmpty()) {
                    addWatchlist(s);
                }
            }
        }


        // history
        this.history = new HashMap<>();
        if (historyStr != null) {
            String[] historyArr = historyStr.split(";");
            for (String s : historyArr) {
                String[] detailArr = s.split("@");

                Movie movie = DataManager.getMovie(detailArr[0]);
                for (int i = 1; i < detailArr.length; i++) {
                    if (this.history.containsKey(movie)) {
                        // Movie exist -> List exist
                        List<String> currentList = this.history.get(movie);
                        currentList.add(detailArr[i]);
                        this.history.put(movie, currentList);
                    } else {
                        List<String> newList = new ArrayList<>();
                        newList.add(detailArr[i]);
                        this.history.put(movie, newList);
                    }
                }
            }
        }
    }
    // create a user object only through username and password
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.watchlist = new ArrayList<>();
        this.history = new HashMap<>();
    }
    public User(String username, String password, List<Movie> watchlist, Map<Movie, List<String>> history) {
        this.username = username;
        this.password = password;
        this.watchlist = watchlist;
        this.history = history;
    }

    // setters
    public void setUsername(String username){
        this.username = username;
    }
    public void setPassword(String password){
        this.password = password;
    }

    // getters
    public String getUsername(){
        return this.username;
    }
    public String getPassword(){
        return this.password;
    }
    public List<Movie> getWatchlist() {
        return this.watchlist;
    }
    public Map<Movie, List<String>> getHistory() {
        return this.history;
    }

    // String
    @Override
    public String toString(){
        return "User{username = " + this.username + ", password = " + this.password +
                ", watchlist = " + this.watchlist + ", history = " + this.history + "}";
    }

    // behavior
    // 1. Add a movie to the watchlist by id
    public void addWatchlist(String id) {
        Movie movie = DataManager.getMovie(id);
        if (movie != null) {
            this.watchlist.add(movie);
        }
    }

    // 2. Remove the movie from the watchlist
    public void removeWatchlist(String id) {
        if (id == null || id.trim().isEmpty()) {
            return;
        }

        watchlist.removeIf(new Predicate<Movie>() {
            @Override
            public boolean test(Movie movie) {
                return movie != null && id.equals(movie.getId());
            }
        });
    }

    // 3. Add the movie to the history
    public void addHistory(String id) {
        LocalDate today = LocalDate.now();
        Movie currentMovie = DataManager.getMovie(id);

        // remove the movie from the watchlist if it is in the watchlist
        watchlist.remove(currentMovie);

        if (this.history.containsKey(currentMovie)) {
            List<String> currentList = this.history.get(currentMovie);
            currentList.add(today.toString());
            this.history.put(currentMovie, currentList);
        } else {
            List<String> newList = new ArrayList<>();
            newList.add(today.toString());
            this.history.put(currentMovie, newList);
        }
    }

    // 4. check whether the movie is in watchlist
    public boolean hasInWatchlist(String id) {
        for (Movie movie : watchlist) {
            if (movie != null && movie.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }
}
