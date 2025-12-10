package main.java.model.user;

import main.java.model.Movie;

import java.util.List;
import java.util.Map;

public class BasicUser extends User {
    // constructor
    public BasicUser(String username, String password, String watchlist, String history) {
        super(username, password, watchlist, history);
    }
    public BasicUser(String username, String password) {
        super(username, password);
    }
    public BasicUser(String username, String password, List<Movie> watchlist, Map<Movie, List<String>> history) {
        super(username, password, watchlist, history);
    }
}
